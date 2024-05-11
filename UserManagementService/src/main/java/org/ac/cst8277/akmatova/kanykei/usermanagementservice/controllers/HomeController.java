package org.ac.cst8277.akmatova.kanykei.usermanagementservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @GetMapping("/")
  public String home() {
    return "login";
  }

  @GetMapping("/error")
  public String error(HttpServletRequest request) {
    String message = (String) request.getSession().getAttribute("error.message");
    request.getSession().removeAttribute("error.message");
    return message;
  }
}
