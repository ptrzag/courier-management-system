package pl.polsl.courier.management.system.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.polsl.courier.management.system.entity.Car;
import pl.polsl.courier.management.system.repository.CarRepository;

@Controller
@RequestMapping("/car")
public class CarController {
	@Autowired
	CarRepository carRepo;
	
	@PostMapping
	public @ResponseBody String addCar(@RequestBody Car car) {
		car = carRepo.save(car);
		return "Added with id = " + car.getId();
	}
	
	@GetMapping
	public @ResponseBody Optional<Car> getCar(@RequestParam Long id) {
		return carRepo.findById(id);
	}
}
