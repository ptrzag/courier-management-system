package pl.polsl.courier.management.system.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pl.polsl.courier.management.system.entity.RoutePlan;

@Repository
public interface RoutePlanRepository extends CrudRepository<RoutePlan, Long> {
    List<RoutePlan> findByScheduleDate(LocalDate scheduleDate);
    List<RoutePlan> findByStartLocation(String address);
    List<RoutePlan> findByEndLocation(String address);
    List<RoutePlan> findByStartLocationContaining(String fragment);
    List<RoutePlan> findByEndLocationContaining(String fragment);
    List<RoutePlan> findByStops(String address);

    @Query("SELECT r FROM RoutePlan r JOIN r.stops s WHERE s LIKE %:fragment%")
    List<RoutePlan> findByStopsContaining(@Param("fragment") String fragment);
}
