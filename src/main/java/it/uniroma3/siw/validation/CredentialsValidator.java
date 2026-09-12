package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;


import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class CredentialsValidator implements Validator {

    private final CredentialsService credentialsService;
    private final PasswordEncoder passwordEncoder;

    public CredentialsValidator(CredentialsService credentialsService, PasswordEncoder passwordEncoder) {
        this.credentialsService = credentialsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Credentials.class.equals(aClass); // Ritorna se la classe è supportata
    }

    @Override
    public void validate(Object target, Errors errors) {
        Credentials credentials = (Credentials) target;
       
        if (credentials.getUsername() != null && !credentials.getUsername().isBlank()) {
            if (this.credentialsService.existsByUsername(credentials.getUsername().trim())) {
                errors.rejectValue("username", "credentials.username.duplicate");
            }
        }

        if (credentials.getPassword() != null && !credentials.getPassword().isBlank() && credentials.getPassword().length() < 4) {
            errors.rejectValue("password", "Size.credentials.password");
        }
        
        
    }
}