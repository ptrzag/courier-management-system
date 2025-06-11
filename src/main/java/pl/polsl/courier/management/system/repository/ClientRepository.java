package pl.polsl.courier.management.system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.polsl.courier.management.system.entity.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
	List<Client> findByFirstName(String firstName);
    List<Client> findByLastName(String lastName);
    Optional<Client> findByEmail(String email);
    List<Client> findByPhoneNumber(String phoneNumber);
    List<Client> findByFirstNameAndLastName(String firstName, String lastName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);
}