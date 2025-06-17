package pl.polsl.courier.management.system.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import pl.polsl.courier.management.system.dto.ClientDTO;
import pl.polsl.courier.management.system.entity.Client;
import pl.polsl.courier.management.system.repository.ClientRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Klienci", description = "Operacje na zasobie Klienci")
@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientRepository clientRepo;

    @Operation(summary = "Dodaj nowego klienta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Klient utworzony",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "409", description = "Email lub numer telefonu już w użyciu")
    })
    @PostMapping
    public ResponseEntity<EntityModel<ClientDTO>> addClient(@Valid @RequestBody ClientDTO dto) {
        if (clientRepo.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (clientRepo.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already in use");
        }

        Client client = new Client();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPhoneNumber(dto.getPhoneNumber());
        client.setAddress(dto.getAddress());

        Client saved = clientRepo.save(client);
        ClientDTO savedDto = new ClientDTO(saved);

        EntityModel<ClientDTO> model = EntityModel.of(savedDto,
            linkTo(methodOn(ClientController.class).getById(savedDto.getId())).withSelfRel()
        );
        savedDto.getParcelIds().forEach(pid ->
            model.add(linkTo(methodOn(ParcelController.class).getParcel(pid)).withRel("parcel"))
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @Operation(summary = "Aktualizuj istniejącego klienta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Klient zaktualizowany",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "404", description = "Klient nie znaleziony"),
        @ApiResponse(responseCode = "409", description = "Email lub numer telefonu już w użyciu")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ClientDTO>> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDTO dto) {

        Client client = clientRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        if (clientRepo.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (clientRepo.existsByPhoneNumberAndIdNot(dto.getPhoneNumber(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already in use");
        }

        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPhoneNumber(dto.getPhoneNumber());
        client.setAddress(dto.getAddress());

        Client updated = clientRepo.save(client);
        ClientDTO updatedDto = new ClientDTO(updated);

        EntityModel<ClientDTO> model = EntityModel.of(updatedDto,
            linkTo(methodOn(ClientController.class).getById(updatedDto.getId())).withSelfRel()
        );
        updatedDto.getParcelIds().forEach(pid ->
            model.add(linkTo(methodOn(ParcelController.class).getParcel(pid)).withRel("parcel"))
        );

        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Usuń klienta po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Klient usunięty"),
        @ApiResponse(responseCode = "404", description = "Klient nie znaleziony")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (!clientRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
        clientRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Pobierz klienta po ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Klient znaleziony",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "404", description = "Klient nie znaleziony")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClientDTO>> getById(@PathVariable Long id) {
        return clientRepo.findById(id)
            .map(client -> {
                ClientDTO dto = new ClientDTO(client);
                EntityModel<ClientDTO> model = EntityModel.of(dto,
                    linkTo(methodOn(ClientController.class).getById(id)).withSelfRel(),
                    linkTo(methodOn(ClientController.class).getByEmail(dto.getEmail())).withRel("byEmail")
                );
                dto.getParcelIds().forEach(pid ->
                    model.add(linkTo(methodOn(ParcelController.class).getParcel(pid)).withRel("parcel"))
                );
                return ResponseEntity.ok(model);
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
    }

    @Operation(summary = "Pobierz klientów po imieniu")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista klientów zwrócona",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak klientów o podanym imieniu")
    })
    @GetMapping("/firstName/{firstName}")
    public ResponseEntity<CollectionModel<EntityModel<ClientDTO>>> getByFirstName(
            @PathVariable String firstName) {
        List<Client> found = clientRepo.findByFirstName(firstName);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No clients with firstName = '" + firstName + "'");
        }
        List<EntityModel<ClientDTO>> clients = found.stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(clients,
            linkTo(methodOn(ClientController.class).getByFirstName(firstName)).withSelfRel()
        ));
    }

    @Operation(summary = "Pobierz klientów po nazwisku")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista klientów zwrócona",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak klientów o podanym nazwisku")
    })
    @GetMapping("/lastName/{lastName}")
    public ResponseEntity<CollectionModel<EntityModel<ClientDTO>>> getByLastName(
            @PathVariable String lastName) {
        List<Client> found = clientRepo.findByLastName(lastName);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No clients with lastName = '" + lastName + "'");
        }
        List<EntityModel<ClientDTO>> clients = found.stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(clients,
            linkTo(methodOn(ClientController.class).getByLastName(lastName)).withSelfRel()
        ));
    }

    @Operation(summary = "Pobierz klienta po emailu")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Klient znaleziony",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "404", description = "Klient o podanym emailu nie znaleziony")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<EntityModel<ClientDTO>> getByEmail(
            @PathVariable String email) {
        return clientRepo.findByEmail(email)
            .map(client -> ResponseEntity.ok(toModel(client)))
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Client with email = '" + email + "' not found"));
    }

    @Operation(summary = "Pobierz klientów po numerze telefonu")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista klientów zwrócona",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak klientów o podanym numerze telefonu")
    })
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<CollectionModel<EntityModel<ClientDTO>>> getByPhoneNumber(
            @PathVariable String phoneNumber) {
        List<Client> found = clientRepo.findByPhoneNumber(phoneNumber);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No clients with phoneNumber = '" + phoneNumber + "'");
        }
        List<EntityModel<ClientDTO>> clients = found.stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(clients,
            linkTo(methodOn(ClientController.class).getByPhoneNumber(phoneNumber)).withSelfRel()
        ));
    }

    @Operation(summary = "Pobierz klientów po imieniu i nazwisku")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista klientów zwrócona",
            content = @Content(schema = @Schema(implementation = ClientDTO.class))),
        @ApiResponse(responseCode = "404", description = "Brak klientów o podanym imieniu i nazwisku")
    })
    @GetMapping("/name/{firstName}/{lastName}")
    public ResponseEntity<CollectionModel<EntityModel<ClientDTO>>> getByFirstAndLastName(
            @PathVariable String firstName,
            @PathVariable String lastName) {
        List<Client> found = clientRepo.findByFirstNameAndLastName(firstName, lastName);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No clients with name = '" + firstName + " " + lastName + "'");
        }
        List<EntityModel<ClientDTO>> clients = found.stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(clients,
            linkTo(methodOn(ClientController.class)
                .getByFirstAndLastName(firstName, lastName)).withSelfRel()
        ));
    }

    private EntityModel<ClientDTO> toModel(Client client) {
        ClientDTO dto = new ClientDTO(client);
        EntityModel<ClientDTO> model = EntityModel.of(dto,
            linkTo(methodOn(ClientController.class).getById(client.getId())).withSelfRel()
        );
        dto.getParcelIds().forEach(pid ->
            model.add(linkTo(methodOn(ParcelController.class).getParcel(pid))
                .withRel("parcel")));
        return model;
    }
}
