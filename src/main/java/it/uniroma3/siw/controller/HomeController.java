package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.FestivalService;
import it.uniroma3.siw.service.UserService;
@Controller
public class HomeController {
	
private FestivalService festivalService;
private CredentialsService credentialsService;
private UserService userService;
public HomeController(FestivalService festivalService, CredentialsService credentialsService, UserService userService) {
	this.festivalService=festivalService;
	this.credentialsService=credentialsService;
	this.userService=userService;
}


@GetMapping("/")
public String home(Model model) {
	return "homepage";
}
}
