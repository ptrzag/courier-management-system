package pl.polsl.courier.management.system.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import pl.polsl.courier.management.system.dto.CarDTO;
import pl.polsl.courier.management.system.entity.Car;
import pl.polsl.courier.management.system.repository.CarRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Auta", description = "Operacje na zasobie Auta")
@RestController
@RequestMapping("/car")
public class CarController {

    @Autowired
    private CarRepository carRepo;

    @Operation(summary = "Pobierz pojazd po numerze rejestracyjnym")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pojazd znaleziony",
            content = @Content(schema = @Schema(implementation = CarDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pojazd nie znaleziony")
    })
    @GetMapping("/{registrationNumber}")
    public ResponseEntity<EntityModel<CarDTO>> getCarByRegistrationNumber(
            @PathVariable String registrationNumber) {
        Car car = carRepo.findByRegistrationNumber(registrationNumber)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Car with registration number '" + registrationNumber + "' not found"));

        CarDTO dto = new CarDTO(car);
        EntityModel<CarDTO> model = EntityModel.of(dto,
            linkTo(methodOn(CarController.class).getCarByRegistrationNumber(registrationNumber)).withSelfRel(),
            linkTo(methodOn(CarController.class).getAvailableCars()).withRel("available-cars"),
            linkTo(methodOn(CarController.class).getOccupiedCars()).withRel("occupied-cars")
        );

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Dodaj nowy pojazd")
    @ApiResponse(responseCode = "201", description = "Pojazd utworzony",
        content = @Content(schema = @Schema(implementation = CarDTO.class)))
    @PostMapping
    public ResponseEntity<EntityModel<CarDTO>> addCar(@Valid @RequestBody CarDTO dto) {
        if (carRepo.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Car with registration number '" + dto.getRegistrationNumber() + "' already exists");
        }
        Car car = new Car();
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setRegistrationNumber(dto.getRegistrationNumber());
        car.setMileage(dto.getMileage());
        car.setCapacity(dto.getCapacity());
        Car saved = carRepo.save(car);
        CarDTO savedDto = new CarDTO(saved);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(EntityModel.of(savedDto,
                linkTo(methodOn(CarController.class)
                    .getCarByRegistrationNumber(savedDto.getRegistrationNumber())).withSelfRel()
            ));
    }

    @Operation(summary = "Usuń pojazd po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pojazd usunięty"),
        @ApiResponse(responseCode = "404", description = "Pojazd nie znaleziony"),
        @ApiResponse(responseCode = "400", description = "Nie można usunąć przypisanego pojazdu")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        Car car = carRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        if (!car.getRoutes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Cannot delete car assigned to routes");
        }
        carRepo.delete(car);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Aktualizuj pojazd po ID")
    @ApiResponse(responseCode = "200", description = "Pojazd zaktualizowany",
        content = @Content(schema = @Schema(implementation = CarDTO.class)))
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CarDTO>> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarDTO dto) {
        Car car = carRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        if (!dto.getRegistrationNumber().equals(car.getRegistrationNumber())
                && carRepo.existsByRegistrationNumber(dto.getRegistrationNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Registration number '" + dto.getRegistrationNumber() + "' is already in use");
        }
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setRegistrationNumber(dto.getRegistrationNumber());
        car.setMileage(dto.getMileage());
        car.setCapacity(dto.getCapacity());
        Car updated = carRepo.save(car);
        CarDTO updatedDto = new CarDTO(updated);

        return ResponseEntity.ok(EntityModel.of(updatedDto,
            linkTo(methodOn(CarController.class)
                .getCarByRegistrationNumber(updatedDto.getRegistrationNumber())).withSelfRel()));
    }

    @Operation(summary = "Lista dostępnych pojazdów")
    @ApiResponse(responseCode = "200", description = "Zwraca listę dostępnych pojazdów",
        content = @Content(schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/available")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getAvailableCars() {
        List<EntityModel<CarDTO>> cars = carRepo.findByRoutesIsEmpty().stream()
            .map(car -> EntityModel.of(new CarDTO(car),
                linkTo(methodOn(CarController.class)
                    .getCarByRegistrationNumber(car.getRegistrationNumber())).withSelfRel()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
            linkTo(methodOn(CarController.class).getAvailableCars()).withSelfRel()));
    }

    @Operation(summary = "Lista zajętych pojazdów")
    @ApiResponse(responseCode = "200", description = "Zwraca listę zajętych pojazdów",
        content = @Content(schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/occupied")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getOccupiedCars() {
        List<EntityModel<CarDTO>> cars = carRepo.findByRoutesIsNotEmpty().stream()
            .map(car -> EntityModel.of(new CarDTO(car),
                linkTo(methodOn(CarController.class)
                    .getCarByRegistrationNumber(car.getRegistrationNumber())).withSelfRel()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
            linkTo(methodOn(CarController.class).getOccupiedCars()).withSelfRel()));
    }

    @Operation(summary = "Lista pojazdów wg marki")
    @ApiResponse(responseCode = "200", description = "Zwraca listę pojazdów o danej marce",
        content = @Content(schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/brand/{brand}")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getCarsByBrand(
            @PathVariable String brand) {
        List<EntityModel<CarDTO>> cars = carRepo.findByBrand(brand).stream()
            .map(car -> EntityModel.of(new CarDTO(car),
                linkTo(methodOn(CarController.class)
                    .getCarByRegistrationNumber(car.getRegistrationNumber())).withSelfRel()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
            linkTo(methodOn(CarController.class).getCarsByBrand(brand)).withSelfRel()));
    }

    @Operation(summary = "Lista pojazdów wg modelu")
    @ApiResponse(responseCode = "200", description = "Zwraca listę pojazdów o danym modelu",
        content = @Content(schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/model/{model}")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getCarsByModel(
            @PathVariable String model) {
        List<EntityModel<CarDTO>> cars = carRepo.findByModel(model).stream()
            .map(car -> EntityModel.of(new CarDTO(car),
                linkTo(methodOn(CarController.class)
                    .getCarByRegistrationNumber(car.getRegistrationNumber())).withSelfRel()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
            linkTo(methodOn(CarController.class).getCarsByModel(model)).withSelfRel()));
    }

    @Operation(summary = "Lista pojazdów wg pojemności")
    @ApiResponse(responseCode = "200", description = "Zwraca listę pojazdów filtrowaną po pojemności",
        content = @Content(schema = @Schema(implementation = CarDTO.class)))
    @GetMapping("/capacity")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getCarsByCapacity(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max) {
        List<Car> rawCars;
        if (min != null && max != null) {
            rawCars = carRepo.findByCapacityBetween(min, max);
        } else if (min != null) {
            rawCars = carRepo.findByCapacityGreaterThanEqual(min);
        } else if (max != null) {
            rawCars = carRepo.findByCapacityLessThanEqual(max);
        } else {
            rawCars = (List<Car>) carRepo.findAll();
        }
        List<EntityModel<CarDTO>> cars = rawCars.stream()
            .map(car -> EntityModel.of(new CarDTO(car),
                linkTo(methodOn(CarController.class)
                    .getCarByRegistrationNumber(car.getRegistrationNumber())).withSelfRel()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
            linkTo(methodOn(CarController.class).getCarsByCapacity(min, max)).withSelfRel()));
    }
}
