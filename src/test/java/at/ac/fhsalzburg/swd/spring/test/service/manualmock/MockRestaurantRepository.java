package at.ac.fhsalzburg.swd.spring.test.service.manualmock;

import java.util.Optional;

import at.ac.fhsalzburg.swd.spring.dao.Restaurant;
import at.ac.fhsalzburg.swd.spring.dao.RestaurantRepository;

// MockRestaurantRepository shows the manual way how to implement a mock object
// this is possible because RestaurantRepository is just an Interface
// In this case all necessary methods need to be implemented and our test-logic
// is handled within the specific methods
public class MockRestaurantRepository implements RestaurantRepository {

    @Override
    public Optional<Restaurant> findByName(String name) {
        switch (name) {
            case "existingRestaurant":
                return Optional.of(new Restaurant("existingRestaurant"));
            default:
                break;
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S extends Restaurant> S save(S entity) {
        if(entity == null)
            return null;
        return (S) new Restaurant("newRestaurant");
    }

    @Override
    public <S extends Restaurant> Iterable<S> saveAll(Iterable<S> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterable<Restaurant> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<Restaurant> findAllById(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteById(Long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Restaurant entity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll(Iterable<? extends Restaurant> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }



}
