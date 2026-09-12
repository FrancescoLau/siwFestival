package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.service.FilmService;

@Component
public class FilmValidator implements Validator {

    private final FilmService filmService;

    public FilmValidator(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Film film = (Film) target;     
        
        //  Controllo coerenza date regista in totale sicurezza contro i NullPointerException
        if (film.getAnno() != null && film.getRegista() != null) {
            Regista regista = film.getRegista();

            // Film uscito prima della nascita del regista
            if (regista.getDataNascita() != null && film.getAnno() < regista.getDataNascita().getYear()) {
                errors.reject("film.regista.incompatibile");
            }

            // Film uscito dopo la morte del regista (solo se la data di morte è presente)
            if (regista.getDataMorte() != null && film.getAnno() > regista.getDataMorte().getYear()) {
                errors.reject("film.regista.incompatibile");
            }
            
         
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Film.class.equals(clazz);
    }
}