package pl.polsl.courier.management.system.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import pl.polsl.courier.management.system.dto.CarDTO;
import pl.polsl.courier.management.system.entity.Car;
import pl.polsl.courier.management.system.repository.CarRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Controller
@RequestMapping("/car")
public class CarController {

    @Autowired
    CarRepository carRepo;

    @GetMapping("/{registrationNumber}")
    public ResponseEntity<EntityModel<CarDTO>> getCarByRegistrationNumber(@PathVariable String registrationNumber) {
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

    @PostMapping
    public ResponseEntity<EntityModel<CarDTO>> addCar(@RequestBody CarDTO dto) {
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

        return ResponseEntity.status(HttpStatus.CREATED).body(
                EntityModel.of(savedDto,
                        linkTo(methodOn(CarController.class).getCarByRegistrationNumber(savedDto.getRegistrationNumber())).withSelfRel()
                )
        );
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CarDTO>> updateCar(@PathVariable Long id, @RequestBody CarDTO dto) {
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
                linkTo(methodOn(CarController.class).getCarByRegistrationNumber(updatedDto.getRegistrationNumber())).withSelfRel()));
    }

    @GetMapping("/available")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getAvailableCars() {
        List<EntityModel<CarDTO>> cars = carRepo.findByRoutesIsEmpty().stream()
                .map(car -> {
                    CarDTO dto = new CarDTO(car);
                    return EntityModel.of(dto,
                            linkTo(methodOn(CarController.class).getCarByRegistrationNumber(dto.getRegistrationNumber())).withSelfRel());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
                linkTo(methodOn(CarController.class).getAvailableCars()).withSelfRel()));
    }

    @GetMapping("/occupied")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getOccupiedCars() {
        List<EntityModel<CarDTO>> cars = carRepo.findByRoutesIsNotEmpty().stream()
                .map(car -> {
                    CarDTO dto = new CarDTO(car);
                    return EntityModel.of(dto,
                            linkTo(methodOn(CarController.class).getCarByRegistrationNumber(dto.getRegistrationNumber())).withSelfRel());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
                linkTo(methodOn(CarController.class).getOccupiedCars()).withSelfRel()));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getCarsByBrand(@PathVariable String brand) {
        List<EntityModel<CarDTO>> cars = carRepo.findByBrand(brand).stream()
                .map(car -> {
                    CarDTO dto = new CarDTO(car);
                    return EntityModel.of(dto,
                            linkTo(methodOn(CarController.class).getCarByRegistrationNumber(dto.getRegistrationNumber())).withSelfRel());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
                linkTo(methodOn(CarController.class).getCarsByBrand(brand)).withSelfRel()));
    }

    @GetMapping("/model/{model}")
    public ResponseEntity<CollectionModel<EntityModel<CarDTO>>> getCarsByModel(@PathVariable String model) {
        List<EntityModel<CarDTO>> cars = carRepo.findByModel(model).stream()
                .map(car -> {
                    CarDTO dto = new CarDTO(car);
                    return EntityModel.of(dto,
                            linkTo(methodOn(CarController.class).getCarByRegistrationNumber(dto.getRegistrationNumber())).withSelfRel());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
                linkTo(methodOn(CarController.class).getCarsByModel(model)).withSelfRel()));
    }

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
                .map(car -> {
                    CarDTO dto = new CarDTO(car);
                    return EntityModel.of(dto,
                            linkTo(methodOn(CarController.class).getCarByRegistrationNumber(dto.getRegistrationNumber())).withSelfRel());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(cars,
                linkTo(methodOn(CarController.class).getCarsByCapacity(min, max)).withSelfRel()));
    }
}
