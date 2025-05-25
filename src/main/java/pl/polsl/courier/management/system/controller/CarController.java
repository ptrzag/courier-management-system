package pl.polsl.courier.management.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import pl.polsl.courier.management.system.entity.Car;
import pl.polsl.courier.management.system.repository.CarRepository;

@Controller
@RequestMapping("/car")
public class CarController {
	@Autowired
	CarRepository carRepo;
	
	@GetMapping("/{registrationNumber}")
	public @ResponseBody Car getCarByRegistrationNumber(@PathVariable String registrationNumber) {
	    return carRepo.findByRegistrationNumber(registrationNumber)
	        // Check if this registration number already exists
	    	.orElseThrow(() -> new ResponseStatusException(
	            HttpStatus.NOT_FOUND,
	            "Car with registration number '" + registrationNumber + "' not found"
	        ));
	}
	
	@PostMapping
	public @ResponseBody Car addCar(@RequestBody Car car) {
	    String regNum = car.getRegistrationNumber();
	    
	    // Check if this registration number already exists
	    if (carRepo.existsByRegistrationNumber(regNum)) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Car with registration number '" + regNum + "' already exists"
	        );
	    }
	    return carRepo.save(car);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
	    // Check if car with this id exists
		Car car = carRepo.findById(id)
	        .orElseThrow(() -> new ResponseStatusException(
	            HttpStatus.NOT_FOUND, "Car not found"));
	    
	    // Check if this car has routes assigned
	    if (!car.getRoutes().isEmpty()) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Cannot delete car assigned to routes"
	        );
	    }
	    carRepo.delete(car);
	    return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/{id}")
	public @ResponseBody Car updateCar(@PathVariable Long id, @RequestBody Car newCar) {
	    // Check if car with this id exists
		Car car = carRepo.findById(id)
	        .orElseThrow(() -> new ResponseStatusException(
	            HttpStatus.NOT_FOUND, "Car not found"
	        ));

	    // Check if this registration number already exists
	    String newReg = newCar.getRegistrationNumber();
	    if (!newReg.equals(car.getRegistrationNumber())
	        && carRepo.existsByRegistrationNumber(newReg)) {
	        throw new ResponseStatusException(
	            HttpStatus.BAD_REQUEST,
	            "Registration number '" + newReg + "' is already in use"
	        );
	    }

	    // Update information
	    car.setBrand(newCar.getBrand());
	    car.setModel(newCar.getModel());
	    car.setRegistrationNumber(newReg);
	    car.setMileage(newCar.getMileage());
	    car.setCapacity(newCar.getCapacity());

	    return carRepo.save(car);
	}
	
	@GetMapping("/available")
	public @ResponseBody List<Car> getAvailableCars() {
	    return carRepo.findByRoutesIsEmpty();
	}
	
	@GetMapping("/occupied")
	public @ResponseBody List<Car> getOccupiedCars() {
	    return carRepo.findByRoutesIsNotEmpty();
	}
	
	@GetMapping("/brand/{brand}")
	public @ResponseBody List<Car> getCarsByBrand(@PathVariable String brand) {
	    return carRepo.findByBrand(brand);
	}

	@GetMapping("/model/{model}")
	public @ResponseBody List<Car> getCarsByModel(@PathVariable String model) {
	    return carRepo.findByModel(model);
	}
	
	@GetMapping("/capacity")
	public @ResponseBody List<Car> getCarsByCapacity(@RequestParam(required = false) Double min, @RequestParam(required = false) Double max) {
	    if (min != null && max != null) {
	        return carRepo.findByCapacityBetween(min, max);
	    } else if (min != null) {
	        return carRepo.findByCapacityGreaterThanEqual(min);
	    } else if (max != null) {
	        return carRepo.findByCapacityLessThanEqual(max);
	    } else {
	        return (List<Car>) carRepo.findAll();
	    }
	}

}
