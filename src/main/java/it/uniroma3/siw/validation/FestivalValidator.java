package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Festival;

@Component
public class FestivalValidator implements Validator {

    @Override
    public void validate(Object target, Errors errors) {
        Festival festival = (Festival) target;

        // Controllo coerenza date: dataFine deve essere successiva a dataInizio
        if (festival.getDataInizio() != null && festival.getDataFine() != null) {
            if (!festival.getDataFine().isAfter(festival.getDataInizio())) {
                errors.rejectValue("dataFine", "festival.dataFine.invalid");
            }
        }
       
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Festival.class.equals(clazz);
    }
}