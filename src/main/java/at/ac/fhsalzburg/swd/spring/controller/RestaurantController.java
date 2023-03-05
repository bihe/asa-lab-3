package at.ac.fhsalzburg.swd.spring.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import at.ac.fhsalzburg.swd.spring.dao.Restaurant;
import at.ac.fhsalzburg.swd.spring.services.RestaurantService;

// Putting the RequestMapping on top of the Controller tells all other methods
// that the path always starts with "/restaurant"
@RequestMapping("/restaurant")
@Controller
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    private RestaurantService resService;

    // The dependency for this controller is injected via the constructor.
    // This approach "feels" very natural, because it would work without
    // a dependency-injection container as well - so less magic
    // We do not need the @Autowired annotation, because spring is clever
    // and figures this out.
    @Autowired
    public RestaurantController(RestaurantService resService) {
        this.resService = resService;
    }

    // The empty request mapping takes care of the paths
    //  - /restaurant
    //  - /restaurant/
    // with this approach the "trailing-slash" issue is gone --> https://delante.co/what-is-trailing-slash/
    @RequestMapping("")
    public String index(Model model) {
        logger.debug("show the restaurant start page");

        List<Restaurant> all = this.resService.getAllRestaurants();
        // the model is passed on to the Thymeleaf template and can be used with the defined name
        model.addAttribute("restaurants", all);
        return "restaurant/index";
    }

    // When the /add path is called via GET (typical click/link/...) we show the form
    @RequestMapping("/add")
    public String showAddView(Model model) {
        RestaurantForm form = new RestaurantForm();
        model.addAttribute("restaurantForm", form);
        return "restaurant/add";
    }

    @RequestMapping("/edit/{id}")
    public String showEditView(Model model, @PathVariable Long id) {
        if (id <= 0) {
            model.addAttribute("error", "Could not access the given restaurant!");
            return "restaurant";
        }

        Restaurant res = this.resService.getById(id);
        if (res == null) {
            throw new RestaurantNotFoundException();
        }

        RestaurantForm form = new RestaurantForm();
        form.setId(res.getId());
        form.setName(res.getName());
        model.addAttribute("restaurantForm", form);
        return "restaurant/add";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String handleAdd(Model model, @ModelAttribute("restaurantForm") RestaurantForm form) {
        String name = form.getName();
        Long id = form.getId();

        // server-side validation of the given name
        if ("".equals(name) || name == null) {
            model.addAttribute("error", "The name of the restaurant is missing!");
            return "restaurant/add";
        }

        try {
            // if no id is supplied we crate a new restaurant
            if (id != null && id > 0) {
                Restaurant res = new Restaurant(name);
                res.setId(id);
                this.resService.save(res);
            } else {
                Restaurant res = this.resService.create(name);
                if (res == null || res.getId() == 0) {
                    model.addAttribute("error", "Could not create the restaurant!");
                    return "restaurant/add";
                }
            }
        } catch (Exception EX) {
            model.addAttribute("error", "There was an error saving the restaurant: " + EX.getMessage());
            return "restaurant/add";
        }

        return "redirect:/restaurant";
    }
}
