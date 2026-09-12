package it.uniroma3.siw.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.validation.CredentialsValidator;
import it.uniroma3.siw.validation.UserValidator;
import jakarta.validation.Valid;

@Controller
public class UserController {

    private final CredentialsService credentialsService;
    private final PasswordEncoder passwordEncoder;
    private final CredentialsValidator credentialsValidator;
    private final UserValidator userValidator;

    public UserController(CredentialsService credentialsService, 
                          PasswordEncoder passwordEncoder,
                          CredentialsValidator credentialsValidator,
                          UserValidator userValidator) {
        this.credentialsService = credentialsService;
        this.passwordEncoder = passwordEncoder;
        this.credentialsValidator = credentialsValidator;
        this.userValidator = userValidator;
    }

    // Mostra la vista di Login
    @GetMapping(value = {"/login", "/login.html"})
    public String showLoginForm() {
        return "login";
    }

    // Mostra la form di Registrazione con modello inizializzato
    @GetMapping(value = {"/register", "/register.html"})
    public String showRegisterForm(Model model) {
        Credentials credentials = new Credentials();
        credentials.setUser(new User());
        model.addAttribute("credentials", credentials);
        return "register";
    }

    // Gestisce l'invio della form di Registrazione
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("credentials") Credentials credentials, 
                               BindingResult bindingResult, 
                               Model model) {

        // Validazione custom su credenziali e dati anagrafici
        this.credentialsValidator.validate(credentials, bindingResult);
        if (credentials.getUser() != null) {
            this.userValidator.validate(credentials.getUser(), bindingResult);
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Cifratura password e assegnazione del ruolo base
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        credentials.setRole(Credentials.DEFAULT_ROLE);

        // Salvataggio tramite Service (salva anche User per via del CascadeType.ALL)
        this.credentialsService.saveCredentials(credentials);

        return "redirect:/login";
    }
}