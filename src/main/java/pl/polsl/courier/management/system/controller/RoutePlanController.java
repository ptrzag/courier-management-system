package pl.polsl.courier.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import pl.polsl.courier.management.system.dto.RoutePlanDTO;
import pl.polsl.courier.management.system.entity.RoutePlan;
import pl.polsl.courier.management.system.repository.CarRepository;
import pl.polsl.courier.management.system.repository.RoutePlanRepository;
import pl.polsl.courier.management.system.repository.ParcelRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RoutePlanDTO>> getRoutePlan(
            @PathVariable Long id) {
        RoutePlan route = routePlanRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Route not found with id: " + id
            ));
        return ResponseEntity.ok(toModel(route));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<RoutePlanDTO>> updateRoutePlan(
            @PathVariable Long id,
            @Valid @RequestBody RoutePlanDTO dto) {
        RoutePlan route = routePlanRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cannot update. Route not found with id: " + id
            ));
        applyDto(route, dto);
        RoutePlan updated = routePlanRepo.save(route);
        return ResponseEntity.ok(toModel(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutePlan(
            @PathVariable Long id) {
        if (!routePlanRepo.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Cannot delete. Route not found with id: " + id
            );
        }
        routePlanRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByScheduleDate(
            @PathVariable String date) {
        LocalDate d = LocalDate.parse(date);
        List<RoutePlan> list = routePlanRepo.findByScheduleDate(d);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No routes found for date: " + date
            );
        }
        return wrapList(list);
    }

    @GetMapping("/start/{address}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStartLocation(
            @PathVariable String address) {
        List<RoutePlan> list = routePlanRepo.findByStartLocation(address);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No routes found starting at: " + address
            );
        }
        return wrapList(list);
    }

    @GetMapping("/start/fragment/{fragment}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStartFragment(
            @PathVariable String fragment) {
        List<RoutePlan> list = routePlanRepo.findByStartLocationContaining(fragment);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No routes found with start containing: " + fragment
            );
        }
        return wrapList(list);
    }

    @GetMapping("/end/{address}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByEndLocation(
            @PathVariable String address) {
        List<RoutePlan> list = routePlanRepo.findByEndLocation(address);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No routes found ending at: " + address
            );
        }
        return wrapList(list);
    }

    @GetMapping("/end/fragment/{fragment}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByEndFragment(
            @PathVariable String fragment) {
        List<RoutePlan> list = routePlanRepo.findByEndLocationContaining(fragment);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No routes found with end containing: " + fragment
            );
        }
        return wrapList(list);
    }

    @GetMapping("/stop/{address}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStop(
            @PathVariable String address) {
        List<RoutePlan> list = routePlanRepo.findByStops(address);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No routes found stopping at: " + address
            );
        }
        return wrapList(list);
    }

    @GetMapping("/stop/fragment/{fragment}")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStopFragment(
            @PathVariable String fragment) {
        List<RoutePlan> list = routePlanRepo.findByStopsContaining(fragment);
        if (list.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No routes found with stop containing: " + fragment
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
