package at.ac.fhsalzburg.swd.spring.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// there are many possibilities to create queries
// https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#jpa.query-methods.query-creation
@Repository
public interface ChairRepository extends CrudRepository<Chair, Long> /* CustomChairRepository */ {
    Optional<Chair> findByName(String name);
    List<Chair> findByNameEndingWith(String name);
}
