package at.ac.fhsalzburg.swd.spring.patterns;

import java.util.ArrayList;
import java.util.List;

public class ObservedObject {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer obs) {
        this.observers.add(obs);
    }

    public void removeObserver(Observer obs) {
        this.observers.remove(obs);
    }

    public void notify(String message) {
        for(Observer o : this.observers) {
            o.update(message);
        }
    }
}
