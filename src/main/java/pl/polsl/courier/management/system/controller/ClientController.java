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

import pl.polsl.courier.management.system.dto.ClientDTO;
import pl.polsl.courier.management.system.entity.Client;
import pl.polsl.courier.management.system.repository.ClientRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientRepository clientRepo;

    @PostMapping
    public ResponseEntity<EntityModel<ClientDTO>> addClient(@RequestBody ClientDTO dto) {
        Client client = new Client();
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setEmail(dto.getEmail());
        client.setPhoneNumber(dto.getPhoneNumber());
        client.setAddress(dto.getAddress());

        Client saved = clientRepo.save(client);
        ClientDTO savedDto = new ClientDTO(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                EntityModel.of(savedDto,
                        linkTo(methodOn(ClientController.class).getById(savedDto.getId())).withSelfRel()));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EntityModel<ClientDTO>> updateClient(@PathVariable Long id,
                                                                @RequestBody ClientDTO dto) {
        return clientRepo.findById(id)
                .map(c -> {
                    c.setFirstName(dto.getFirstName());
                    c.setLastName(dto.getLastName());
                    c.setEmail(dto.getEmail());
                    c.setPhoneNumber(dto.getPhoneNumber());
                    c.setAddress(dto.getAddress());

                    Client updated = clientRepo.save(c);
                    ClientDTO updatedDto = new ClientDTO(updated);

                    return ResponseEntity.ok(EntityModel.of(updatedDto,
                            linkTo(methodOn(ClientController.class).getById(updatedDto.getId())).withSelfRel()));
                })
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (!clientRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        clientRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClientDTO>> getById(@PathVariable Long id) {
        return clientRepo.findById(id)
                .map(client -> {
                    ClientDTO dto = new ClientDTO(client);
                    return ResponseEntity.ok(EntityModel.of(dto,
                            linkTo(methodOn(ClientController.class).getById(id)).withSelfRel(),
                            linkTo(methodOn(ClientController.class).getByEmail(dto.getEmail())).withRel("byEmail")));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/firstName")
    public ResponseEntity<CollectionModel<EntityModel<ClientDTO>>> getByFirstName(@RequestParam String firstName) {
        List<EntityModel<ClientDTO>> clients = clientRepo.findByFirstName(firstName).stream()
                .map(c -> EntityModel.of(new ClientDTO(c),
                        linkTo(methodOn(ClientController.class).getById(c.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(clients,
                linkTo(methodOn(ClientController.class).getByFirstName(firstName)).withSelfRel()));
    }

    @GetMapping("/search/lastName")
    public ResponseEntity<CollectionModel<EntityModel<ClientDTO>>> getByLastName(@RequestParam String lastName) {
        List<EntityModel<ClientDTO>> clients = clientRepo.findByLastName(lastName).stream()
                .map(c -> EntityModel.of(new ClientDTO(c),
                        linkTo(methodOn(ClientController.class).getById(c.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(clients,
                linkTo(methodOn(ClientController.class).getByLastName(lastName)).withSelfRel()));
    }

    @GetMapping("/search/email")
    public ResponseEntity<EntityModel<ClientDTO>> getByEmail(@RequestParam String email) {
        return clientRepo.findByEmail(email)
                .map(client -> {
                    ClientDTO dto = new ClientDTO(client);
                    return ResponseEntity.ok(EntityModel.of(dto,
                            linkTo(methodOn(ClientController.class).getByEmail(email)).withSelfRel(),
                            linkTo(methodOn(ClientController.class).getById(dto.getId())).withRel("byId")));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/phone")
    public ResponseEntity<CollectionModel<EntityModel<ClientDTO>>> getByPhoneNumber(@RequestParam String phoneNumber) {
        List<EntityModel<ClientDTO>> clients = clientRepo.findByPhoneNumber(phoneNumber).stream()
                .map(c -> EntityModel.of(new ClientDTO(c),
                        linkTo(methodOn(ClientController.class).getById(c.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(clients,
                linkTo(methodOn(ClientController.class).getByPhoneNumber(phoneNumber)).withSelfRel()));
    }

    @GetMapping("/search/name")
    public ResponseEntity<CollectionModel<EntityModel<ClientDTO>>> getByFirstAndLastName(
            @RequestParam String firstName,
            @RequestParam String lastName) {

        List<EntityModel<ClientDTO>> clients = clientRepo.findByFirstNameAndLastName(firstName, lastName).stream()
                .map(c -> EntityModel.of(new ClientDTO(c),
                        linkTo(methodOn(ClientController.class).getById(c.getId())).withSelfRel()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(clients,
                linkTo(methodOn(ClientController.class).getByFirstAndLastName(firstName, lastName)).withSelfRel()));
    }
}
