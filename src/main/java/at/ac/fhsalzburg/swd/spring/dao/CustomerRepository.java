package at.ac.fhsalzburg.swd.spring.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

	@Transactional(timeout = 10)
    List<Customer> findByLastName(String lastName);

	@Transactional(timeout = 10)
	Customer findById(long id);
}
