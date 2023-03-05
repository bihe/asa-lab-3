package at.ac.fhsalzburg.swd.spring.services;

import java.util.Date;

import at.ac.fhsalzburg.swd.spring.dao.Customer;

public interface CustomerServiceInterface {

    public abstract String doSomething();

    public abstract boolean addCustomer(String firstName, String lastName, String eMail, String Tel,
            Date BirthDate);

    public abstract boolean addCustomer(Customer costumer);

    public abstract Iterable<Customer> getAll();

    public abstract Customer getById(Long id);

    public abstract void deleteById(Long id);

    public abstract boolean hasCredit(Customer customer);

}
