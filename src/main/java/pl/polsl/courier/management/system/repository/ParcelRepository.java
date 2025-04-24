package pl.polsl.courier.management.system.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.polsl.courier.management.system.entity.Parcel;

@Repository
public interface ParcelRepository extends CrudRepository<Parcel, Long> {

}
