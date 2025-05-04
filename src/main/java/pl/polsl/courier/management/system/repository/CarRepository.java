package pl.polsl.courier.management.system.repository;

import org.springframework.data.repository.CrudRepository;

import pl.polsl.courier.management.system.entity.Car;

public interface CarRepository extends CrudRepository<Car, Long> {

}
