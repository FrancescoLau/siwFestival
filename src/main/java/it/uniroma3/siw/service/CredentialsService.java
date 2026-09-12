package it.uniroma3.siw.service;


import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {
   
	private CredentialsRepository credentialsRepository;
    private PasswordEncoder passwordEncoder;
    
    public CredentialsService(CredentialsRepository credentialsRepository, PasswordEncoder passwordEncoder) {
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Credentials getCredentials(Long id) {
        return this.credentialsRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Credentials getCredentials(String username) {
        return this.credentialsRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        return this.credentialsRepository.save(credentials);
    }

    @Transactional(readOnly = true)
    public Optional<Credentials> findByUsername(String username) {
        return this.credentialsRepository.findByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return this.credentialsRepository.existsByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public void validateCredentialsUpdate(Credentials originalCredentials,
                                          Credentials formData,
                                          String oldPassword,
                                          String confirmNewPassword,
                                          BindingResult bindingResult) {

        boolean usernameInvariato = formData.getUsername() != null 
                && formData.getUsername().trim().equals(originalCredentials.getUsername());

        // 1. Controllo validità Username
        if (formData.getUsername() == null || formData.getUsername().isBlank()) {
            bindingResult.rejectValue("username", "NotBlank.credentials.username", "Lo username non può essere vuoto.");
        } else if (!usernameInvariato && this.credentialsRepository.existsByUsername(formData.getUsername().trim())) {
            bindingResult.rejectValue("username", "credentials.duplicate", "Questo username è già registrato.");
        }

        // 2. Controllo validità Password (se l'utente sta provando a cambiarla)
        boolean inseritaPassword = (oldPassword != null && !oldPassword.isBlank())
                || (formData.getPassword() != null && !formData.getPassword().isBlank())
                || (confirmNewPassword != null && !confirmNewPassword.isBlank());

        if (inseritaPassword) {
            if (oldPassword == null || oldPassword.isBlank()) {
                bindingResult.rejectValue("password", "oldPassword.required", "Inserisci la password attuale per procedere.");
            } else if (!this.passwordEncoder.matches(oldPassword, originalCredentials.getPassword())) {
                bindingResult.rejectValue("password", "oldPassword.invalid", "La password attuale non è corretta.");
            }

            if (formData.getPassword() == null || formData.getPassword().isBlank()) {
                bindingResult.rejectValue("password", "newPassword.required", "La nuova password è obbligatoria.");
            } else if (formData.getPassword().length() < 4) {
                bindingResult.rejectValue("password", "Size.credentials.password", "La nuova password deve contenere almeno 4 caratteri.");
            }

            if (confirmNewPassword == null || confirmNewPassword.isBlank()) {
                bindingResult.rejectValue("password", "confirmPassword.required", "Ripeti la nuova password.");
            } else if (formData.getPassword() != null && !formData.getPassword().equals(confirmNewPassword)) {
                bindingResult.rejectValue("password", "newPassword.mismatch", "La nuova password e la conferma non coincidono.");
            }
        }
    }

    /**
     * Applica le modifiche, persiste le credenziali e aggiorna la sessione di sicurezza attiva.
     */
    @Transactional
    public void updateCredentials(Credentials originalCredentials,
                                  Credentials formData,
                                  String newPassword,
                                  UserDetails principal) {

        originalCredentials.setUsername(formData.getUsername().trim());

        if (newPassword != null && !newPassword.isBlank()) {
            originalCredentials.setPassword(this.passwordEncoder.encode(newPassword));
        }

        this.credentialsRepository.save(originalCredentials);

        // Istanziazione esplicita di org.springframework.security.core.userdetails.User
        org.springframework.security.core.userdetails.UserDetails newUserDetails = 
                new org.springframework.security.core.userdetails.User(
                        originalCredentials.getUsername(),
                        originalCredentials.getPassword(),
                        principal.getAuthorities()
                );

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                null,
                newUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
    
    
}
