package at.ac.fhsalzburg.swd.spring.controller;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import at.ac.fhsalzburg.swd.spring.services.CustomerServiceInterface;

@Controller // marks the class as a web controller, capable of handling the HTTP requests. Spring
            // will look at the methods of the class marked with the @Controller annotation and
            // establish the routing table to know which methods serve which endpoints.
public class TemplateController {

    // Dependency Injection
    // ----------------------------------------------------------------------

    @Autowired
    CustomerServiceInterface customerService;

    // HTTP Request Mappings GET/POST/... and URL Paths
    // ----------------------------------------------------------------------


    @RequestMapping("/") // The @RequestMapping(method = RequestMethod.GET, value = "/path")
                         // annotation specifies a method in the controller that should be
                         // responsible for serving the HTTP request to the given path. Spring will
                         // work the implementation details of how it's done. You simply specify the
                         // path value on the annotation and Spring will route the requests into the
                         // correct action methods:
                         // https://springframework.guru/spring-requestmapping-annotation/#:~:text=%40RequestMapping%20is%20one%20of%20the,map%20Spring%20MVC%20controller%20methods.
    public String index(Model model, HttpSession session) {

        model.addAttribute("halloNachricht", "welchem to SWD lab");
        model.addAttribute("customers", customerService.getAll());

        return "index";
    }

    @RequestMapping(value = {"/addCustomer"}, method = RequestMethod.GET)
    public String showAddPersonPage(Model model) {
        CustomerForm customerForm = new CustomerForm();
        model.addAttribute("customerForm", customerForm);

        return "addCustomer";
    }


    @RequestMapping(value = {"/addCustomer"}, method = RequestMethod.POST)
    public String addCustomer(Model model, //
            @ModelAttribute("customerForm") CustomerForm customerForm) { // The @ModelAttribute is
                                                                         // an annotation that binds
                                                                         // a method parameter or
                                                                         // method return value to a
                                                                         // named model attribute
                                                                         // and then exposes it to a
                                                                         // web view:
                                                                         // https://www.baeldung.com/spring-mvc-and-the-modelattribute-annotation
        String firstName = customerForm.getFirstName();
        String lastName = customerForm.getLastName();
        String eMail = customerForm.getEMail();
        String tel = customerForm.getTel();
        Date birth = customerForm.getBirthDate();

        customerService.addCustomer(firstName, lastName, eMail, tel, birth);

        return "redirect:/";
    }
}
