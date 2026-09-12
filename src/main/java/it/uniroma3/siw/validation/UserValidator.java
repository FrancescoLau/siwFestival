package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;

@Component
public class UserValidator implements Validator {
    private final UserService userService;

    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target == null) {
            return;
        }
        User user = (User) target;
        
        // Controllo duplicato Email (escludendo l'utente corrente se ha già un ID)
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            User existingUser = this.userService.findByEmail(user.getEmail().trim());
            if (existingUser != null && (user.getId() == null || !user.getId().equals(existingUser.getId()))) {
                errors.rejectValue("email", "user.email.duplicate");
            }
        }
    }
}