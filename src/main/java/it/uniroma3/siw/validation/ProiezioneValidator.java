package it.uniroma3.siw.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.service.ProiezioneService;

@Component
public class ProiezioneValidator implements Validator {

    private final ProiezioneService proiezioneService;

    public ProiezioneValidator(ProiezioneService proiezioneService) {
        this.proiezioneService = proiezioneService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Proiezione.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Proiezione proiezione = (Proiezione) target;

        // 1. Controllo date interne al festival
        if (proiezione.getFestival() != null && proiezione.getData() != null) {
            if (proiezione.getData().isBefore(proiezione.getFestival().getDataInizio()) ||
                proiezione.getData().isAfter(proiezione.getFestival().getDataFine())) {
                errors.reject("proiezione.dataFestival");
            }
        }
    }
}