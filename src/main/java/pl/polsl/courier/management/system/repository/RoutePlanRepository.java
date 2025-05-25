package pl.polsl.courier.management.system.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import pl.polsl.courier.management.system.entity.RoutePlan;

public interface RoutePlanRepository extends CrudRepository<RoutePlan, Long> {
    List<RoutePlan> findByScheduleDate(LocalDate scheduleDate);
}