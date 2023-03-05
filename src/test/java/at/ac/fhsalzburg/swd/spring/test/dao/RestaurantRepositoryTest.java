package at.ac.fhsalzburg.swd.spring.test.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import at.ac.fhsalzburg.swd.spring.dao.Chair;
import at.ac.fhsalzburg.swd.spring.dao.ChairRepository;
import at.ac.fhsalzburg.swd.spring.dao.Restaurant;
import at.ac.fhsalzburg.swd.spring.dao.RestaurantRepository;

// https://www.baeldung.com/spring-boot-testing
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository resRepo;

    @Autowired
    private ChairRepository chairRepo;

    @Test
    public void whenRestaurantCreated_thenIdFilled() {
        // given
        Restaurant res = new Restaurant("Restaurant");
        // when
        Restaurant saved = resRepo.save(res);
        // then
        assertTrue(saved.getId() > 0);
    }

    @Test
    public void whenRestaurantAssignedToChair_thenRestaurantShouldBeFetched() {
        // given
        Restaurant res = new Restaurant("Restaurant");
        Chair c1 = new Chair("Chair1");
        Chair c2 = new Chair("Chair2");

        // the chairs are assigned to the given restaurant
        // to create the foreign key relation
        res.getChairs().add(c1);
        res.getChairs().add(c2);

        // when

        // order is important, as the chairs reference a restaurant it needs to be saved first
        Restaurant savedRestaurant = resRepo.save(res);
        chairRepo.save(c1);
        chairRepo.save(c2);

        // find all restaurants, the chairs should be fetched as well
        Iterable<Restaurant> fetchedAll = resRepo.findAll();
        Restaurant fetched = fetchedAll.iterator().next();

        Optional<Chair> foundChair1 = chairRepo.findByName("Chair1");
        Optional<Chair> foundChair2 = chairRepo.findByName("Chair2");

        Set<Chair> chairs = fetched.getChairs();

        // then
        assertTrue(savedRestaurant.getId() > 0);
        assertNotNull(fetched);
        assertEquals("Restaurant", fetched.getName());
        assertEquals(2, chairs.size());

        // the chair should have the reference to the restaurant
        assertTrue(foundChair1.isPresent());
        assertTrue(foundChair2.isPresent());
    }
}
