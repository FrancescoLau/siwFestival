package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.service.RecensioneService;

@Component
public class RecensioneValidator implements Validator {

    private final RecensioneService recensioneService;

    public RecensioneValidator(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Recensione recensione = (Recensione) target;

        // 1. Controllo range voto
        if (recensione.getVoto() != null && (recensione.getVoto() < 1 || recensione.getVoto() > 5)) {
            errors.rejectValue("voto", "recensione.voto.range");
        }

        // 2. Controllo duplicato (se la recensione non ha ancora un ID, ossia in fase di inserimento)
        if (recensione.getId() == null && recensione.getUtente() != null && recensione.getFilm() != null) {
            if (this.recensioneService.existsByUtenteAndFilm(recensione.getUtente(), recensione.getFilm())) {
                errors.reject("recensione.duplicate");
            }
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Recensione.class.equals(clazz);
    }
}