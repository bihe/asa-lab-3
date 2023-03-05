package at.ac.fhsalzburg.swd.spring.test.patterns;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import at.ac.fhsalzburg.swd.spring.patterns.ObservedObject;
import at.ac.fhsalzburg.swd.spring.patterns.Observer;

public class ObserverTest {

    /**
     * The class is used to "catch" the messages sent to all observers
     * this is the beauty of interfaces and unit-testing ;)
     */
    private class TestObserver implements Observer {

        private String receivedMessage;

        @Override
        public void update(String message) {
            this.receivedMessage = message;
        }

        public String getReceivedMessage() {
            return this.receivedMessage;
        }
    }

    @Test
    public void testObserver_notify() {
        ObservedObject observed = new ObservedObject();
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();
        String messageToSend = "update to all observers";

        observed.addObserver(observer1);
        observed.addObserver(observer2);
        observed.notify(messageToSend);

        assertEquals(messageToSend, observer1.getReceivedMessage());
        assertEquals(messageToSend, observer2.getReceivedMessage());

        // important! forgetting this is always the source for memory-leaks!!
        observed.removeObserver(observer1);
        observed.removeObserver(observer2);
    }
}
