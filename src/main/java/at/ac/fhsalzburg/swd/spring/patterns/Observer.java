package at.ac.fhsalzburg.swd.spring.patterns;

/**
 * Each observer needs to implement the interface to get updates
 */
public interface Observer {
    void update(String message);
}
