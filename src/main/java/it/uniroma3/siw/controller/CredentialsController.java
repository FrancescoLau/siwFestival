package it.uniroma3.siw.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class CredentialsController {

    private final CredentialsService credentialsService;
    private final PasswordEncoder passwordEncoder;

    public CredentialsController(CredentialsService credentialsService, PasswordEncoder passwordEncoder) {
        this.credentialsService = credentialsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/updateCredentials")
    public String showUpdateCredentials(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        if (credentials == null) {
            return "redirect:/login";
        }

        credentials.setPassword("");
        model.addAttribute("credentials", credentials);
        return "updateCredentials";
    }

    @PostMapping("/updateCredentials")
    public String updateCredentials(@ModelAttribute("credentials") Credentials formData,
                                    BindingResult bindingResult,
                                    @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                    @RequestParam(value = "confirmNewPassword", required = false) String confirmNewPassword,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Credentials originalCredentials = this.credentialsService.getCredentials(userDetails.getUsername());
        if (originalCredentials == null) {
            return "redirect:/login";
        }

        // Delega la validazione al service
        this.credentialsService.validateCredentialsUpdate(originalCredentials, formData, oldPassword, confirmNewPassword, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("credentials", formData);
            return "updateCredentials";
        }

        // Se non è stato modificato nulla, ritorna direttamente alla home
        boolean usernameInvariato = formData.getUsername().trim().equals(originalCredentials.getUsername());
        boolean nessunaPassword = (formData.getPassword() == null || formData.getPassword().isBlank());
        if (usernameInvariato && nessunaPassword) {
            return "redirect:/";
        }

        // Delega l'aggiornamento e la ri-autenticazione al service
        this.credentialsService.updateCredentials(originalCredentials, formData, formData.getPassword(), userDetails);

        return "redirect:/";
    }
    
    
}