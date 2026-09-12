package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.repository.RegistaRepository;

@Component
public class RegistaValidator implements Validator {

    private final RegistaRepository registaRepository;

    public RegistaValidator(RegistaRepository registaRepository) {
        this.registaRepository = registaRepository;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Regista regista = (Regista) target;
        
        // Controllo duplicati (errore globale)
        if (regista.getNome() != null && regista.getCognome() != null && regista.getDataNascita() != null) {
            String nome = regista.getNome().trim();
            String cognome = regista.getCognome().trim();

            if (regista.getId() == null) {
                if (this.registaRepository.existsByNomeAndCognomeAndDataNascita(nome, cognome, regista.getDataNascita())) {
                    errors.reject("regista.duplicate");
                }
            } else {
                if (this.registaRepository.existsByNomeAndCognomeAndDataNascitaAndIdNot(nome, cognome, regista.getDataNascita(), regista.getId())) {
                    errors.reject("regista.duplicate");
                }
            }
        }
        
        // Controllo coerenza date (associato al campo dataMorte)
        if (regista.getDataNascita() != null && regista.getDataMorte() != null) {
            if (regista.getDataMorte().isBefore(regista.getDataNascita())) {
                errors.rejectValue("dataMorte", "regista.dataMorte");
            }
        }
        
        
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Regista.class.equals(clazz);
    }
}