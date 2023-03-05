package at.ac.fhsalzburg.swd.spring.test.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import at.ac.fhsalzburg.swd.spring.controller.TemplateController;
import at.ac.fhsalzburg.swd.spring.dao.Customer;
import at.ac.fhsalzburg.swd.spring.dao.CustomerRepository;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
public class ControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // in these tests we focus on the controller, so we don't test the repo and mock the needed
    // behavior
    @MockBean
    private CustomerRepository repo;

    @Autowired
    TemplateController myController;

    @BeforeEach // this method will be run before each test
    public void setup() {
        // init webcontext
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void whenControllerInjected_thenNotNull() throws Exception {
        assertThat(myController).isNotNull();
    }

    // HTTP add customer test
    @Test
    public void givenCustomerForm_whenAddCustomer_thenRedirect() throws Exception {
        /*
         * submitting the form object itself does not work in the unit test (even flashAttr does not
         * work :() CustomerForm form = new CustomerForm(); form.setFirstName("Max");
         * form.setLastName("MusterMann"); form.setEMail("max@musterm.ann"); form.setTel("123");
         */

        mvc.perform(MockMvcRequestBuilders.post("/addCustomer").param("firstName", "Max")
                .param("lastName", "Mustermann").param("eMail", "max@musterm.ann")
                .param("tel", "1234").param("birthDate", "2000-01-01")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection());

    }


    // REST Service Test
    @Test
    public void givenCustomer_whenGetCustomer_thenReturnJsonArrayTest() throws Exception {

        Customer customer = new Customer("Max", "Mustermann", "max@muster.com", "123", new Date());

        List<Customer> allCustomers = Arrays.asList(customer);

        // mock the repo: whenever findAll is called, we will get our predefined customer
        given(repo.findAll()).willReturn(allCustomers);

        // call REST service and check
        mvc.perform(get("/api/customers").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].lastName", is(customer.getLastName())));


    }



}
