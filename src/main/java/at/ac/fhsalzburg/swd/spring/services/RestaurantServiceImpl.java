package at.ac.fhsalzburg.swd.spring.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

import at.ac.fhsalzburg.swd.spring.dao.Chair;
import at.ac.fhsalzburg.swd.spring.dao.ChairRepository;
import at.ac.fhsalzburg.swd.spring.dao.Restaurant;
import at.ac.fhsalzburg.swd.spring.dao.RestaurantRepository;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private RestaurantRepository resRepo;
    private ChairRepository chairRepo;

    // Constructor-Injection is recommended because it "feels" more natural and less
    // magic compared to property injection.
    // https://www.baeldung.com/constructor-injection-in-spring
    // Note: The @Autowired annotation is optional here. If only one constructor is
    // available it can be omitted
    @Autowired
    public RestaurantServiceImpl(RestaurantRepository resRepo, ChairRepository chairRepo) {
        this.resRepo = resRepo;
        this.chairRepo = chairRepo;
    }

    @Override
    public Restaurant create(String name) {
        if ("".equals(name) || name == null) {
            throw new java.lang.IllegalArgumentException("empty or null name supplied!");
        }

        var exists = this.resRepo.findByName(name);
        if (exists.isPresent()) {
            throw new java.lang.IllegalArgumentException("the given name '" + name + "' is already in use!");
        }

        return this.resRepo.save(new Restaurant(name));
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        if (restaurant == null) {
            throw new java.lang.IllegalArgumentException("No restaurant supplied!");
        }
        if (restaurant.getId() == null) {
            throw new java.lang.IllegalArgumentException("Restaurant without id supplied!");
        }
        Optional<Restaurant> lookup = this.resRepo.findById(restaurant.getId());
        if (!lookup.isPresent()) {
            throw new java.lang.IllegalArgumentException("Could not find restaurant by id: " + restaurant.getId());
        }

        // simple business-logic
        // if we save the restaurant with a given name ensure that the name is not already taken
        lookup = this.resRepo.findByName(restaurant.getName());
        // if we find the same name signal error - but only if the found is not the one we are editing
        if (lookup.isPresent() && lookup.get().getId() != restaurant.getId()) {
            // we have a problem, cannot save a restaurant with the same name
            throw new java.lang.IllegalArgumentException("the given name '" + restaurant.getName() + "' is already in use!");
        }

        return this.resRepo.save(restaurant);
    }

    @Override
    public Restaurant getRestaurantByName(String name) {
        if ("".equals(name) || name == null) {
            throw new java.lang.IllegalArgumentException("empty or null name supplied!");
        }
        var exists = this.resRepo.findByName(name);
        if (!exists.isPresent()) {
            return null;
        }

        return exists.get();
    }

    @Override
    public Restaurant addChair(Restaurant restaurant, Chair chair) {
        if (restaurant == null) {
            throw new java.lang.IllegalArgumentException("null restaurant supplied!");
        }

        if (chair == null) {
            throw new java.lang.IllegalArgumentException("null chair supplied!");
        }

        var existingRes = this.resRepo.findByName(restaurant.getName());
        if (!existingRes.isPresent()) {
            throw new RuntimeException("the provided restaurant could not be found!");
        }
        Restaurant res = existingRes.get();
        Chair c = null;

        var existingChair = this.chairRepo.findByName(chair.getName());
        if (!existingChair.isPresent()) {
            c = this.chairRepo.save(chair);
        } else {
            c = existingChair.get();
        }

        restaurant.getChairs().add(c);
        this.resRepo.save(restaurant);

        return res;
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        Iterable<Restaurant> all = this.resRepo.findAll();
        if (all != null) {
            return ImmutableList.copyOf(all.iterator());
        }
        return new ArrayList<>();
    }

    @Override
    public Restaurant getById(Long id) {
        Optional<Restaurant> found = this.resRepo.findById(id);
        if (!found.isPresent())
            return null;
        return found.get();
    }

}
