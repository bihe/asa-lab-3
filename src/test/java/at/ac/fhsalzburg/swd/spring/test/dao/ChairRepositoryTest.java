package at.ac.fhsalzburg.swd.spring.test.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import at.ac.fhsalzburg.swd.spring.dao.Chair;
import at.ac.fhsalzburg.swd.spring.dao.ChairRepository;

// https://www.baeldung.com/spring-boot-testing
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class ChairRepositoryTest {

    @Autowired
    private ChairRepository repo;

    @Test
    public void whenChairCreated_thenIdFilled() {
        // given
        Chair chair = new Chair("test-chair");
        // when
        Chair saved = repo.save(chair);
        // then
        assertTrue(saved.getId() > 0);
    }

    @Test
    public void validateCRUDmethods() {
        // given
        Chair chair1 = new Chair("test-chair");
        Chair chair2 = new Chair("test2");
        List<Chair> chairs = new ArrayList<>();
        chairs.add(chair1);
        chairs.add(chair2);

        repo.saveAll(chairs);

        var allChairs = repo.findAll();
        assertNotNull(chairs, "no chairs found");
        assertEquals(2, ((Collection<Chair>)allChairs).size());

        Optional<Chair> findTest2 = repo.findByName("test2");
        assertTrue(findTest2.isPresent());

        Chair test2 = findTest2.get();
        test2.setName("test2update");
        repo.save(test2);

        findTest2 = repo.findByName("test2");
        assertFalse(findTest2.isPresent());

        Optional<Chair> findTest2Update = repo.findByName("test2update");
        assertTrue(findTest2Update.isPresent());
        assertEquals("test2update", findTest2Update.get().getName());
        repo.deleteAll();
        allChairs = repo.findAll();
        assertEquals(0, ((Collection<Chair>)allChairs).size());
    }
}
