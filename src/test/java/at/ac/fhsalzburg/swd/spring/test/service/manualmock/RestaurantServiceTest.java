package at.ac.fhsalzburg.swd.spring.test.service.manualmock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.ac.fhsalzburg.swd.spring.dao.Restaurant;
import at.ac.fhsalzburg.swd.spring.services.RestaurantService;
import at.ac.fhsalzburg.swd.spring.services.RestaurantServiceImpl;

public class RestaurantServiceTest {

    private RestaurantService resService;

    // the @BeforeEach annotation tells JUnit to execute the following logic
    @BeforeEach
    void setUp()
    {
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
            = new RestaurantServiceImpl(new MockRestaurantRepository(), new MockChairRepository());
    }

    @Test
    public void test_createRestauranet()
    {
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
}
