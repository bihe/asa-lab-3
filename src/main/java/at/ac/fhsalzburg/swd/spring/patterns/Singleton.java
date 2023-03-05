package at.ac.fhsalzburg.swd.spring.patterns;

/**
 * This example of the Singleton Pattern is taken from
 * https://github.com/iluwatar/java-design-patterns/
 */
public final class Singleton {

    // Volatile keyword is used to modify the value of a variable by different threads.
    // It is also used to make classes thread safe. It means that multiple threads can use a method
    // and instance of the classes at the same time without any problem. The volatile keyword
    // can be used either with primitive type or objects.
    // @see https://www.javatpoint.com/volatile-keyword-in-java
    private static volatile Singleton instance;

    private Singleton() {
      // Protect against instantiation via reflection
      if (instance != null) {
        throw new IllegalStateException("Already initialized.");
      }
    }

    /**
     * The instance doesn't get created until the method is called for the first time.
     */
    public static synchronized Singleton getInstance() {
      if (instance == null) {
        synchronized (Singleton.class) {
          if (instance == null) {
            instance = new Singleton();
          }
        }
      }
      return instance;
    }
  }
