package pl.polsl.courier.management.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;


import pl.polsl.courier.management.system.repository.ClientRepository;

import pl.polsl.courier.management.system.entity.Client;

@Controller
@RequestMapping("/client")
public class ClientController {
	@Autowired
	ClientRepository clientRepo;
	
	@PostMapping
	public @ResponseBody String addClient(@RequestBody Client client) {
		client = clientRepo.save(client);
		return "Added with id = " + client.getId();
	}
	
	@GetMapping
	public @ResponseBody Iterable<Client> getClients(@RequestParam String lastName) {
		return clientRepo.findByLastName(lastName);
	}
}