package pl.polsl.courier.management.system.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.polsl.courier.management.system.entity.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
	List<Client> findByLastName(String lastName);
}