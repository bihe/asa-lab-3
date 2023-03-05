package at.ac.fhsalzburg.swd.spring.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends CrudRepository<Restaurant, Long>{
    Optional<Restaurant> findByName(String name);
}
