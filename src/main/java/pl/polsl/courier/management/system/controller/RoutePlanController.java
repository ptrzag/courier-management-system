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

import pl.polsl.courier.management.system.dto.RoutePlanDTO;
import pl.polsl.courier.management.system.entity.RoutePlan;
import pl.polsl.courier.management.system.entity.Parcel;
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

    // -------- CREATE --------
    @PostMapping
    public ResponseEntity<EntityModel<RoutePlanDTO>> addRoutePlan(
            @Valid @RequestBody RoutePlanDTO dto) {

        RoutePlan route = new RoutePlan();
        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());
        route.setEstimatedTime(dto.getEstimatedTime());
        route.setScheduleDate(dto.getScheduleDate());
        route.setStops(dto.getStops());

        if (dto.getCarId() != null) {
            carRepo.findById(dto.getCarId()).ifPresent(route::setCar);
        }

        // assign parcels if provided
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

        RoutePlan saved = routePlanRepo.save(route);
        RoutePlanDTO savedDto = new RoutePlanDTO(saved);

        return ResponseEntity
                .created(linkTo(methodOn(RoutePlanController.class)
                        .getRoutePlan(savedDto.getId())).toUri())
                .body(buildModel(savedDto, saved));
    }

    // -------- READ ONE --------
    @GetMapping
    public ResponseEntity<EntityModel<RoutePlanDTO>> getRoutePlan(@RequestParam Long id) {
        return routePlanRepo.findById(id)
                .map(route -> {
                    RoutePlanDTO dto = new RoutePlanDTO(route);
                    return ResponseEntity.ok(buildModel(dto, route));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // -------- UPDATE --------
    @PutMapping("/update")
    public ResponseEntity<EntityModel<RoutePlanDTO>> updateRoutePlan(
            @Valid @RequestBody RoutePlanDTO dto) {

        if (dto.getId() == null || !routePlanRepo.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }

        RoutePlan route = routePlanRepo.findById(dto.getId()).get();

        // 1. Update basic fields
        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());
        route.setEstimatedTime(dto.getEstimatedTime());
        route.setScheduleDate(dto.getScheduleDate());
        route.setStops(dto.getStops());

        // 2. Update car assignment
        if (dto.getCarId() != null) {
            carRepo.findById(dto.getCarId()).ifPresent(route::setCar);
        } else {
            route.setCar(null);
        }

        // 3. Update parcels
        // detach old
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

        RoutePlan updated = routePlanRepo.save(route);
        RoutePlanDTO updatedDto = new RoutePlanDTO(updated);

        return ResponseEntity.ok(buildModel(updatedDto, updated));
    }

    // -------- DELETE --------
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRoutePlan(@RequestParam Long id) {
        if (!routePlanRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        routePlanRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // -------- FILTERS --------
    @GetMapping("/by-date")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByScheduleDate(
            @RequestParam String date) {
        LocalDate parsed = LocalDate.parse(date);
        return wrapList(routePlanRepo.findByScheduleDate(parsed));
    }

    @GetMapping("/by-start-address")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStartAddress(
            @RequestParam String address) {
        return wrapList(routePlanRepo.findByStartLocation(address));
    }

    @GetMapping("/by-end-address")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByEndAddress(
            @RequestParam String address) {
        return wrapList(routePlanRepo.findByEndLocation(address));
    }

    @GetMapping("/by-start-fragment")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStartFragment(
            @RequestParam String fragment) {
        return wrapList(routePlanRepo.findByStartLocationContaining(fragment));
    }

    @GetMapping("/by-end-fragment")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByEndFragment(
            @RequestParam String fragment) {
        return wrapList(routePlanRepo.findByEndLocationContaining(fragment));
    }

    @GetMapping("/by-stop-address")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStopAddress(
            @RequestParam String address) {
        return wrapList(routePlanRepo.findByStops(address));
    }

    @GetMapping("/by-stop-fragment")
    public ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> getByStopFragment(
            @RequestParam String fragment) {
        return wrapList(routePlanRepo.findByStopsContaining(fragment));
    }

    // -------- HELPERS --------

    private EntityModel<RoutePlanDTO> buildModel(RoutePlanDTO dto, RoutePlan route) {
        EntityModel<RoutePlanDTO> model = EntityModel.of(dto,
            linkTo(methodOn(RoutePlanController.class).getRoutePlan(dto.getId())).withSelfRel()
        );

        if (route.getCar() != null) {
            model.add(linkTo(methodOn(CarController.class)
                .getCarByRegistrationNumber(route.getCar().getRegistrationNumber()))
                .withRel("car"));
        }

        if (dto.getParcelIds() != null) {
            dto.getParcelIds().forEach(pid ->
                model.add(linkTo(methodOn(ParcelController.class).getParcel(pid))
                    .withRel("parcel"))
            );
        }

        return model;
    }

    private ResponseEntity<CollectionModel<EntityModel<RoutePlanDTO>>> wrapList(List<RoutePlan> list) {
        List<EntityModel<RoutePlanDTO>> dtos = list.stream()
            .map(r -> buildModel(new RoutePlanDTO(r), r))
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(dtos));
    }
}
