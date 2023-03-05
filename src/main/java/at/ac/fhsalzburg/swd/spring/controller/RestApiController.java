package at.ac.fhsalzburg.swd.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.fhsalzburg.swd.spring.dao.Customer;
import at.ac.fhsalzburg.swd.spring.services.CustomerServiceInterface;

@RestController
@RequestMapping("/api")
public class RestApiController {

    @Autowired private CustomerServiceInterface customerService;

    @GetMapping("/customers")
    public @ResponseBody List<Customer> allUsers() {

        return (List<Customer>) customerService.getAll();
    }

    @RequestMapping(value = {"/customers/{id}"}, method = RequestMethod.GET)
    public @ResponseBody Customer getCustomer(@PathVariable long id) {
        Customer customer = customerService.getById(id);

        return customer;
    }

    @RequestMapping(value = {"/customers/{id}"}, method = RequestMethod.PUT)
    public String setCustomer(@RequestBody Customer customer) {

        customerService.addCustomer(customer);

        return "redirect:/customers";
    }

    // @DeleteMapping annotation maps HTTP DELETE requests onto
    // specific handler methods. It is a composed annotation that
    // acts as a shortcut for @RequestMapping(method =
    // RequestMethod.DELETE).
    @DeleteMapping("/customers/{id}")
    public String delete(@PathVariable String id) {
        Long customerid = Long.parseLong(id);
        customerService.deleteById(customerid);
        return "redirect:/customers";
    }
}
