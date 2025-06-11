package pl.polsl.courier.management.system.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import pl.polsl.courier.management.system.dto.ParcelDTO;
import pl.polsl.courier.management.system.entity.Parcel;
import pl.polsl.courier.management.system.repository.ParcelRepository;
import pl.polsl.courier.management.system.repository.ClientRepository;
import pl.polsl.courier.management.system.repository.RoutePlanRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/parcel")
public class ParcelController {

    @Autowired private ParcelRepository parcelRepo;
    @Autowired private ClientRepository clientRepo;
    @Autowired private RoutePlanRepository routePlanRepo;

    @PostMapping
    public ResponseEntity<EntityModel<ParcelDTO>> addParcel(@Valid @RequestBody ParcelDTO dto) {
        Parcel p = new Parcel();
        p.setContentDescription(dto.getContentDescription());
        p.setSenderAddress(dto.getSenderAddress());
        p.setRecipientAddress(dto.getRecipientAddress());
        p.setDispatchDate(dto.getDispatchDate());
        p.setDeliveryDate(dto.getDeliveryDate());
        p.setWeight(dto.getWeight());
        p.setPrice(dto.getPrice());

        if (dto.getClientId() != null) {
            clientRepo.findById(dto.getClientId()).ifPresent(p::setClient);
        }
        if (dto.getRoutePlanId() != null) {
            routePlanRepo.findById(dto.getRoutePlanId()).ifPresent(p::setRoutePlan);
        }

        Parcel saved = parcelRepo.save(p);
        ParcelDTO savedDto = new ParcelDTO(saved);

        EntityModel<ParcelDTO> model = EntityModel.of(savedDto,
            linkTo(methodOn(ParcelController.class).getParcel(savedDto.getId())).withSelfRel(),
            linkTo(methodOn(ClientController.class).getById(savedDto.getClientId())).withRel("client"),
            linkTo(methodOn(RoutePlanController.class).getRoutePlan(savedDto.getRoutePlanId())).withRel("routePlan")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ParcelDTO>> getParcel(@PathVariable Long id) {
        Parcel parcel = parcelRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Parcel not found with id: " + id
            ));

        return ResponseEntity.ok(toModel(parcel));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ParcelDTO>>> getAllParcels() {
        List<EntityModel<ParcelDTO>> list = StreamSupport.stream(parcelRepo.findAll().spliterator(), false)
            .map(this::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(list,
            linkTo(methodOn(ParcelController.class).getAllParcels()).withSelfRel()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ParcelDTO>> updateParcel(
            @PathVariable Long id,
            @Valid @RequestBody ParcelDTO dto) {

        Parcel p = parcelRepo.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Parcel not found with id: " + id)
            );

        Parcel updated = parcelRepo.save(p);
        return ResponseEntity.ok(toModel(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        if (!parcelRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Parcel not found with id: " + id);
        }
        parcelRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    private EntityModel<ParcelDTO> toModel(Parcel p) {
        ParcelDTO dto = new ParcelDTO(p);
        EntityModel<ParcelDTO> model = EntityModel.of(dto,
            linkTo(methodOn(ParcelController.class).getParcel(p.getId())).withSelfRel(),
            linkTo(methodOn(ClientController.class).getById(dto.getClientId())).withRel("client"),
            linkTo(methodOn(RoutePlanController.class).getRoutePlan(dto.getRoutePlanId())).withRel("routePlan")
        );
        return model;
    }
}
