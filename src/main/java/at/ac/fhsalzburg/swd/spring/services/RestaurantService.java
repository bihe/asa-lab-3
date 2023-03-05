package at.ac.fhsalzburg.swd.spring.services;

import java.util.List;

import at.ac.fhsalzburg.swd.spring.dao.Chair;
import at.ac.fhsalzburg.swd.spring.dao.Restaurant;

/**
 * RestaurantService provides the logic to maintain the infrastructure of a restaurant
 * It enables to manage the chairs of the whole restaurant
 */
public interface RestaurantService {
    List<Restaurant> getAllRestaurants();
    Restaurant getById(Long id);
    Restaurant create(String name);
    Restaurant save(Restaurant restaurant);
    Restaurant getRestaurantByName(String name);
    Restaurant addChair(Restaurant restaurant, Chair chair);
}
