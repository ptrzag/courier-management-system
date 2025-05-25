package pl.polsl.courier.management.system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import pl.polsl.courier.management.system.entity.Car;

public interface CarRepository extends CrudRepository<Car, Long> {
	Optional<Car> findByRegistrationNumber(String registrationNumber);
	boolean existsByRegistrationNumber(String registrationNumber);
	List<Car> findByRoutesIsEmpty();
    List<Car> findByRoutesIsNotEmpty();
    List<Car> findByBrand(String brand);
    List<Car> findByModel(String model);
    List<Car> findByCapacityBetween(Double minCapacity, Double maxCapacity);
    List<Car> findByCapacityGreaterThanEqual(Double minCapacity);
    List<Car> findByCapacityLessThanEqual(Double maxCapacity);
}
