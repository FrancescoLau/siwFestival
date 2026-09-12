package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Sala;
import it.uniroma3.siw.service.SalaService;

@Component
public class SalaValidator implements Validator {

    private final SalaService salaService;

    public SalaValidator(SalaService salaService) {
        this.salaService = salaService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Sala.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Sala sala = (Sala) target;
        if (this.salaService.alreadyExists(sala)) {
            errors.reject("sala.duplicate");
        }
    }
}