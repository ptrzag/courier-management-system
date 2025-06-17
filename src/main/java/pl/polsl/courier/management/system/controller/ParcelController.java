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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import pl.polsl.courier.management.system.dto.ParcelDTO;
import pl.polsl.courier.management.system.entity.Parcel;
import pl.polsl.courier.management.system.repository.ParcelRepository;
import pl.polsl.courier.management.system.repository.ClientRepository;
import pl.polsl.courier.management.system.repository.RoutePlanRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Przesyłki", description = "Operacje na zasobie Przesyłki")
@RestController
@RequestMapping("/parcel")
public class ParcelController {

    @Autowired private ParcelRepository parcelRepo;
    @Autowired private ClientRepository clientRepo;
    @Autowired private RoutePlanRepository routePlanRepo;

    @Operation(summary = "Dodaj nową przesyłkę")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Przesyłka utworzona",
            content = @Content(schema = @Schema(implementation = ParcelDTO.class))),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe")
    })
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

    @Operation(summary = "Pobierz przesyłkę po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Przesyłka znaleziona",
            content = @Content(schema = @Schema(implementation = ParcelDTO.class))),
        @ApiResponse(responseCode = "404", description = "Przesyłka nie znaleziona")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ParcelDTO>> getParcel(@PathVariable Long id) {
        Parcel parcel = parcelRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Przesyłka nie znaleziona z ID: " + id
            ));
        return ResponseEntity.ok(toModel(parcel));
    }

    @Operation(summary = "Pobierz wszystkie przesyłki")
    @ApiResponse(responseCode = "200", description = "Lista przesyłek",
        content = @Content(schema = @Schema(implementation = ParcelDTO.class)))
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ParcelDTO>>> getAllParcels() {
        List<EntityModel<ParcelDTO>> list = StreamSupport.stream(parcelRepo.findAll().spliterator(), false)
            .map(this::toModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(list,
            linkTo(methodOn(ParcelController.class).getAllParcels()).withSelfRel()
        ));
    }

    @Operation(summary = "Aktualizuj przesyłkę po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Przesyłka zaktualizowana",
            content = @Content(schema = @Schema(implementation = ParcelDTO.class))),
        @ApiResponse(responseCode = "404", description = "Przesyłka nie znaleziona"),
        @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ParcelDTO>> updateParcel(
            @PathVariable Long id,
            @Valid @RequestBody ParcelDTO dto) {

        Parcel p = parcelRepo.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Przesyłka nie znaleziona z ID: " + id)
            );

        // Aktualizacja pól
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

        Parcel updated = parcelRepo.save(p);
        return ResponseEntity.ok(toModel(updated));
    }

    @Operation(summary = "Usuń przesyłkę po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Przesyłka usunięta"),
        @ApiResponse(responseCode = "404", description = "Przesyłka nie znaleziona")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        if (!parcelRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Przesyłka nie znaleziona z ID: " + id);
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
