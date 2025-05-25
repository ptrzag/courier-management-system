package pl.polsl.courier.management.system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pl.polsl.courier.management.system.entity.Client;

import pl.polsl.courier.management.system.repository.ClientRepository;

@RestController            
@RequestMapping("/client")
public class ClientController {
	@Autowired
    private ClientRepository clientRepo;

    @PostMapping
    public Client addClient(@RequestBody Client client) {
        return clientRepo.save(client);
    }

    @PutMapping("/update/{id}")
    public Client updateClient(@PathVariable Long id,
                               @RequestBody Client details) {
        return clientRepo.findById(id)
            .map(c -> {
                c.setFirstName(details.getFirstName());
                c.setLastName(details.getLastName());
                c.setEmail(details.getEmail());
                c.setPhoneNumber(details.getPhoneNumber());
                c.setAddress(details.getAddress());
                return clientRepo.save(c);
            })
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        if (!clientRepo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        clientRepo.deleteById(id);
    }

    @GetMapping("/search/firstName")
    public List<Client> getByFirstName(@RequestParam String firstName) {
        return clientRepo.findByFirstName(firstName);
    }

    @GetMapping("/search/lastName")
    public List<Client> getByLastName(@RequestParam String lastName) {
        return clientRepo.findByLastName(lastName);
    }

    @GetMapping("/search/email")
    public Client getByEmail(@RequestParam String email) {
        return clientRepo.findByEmail(email).orElse(null);
    }

    @GetMapping("/search/phone")
    public List<Client> getByPhoneNumber(@RequestParam String phoneNumber) {
        return clientRepo.findByPhoneNumber(phoneNumber);
    }

    @GetMapping("/search/name")
    public List<Client> getByFirstAndLastName(@RequestParam String firstName,
                                              @RequestParam String lastName) {
        return clientRepo.findByFirstNameAndLastName(firstName, lastName);
    }
}