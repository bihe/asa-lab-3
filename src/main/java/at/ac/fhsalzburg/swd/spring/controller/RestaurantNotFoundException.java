package at.ac.fhsalzburg.swd.spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "restaurant not found")
public class RestaurantNotFoundException extends RuntimeException {

}
