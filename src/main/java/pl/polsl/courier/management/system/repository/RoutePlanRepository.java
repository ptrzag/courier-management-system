package pl.polsl.courier.management.system.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import pl.polsl.courier.management.system.entity.RoutePlan;

public interface RoutePlanRepository extends CrudRepository<RoutePlan, Long> {
    // Wyszukiwanie po dacie
    List<RoutePlan> findByScheduleDate(LocalDate scheduleDate);

    // Dokładne dopasowanie całego pola startLocation / endLocation
    List<RoutePlan> findByStartLocation(String address);
    List<RoutePlan> findByEndLocation(String address);

    // Contains pozwala szukać po fragmencie, np. kodzie pocztowym "00-950"
    List<RoutePlan> findByStartLocationContaining(String fragment);
    List<RoutePlan> findByEndLocationContaining(String fragment);

    // Dla przystanków: exact match
    List<RoutePlan> findByStops(String address);

    // Dla przystanków: fragment (np. "30-")
    @Query("SELECT r FROM RoutePlan r JOIN r.stops s WHERE s LIKE %:fragment%")
    List<RoutePlan> findByStopsContaining(@Param("fragment") String fragment);
}
