package pl.polsl.courier.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import pl.polsl.courier.management.system.dto.RoutePlanDTO;
import pl.polsl.courier.management.system.entity.RoutePlan;
import pl.polsl.courier.management.system.repository.CarRepository;
import pl.polsl.courier.management.system.repository.ParcelRepository;
import pl.polsl.courier.management.system.repository.RoutePlanRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "PlanyTrasy", description = "Operacje na zasobie Plan trasy")
@RestController
@RequestMapping("/route")
@Validated
public class RoutePlanController {

    @Autowired
    private RoutePlanRepository routePlanRepo;
    @Autowired
    private CarRepository carRepo;
    @Autowired
    private ParcelRepository parcelRepo;

    @Operation(summary = "Dodaj nowy plan trasy")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Plan trasy utworzony",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe")
    })
    @PostMapping
    public ResponseEntity<EntityModel<RoutePlanDTO>> addRoutePlan(
            @Valid @RequestBody RoutePlanDTO dto) {
        RoutePlan route = new RoutePlan();
        applyDto(route, dto);
        RoutePlan saved = routePlanRepo.save(route);
        return ResponseEntity
            .created(linkTo(methodOn(RoutePlanController.class)
                .getRoutePlan(saved.getId())).toUri())
            .body(toModel(saved));
    }

    @Operation(summary = "Pobierz plan trasy po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plan trasy znaleziony",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Plan trasy nie znaleziony")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RoutePlanDTO>> getRoutePlan(
            @PathVariable Long id) {
        RoutePlan route = routePlanRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Plan trasy nie znaleziony z ID: " + id
            ));
        return ResponseEntity.ok(toModel(route));
    }

    @Operation(summary = "Aktualizuj plan trasy po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plan trasy zaktualizowany",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Plan trasy nie znaleziony"),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<RoutePlanDTO>> updateRoutePlan(
            @PathVariable Long id,
            @Valid @RequestBody RoutePlanDTO dto) {
        RoutePlan route = routePlanRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Nie można zaktualizować. Plan trasy nie znaleziony z ID: " + id
            ));
        applyDto(route, dto);
        RoutePlan updated = routePlanRepo.save(route);
        return ResponseEntity.ok(toModel(updated));
    }

    @Operation(summary = "Usuń plan trasy po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Plan trasy usunięty"),
        @ApiResponse(responseCode = "404", description = "Plan trasy nie znaleziony")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutePlan(@PathVariable Long id) {
        if (!routePlanRepo.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Nie można usunąć. Plan trasy nie znaleziony z ID: " + id
            );
        }
        routePlanRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pobierz plany trasy po dacie realizacji")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listę planów trasy zwrócono",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak planów trasy dla tej daty")
    })
    @GetMapping("/date/{date}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByScheduleDate(
            @PathVariable String date) {
        LocalDate d = LocalDate.parse(date);
        List<RoutePlan> list = routePlanRepo.findByScheduleDate(d);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brak planów trasy dla daty: " + date
            );
        }
        return wrapList(list);
    }

    @Operation(summary = "Pobierz plany trasy po punkcie startowym")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listę planów trasy zwrócono",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak planów trasy dla tego punktu startowego")
    })
    @GetMapping("/start/{address}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStartLocation(
            @PathVariable String address) {
        List<RoutePlan> list = routePlanRepo.findByStartLocation(address);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brak planów trasy zaczynających się na: " + address
            );
        }
        return wrapList(list);
    }

    @Operation(summary = "Pobierz plany trasy zawierające fragment adresu startowego")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listę planów trasy zwrócono",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak planów trasy dla tego fragmentu")
    })
    @GetMapping("/start/fragment/{fragment}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStartFragment(
            @PathVariable String fragment) {
        List<RoutePlan> list = routePlanRepo.findByStartLocationContaining(fragment);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brak planów trasy zawierających start: " + fragment
            );
        }
        return wrapList(list);
    }

    @Operation(summary = "Pobierz plany trasy po punkcie końcowym")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listę planów trasy zwrócono",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak planów trasy dla tego punktu końcowego")
    })
    @GetMapping("/end/{address}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByEndLocation(
            @PathVariable String address) {
        List<RoutePlan> list = routePlanRepo.findByEndLocation(address);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brak planów trasy kończących się na: " + address
            );
        }
        return wrapList(list);
    }

    @Operation(summary = "Pobierz plany trasy zawierające fragment adresu końcowego")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listę planów trasy zwrócono",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak planów trasy dla tego fragmentu")
    })
    @GetMapping("/end/fragment/{fragment}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByEndFragment(
            @PathVariable String fragment) {
        List<RoutePlan> list = routePlanRepo.findByEndLocationContaining(fragment);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brak planów trasy zawierających koniec: " + fragment
            );
        }
        return wrapList(list);
    }

    @Operation(summary = "Pobierz plany trasy po przystanku")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listę planów trasy zwrócono",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak planów trasy dla tego przystanku")
    })
    @GetMapping("/stop/{address}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStop(
            @PathVariable String address) {
        List<RoutePlan> list = routePlanRepo.findByStops(address);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brak planów trasy zatrzymujących się na: " + address
            );
        }
        return wrapList(list);
    }

    @Operation(summary = "Pobierz plany trasy zawierające fragment przystanku")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listę planów trasy zwrócono",
            content = @Content(schema = @Schema(implementation = RoutePlanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak planów trasy dla tego fragmentu")
    })
    @GetMapping("/stop/fragment/{fragment}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStopFragment(
            @PathVariable String fragment) {
        List<RoutePlan> list = routePlanRepo.findByStopsContaining(fragment);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brak planów trasy zawierających przystanek: " + fragment
            );
        }
        return wrapList(list);
    }

    private void applyDto(RoutePlan route, RoutePlanDTO dto) {
        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());
        route.setEstimatedTime(dto.getEstimatedTime());
        route.setScheduleDate(dto.getScheduleDate());
        route.setStops(dto.getStops());
        if (dto.getCarId() != null) {
            carRepo.findById(dto.getCarId()).ifPresent(route::setCar);
        } else {
            route.setCar(null);
        }
        route.getParcel().forEach(p -> p.setRoutePlan(null));
        route.getParcel().clear();
        if (dto.getParcelIds() != null) {
            dto.getParcelIds().stream()
                .map(parcelRepo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(p -> {
                    p.setRoutePlan(route);
                    route.getParcel().add(p);
                });
        }
    }

    private EntityModel<RoutePlanDTO> toModel(RoutePlan route) {
        RoutePlanDTO dto = new RoutePlanDTO(route);
        EntityModel<RoutePlanDTO> model = EntityModel.of(dto,
            linkTo(methodOn(RoutePlanController.class)
                .getRoutePlan(route.getId())).withSelfRel()
        );
        if (route.getCar() != null) {
            model.add(linkTo(methodOn(CarController.class)
                .getCarByRegistrationNumber(route.getCar().getRegistrationNumber()))
                .withRel("car"));
        }
        dto.getParcelIds().forEach(pid ->
            model.add(linkTo(methodOn(ParcelController.class)
                .getParcel(pid)).withRel("parcel"))
        );
        return model;
    }

    private ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> wrapList(
            List<RoutePlan> list) {
        List<EntityModel<RoutePlanDTO>> models = list.stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models));
    }
}
