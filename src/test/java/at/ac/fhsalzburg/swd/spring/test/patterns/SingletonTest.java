package at.ac.fhsalzburg.swd.spring.test.patterns;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.jupiter.api.Test;

import at.ac.fhsalzburg.swd.spring.dao.Chair;
import at.ac.fhsalzburg.swd.spring.patterns.Singleton;

public class SingletonTest {

    @Test
    public void testSingletonInstance_same() {
        // when we get an instance of the Singleton object it is ALWAYS the same instance
        Singleton sing1 = Singleton.getInstance();
        Singleton sing2 = Singleton.getInstance();
        assertSame(sing1, sing2);

        // for comparison ordinary objects
        Chair c1 = new Chair();
        Chair c2 = new Chair();
        assertNotSame(c1, c2);
    }
}
