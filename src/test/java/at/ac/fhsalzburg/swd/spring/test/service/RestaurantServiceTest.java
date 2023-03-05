package at.ac.fhsalzburg.swd.spring.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.Lists;

import at.ac.fhsalzburg.swd.spring.dao.Chair;
import at.ac.fhsalzburg.swd.spring.dao.ChairRepository;
import at.ac.fhsalzburg.swd.spring.dao.Restaurant;
import at.ac.fhsalzburg.swd.spring.dao.RestaurantRepository;
import at.ac.fhsalzburg.swd.spring.services.RestaurantService;
import at.ac.fhsalzburg.swd.spring.services.RestaurantServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    // @Mock is new - this annotation comes from the Mockito framework
    // Mockito is able to create objects for a given Interfaces.
    // this is just the start, because we get "empty" objects, they do not possess any logic
    // we need to instruct Mockito what to do in certain cases.
    // how this is done can be seen below @see setUp()
    @Mock private ChairRepository mockChairRepo;
    @Mock private RestaurantRepository mockRestauranetRepo;

    private RestaurantService resService;

    // the @BeforeEach annotation tells JUnit to execute the following logic
    @BeforeEach
    void setUp() {
        // This time we do not ask the spring-boot dependency-injection container to create objects
        //  - DI for spring (https://www.baeldung.com/spring-dependency-injection).
        // As a result we have a new RestaurantServiceImpl() instead of @Autowired RestaurantService
        // by doing this manually, we have much more control what we pass into the object
        //
        // Dependency Injection can be done manually as well. A rather common case is Constructor-injection
        // just pass the dependencies of the given object by constructor parameters
        // the objects passed, are mocked version of the "real" repository,
        // which would be crated by spring-boot (via dependency-injection)
        this.resService
            = new RestaurantServiceImpl(mockRestauranetRepo, mockChairRepo);

        // The service we want to test uses the repositories and calls methods on those repositories.
        // Our task now is to tell Mockito what should happen, when those methods are called on the
        // mocked objects.
        // A bit of disadvantage of this approach is, that we need to know how the RestaurantServiceImpl
        // is working internally. So this is no pure black-box testing. But as we want to test the
        // Service logic this is acceptable.

        // RestaurantRepository.findByName
        // lenient ?? https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#mockito_lenient
        lenient().when(this.mockRestauranetRepo.findByName("existingRestaurant"))
            .thenReturn(Optional.of(new Restaurant("existingRestaurant")));
        lenient().when(this.mockRestauranetRepo.findByName("newRestaurant"))
            .thenReturn(Optional.empty());
        lenient().when(this.mockRestauranetRepo.findById(1L))
            .thenReturn(Optional.of(new Restaurant("restaurant1")));

        // RestaurantRepository.save
        lenient().when(this.mockRestauranetRepo.save(any(Restaurant.class)))
            .thenReturn(new Restaurant("newRestaurant"));

        // RestaurantRepository.findAll
        lenient().when(this.mockRestauranetRepo.findAll())
            .thenReturn(
                Lists.newArrayList(
                    new Restaurant("restaurant1"),
                    new Restaurant("restaurant2"),
                    new Restaurant("restaurant3")
                )
            );

        // ChairRepository.findByName
        lenient().when(this.mockChairRepo.findByName("existingChair"))
            .thenReturn(Optional.of(new Chair("existingChair")));
        lenient().when(this.mockChairRepo.findByName("newChair"))
            .thenReturn(Optional.of(new Chair("newChair")));

        // ChairRepository.save
        lenient().when(this.mockChairRepo.save(new Chair("newChair")))
            .thenReturn(new Chair("newChair"));

    }

    @Test
    public void test_createRestauranet() {
        // if I try to create an existing restaurant an exception is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            this.resService.create("existingRestaurant");
        });

        // the same is true for a missing restaurant name
        assertThrows(IllegalArgumentException.class, () -> {
            this.resService.create("");
        });

        Restaurant res = this.resService.create("newRestaurant");
        assertNotNull(res);
        assertEquals("newRestaurant", res.getName());
    }

    @Test
    public void test_saveRestaurant() {
        Restaurant existingRestaurant = new Restaurant("existingRestaurant");
        // we indicate a "different" object by setting the id
        existingRestaurant.setId(1L);

        // cannot save an restaurant with the same name
        assertThrows(IllegalArgumentException.class, () -> {
            this.resService.save(existingRestaurant);
        });

        // a different name works
        existingRestaurant.setName("newRestaurant");
        Restaurant saved = this.resService.save(existingRestaurant);

        assertNotNull(saved);
    }

    @Test
    public void test_getRestaurant() {
        // if I try to fetch an empty/null restaurant
        assertThrows(IllegalArgumentException.class, () -> {
            this.resService.getRestaurantByName("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.resService.getRestaurantByName(null);
        });

        Restaurant existingRestaurant = this.resService.getRestaurantByName("existingRestaurant");
        assertNotNull(existingRestaurant);
        assertEquals("existingRestaurant", existingRestaurant.getName());

        Restaurant newRestaurant = this.resService.getRestaurantByName("newRestaurant");
        assertNull(newRestaurant);
    }

    @Test
    public void test_addChair() {
        // check that the supplied parameters are validated
        assertThrows(IllegalArgumentException.class, () -> {
            this.resService.addChair(null, new Chair());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            this.resService.addChair(new Restaurant(), null);
        });
        // non-existent restaurant
        assertThrows(RuntimeException.class, () -> {
            this.resService.addChair(new Restaurant("newRestaurant"), new Chair());
        });

        Restaurant res = this.resService.addChair(new Restaurant("existingRestaurant"), new Chair("newChair"));
        assertNotNull(res);
        assertEquals("existingRestaurant", res.getName());

        // same should work for an existing Chair
        res = this.resService.addChair(new Restaurant("existingRestaurant"), new Chair("existingChair"));
        assertNotNull(res);
        assertEquals("existingRestaurant", res.getName());
    }

    @Test
    public void test_getAllRestaurants() {
        List<Restaurant> all = this.resService.getAllRestaurants();
        assertNotNull(all);
        assertEquals(3, all.size());
    }

    @Test
    public void test_getRestaurantById() {
        Restaurant res = this.resService.getById(1L);
        assertNotNull(res);
        assertEquals("restaurant1", res.getName());
    }
}
