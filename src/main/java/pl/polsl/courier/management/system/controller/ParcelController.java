package pl.polsl.courier.management.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.polsl.courier.management.system.entity.Parcel;
import pl.polsl.courier.management.system.repository.ParcelRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/parcel")
public class ParcelController {
	@Autowired
	ParcelRepository parcelRepo;
	
	@PostMapping
	public @ResponseBody String addParcel(@RequestBody Parcel parcel) {
		parcel = parcelRepo.save(parcel);
		return "Added with id = " + parcel.getId();
	}
	
	@GetMapping
	public @ResponseBody Optional<Parcel> getParcel(@RequestParam Long id) {
		return parcelRepo.findById(id);
	}
}
