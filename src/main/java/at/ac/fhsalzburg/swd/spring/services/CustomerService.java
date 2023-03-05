package at.ac.fhsalzburg.swd.spring.services;


import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.fhsalzburg.swd.spring.dao.Customer;
import at.ac.fhsalzburg.swd.spring.dao.CustomerRepository;


@Service
public class CustomerService implements CustomerServiceInterface {

    int i;

    @Autowired
    private CustomerRepository repo;

    public CustomerService() {
        i = 0;
    }

    @Override
    public String doSomething() {
        i++;
        return Integer.toString(i);
    }

    @Override
    public boolean addCustomer(String firstName, String lastName, String eMail, String Tel,
            Date BirthDate) {

        if (firstName != null && firstName.length() > 0 //
                && lastName != null && lastName.length() > 0) {
            Customer newCustomer = new Customer(firstName, lastName, eMail, Tel, BirthDate);

            repo.save(newCustomer);
            return true;
        }

        return false;

    }

    @Override
    public boolean addCustomer(Customer customer) {

        repo.save(customer);

        return false;

    }

    @Override
    public Iterable<Customer> getAll() {
        return repo.findAll();
    }

    @Override
    public Customer getById(Long id) {
        return repo.findById(id).get();
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public boolean hasCredit(Customer customer) {
        if (customer.getCredit() > 0)
            return true;
        else
            return false;
    }
}
