package pl.polsl.courier.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.polsl.courier.management.system.entity.RoutePlan;
import pl.polsl.courier.management.system.repository.RoutePlanRepository;

@Controller
@RequestMapping("/route")
public class RoutePlanController {
	@Autowired
	RoutePlanRepository routePlanRepo;

	@PostMapping
	public @ResponseBody String addRoutePlan(@RequestBody RoutePlan routePlan) {
		routePlan = routePlanRepo.save(routePlan);
		return "Added with id = " + routePlan.getId();
	}

	@GetMapping
	public @ResponseBody Optional<RoutePlan> getRoutePlan(@RequestParam Long id) {
		return routePlanRepo.findById(id);
	}

	@GetMapping("/by-date")
	public @ResponseBody List<RoutePlan> getByScheduleDate(@RequestParam String date) {
		LocalDate scheduleDate = LocalDate.parse(date);
		return routePlanRepo.findByScheduleDate(scheduleDate);
	}

	@PostMapping("/update")
	public @ResponseBody String updateRoutePlan(@RequestBody RoutePlan routePlan) {
		if (routePlan.getId() == null || !routePlanRepo.existsById(routePlan.getId())) {
			return "RoutePlan not found.";
		}
		routePlanRepo.save(routePlan);
		return "RoutePlan updated.";
	}

	@PostMapping("/delete")
	public @ResponseBody String deleteRoutePlan(@RequestParam Long id) {
		if (!routePlanRepo.existsById(id)) {
			return "RoutePlan not found.";
		}
		routePlanRepo.deleteById(id);
		return "RoutePlan deleted.";
	}
}