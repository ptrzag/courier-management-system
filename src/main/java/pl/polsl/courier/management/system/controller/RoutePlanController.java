package pl.polsl.courier.management.system.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pl.polsl.courier.management.system.entity.RoutePlan;
import pl.polsl.courier.management.system.repository.RoutePlanRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/route")
@Validated
public class RoutePlanController {

    @Autowired
    private RoutePlanRepository routePlanRepo;

    @PostMapping
    public String addRoutePlan(@Valid @RequestBody RoutePlan routePlan) {
        routePlan = routePlanRepo.save(routePlan);
        return "Added with id = " + routePlan.getId();
    }

    @GetMapping
    public Optional<RoutePlan> getRoutePlan(@RequestParam Long id) {
        return routePlanRepo.findById(id);
    }

    @GetMapping("/by-date")
    public List<RoutePlan> getByScheduleDate(@RequestParam String date) {
        LocalDate scheduleDate = LocalDate.parse(date);
        return routePlanRepo.findByScheduleDate(scheduleDate);
    }

    @PostMapping("/update")
    public String updateRoutePlan(@Valid @RequestBody RoutePlan routePlan) {
        if (routePlan.getId() == null || !routePlanRepo.existsById(routePlan.getId())) {
            return "RoutePlan not found.";
        }
        routePlanRepo.save(routePlan);
        return "RoutePlan updated.";
    }

    @PostMapping("/delete")
    public String deleteRoutePlan(@RequestParam Long id) {
        if (!routePlanRepo.existsById(id)) {
            return "RoutePlan not found.";
        }
        routePlanRepo.deleteById(id);
        return "RoutePlan deleted.";
    }

    @GetMapping("/by-start-address")
    public List<RoutePlan> getByStartAddress(@RequestParam String address) {
        return routePlanRepo.findByStartLocation(address);
    }

    @GetMapping("/by-end-address")
    public List<RoutePlan> getByEndAddress(@RequestParam String address) {
        return routePlanRepo.findByEndLocation(address);
    }

    @GetMapping("/by-start-fragment")
    public List<RoutePlan> getByStartFragment(@RequestParam String fragment) {
        return routePlanRepo.findByStartLocationContaining(fragment);
    }

    @GetMapping("/by-end-fragment")
    public List<RoutePlan> getByEndFragment(@RequestParam String fragment) {
        return routePlanRepo.findByEndLocationContaining(fragment);
    }

    @GetMapping("/by-stop-address")
    public List<RoutePlan> getByStopAddress(@RequestParam String address) {
        return routePlanRepo.findByStops(address);
    }

    @GetMapping("/by-stop-fragment")
    public List<RoutePlan> getByStopFragment(@RequestParam String fragment) {
        return routePlanRepo.findByStopsContaining(fragment);
    }
}
