package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.exception.ProiezioneFilmFuturoException;
import it.uniroma3.siw.exception.SalaNonDisponibileException;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.repository.FestivalRepository;
import it.uniroma3.siw.repository.ProiezioneRepository;

@Service
public class ProiezioneService {

    private ProiezioneRepository proiezioneRepository;
    private FestivalRepository festivalRepository;

    public ProiezioneService(ProiezioneRepository proiezioneRepository, FestivalRepository festivalRepository) {
        this.proiezioneRepository = proiezioneRepository;
        this.festivalRepository=festivalRepository;
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findAll() {
        return (List<Proiezione>) this.proiezioneRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Proiezione getProiezione(Long id) {
        Optional<Proiezione> p = this.proiezioneRepository.findById(id);
        return p.orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Proiezione> findByFestival(Festival festival) {
        return this.proiezioneRepository.findByFestival(festival);
    }
       
    @Transactional(readOnly = true)
    public boolean isSalaDisponibile(Proiezione nuovaProiezione) {
        if (nuovaProiezione.getSala() == null || nuovaProiezione.getData() == null 
                || nuovaProiezione.getOra() == null || nuovaProiezione.getFilm() == null) {
            return true;
        }

        Film filmNuovo = nuovaProiezione.getFilm();
        int durataNuova = (filmNuovo.getDurata() != null) ? filmNuovo.getDurata() : 120;

        LocalDateTime inizioNuovo = LocalDateTime.of(nuovaProiezione.getData(), nuovaProiezione.getOra());
        LocalDateTime fineNuovo = inizioNuovo.plusMinutes(durataNuova);

        // Recupera le proiezioni sia del giorno stesso che del giorno precedente (per film a cavallo di mezzanotte)
        List<Proiezione> proiezioni = this.proiezioneRepository.findBySalaAndDataBetweenAndStatoNot(
                nuovaProiezione.getSala(),
                nuovaProiezione.getData().minusDays(1),
                nuovaProiezione.getData(),
                Proiezione.StatoProiezione.CANCELLED);

        for (Proiezione p : proiezioni) {
            if (nuovaProiezione.getId() != null && nuovaProiezione.getId().equals(p.getId())) {
                continue;
            }

            int durataEsistente = (p.getFilm() != null && p.getFilm().getDurata() != null) 
                    ? p.getFilm().getDurata() 
                    : 120;

            LocalDateTime inizioEsistente = LocalDateTime.of(p.getData(), p.getOra());
            LocalDateTime fineEsistente = inizioEsistente.plusMinutes(durataEsistente);

            // Condizione di overlap temporale: (InizioA < FineB) && (InizioB < FineA)
            if (inizioNuovo.isBefore(fineEsistente) && inizioEsistente.isBefore(fineNuovo)) {
                return false;
            }
        }

        return true;
    }

    @Transactional
    public Proiezione save(Proiezione proiezione) throws SalaNonDisponibileException, ProiezioneFilmFuturoException {
        if (!isSalaDisponibile(proiezione)) {
            throw new SalaNonDisponibileException(proiezione.getSala().getNome());
        }

        if (proiezione.getData() != null && proiezione.getFilm() != null 
                && proiezione.getFilm().getAnno() != null 
                && proiezione.getData().getYear() < proiezione.getFilm().getAnno()) {
            throw new ProiezioneFilmFuturoException(proiezione, proiezione.getFilm());
        }

        if (proiezione.getStato() == null) {
            proiezione.setStato(Proiezione.StatoProiezione.SCHEDULED);
        }
        return this.proiezioneRepository.save(proiezione);
    }

    @Transactional
    public Proiezione updateProiezione(Proiezione proiezioneCorrente, Proiezione form) 
            throws SalaNonDisponibileException, ProiezioneFilmFuturoException {
        // 1. Verifica disponibilità sala
        if (!isSalaDisponibile(form)) {
            String nomeSala = (form.getSala() != null && form.getSala().getNome() != null) 
                    ? form.getSala().getNome() 
                    : (proiezioneCorrente.getSala() != null ? proiezioneCorrente.getSala().getNome() : "");
            throw new SalaNonDisponibileException(nomeSala);
        }

        // 2. Verifica anno del film
        if (form.getData() != null && form.getFilm() != null 
                && form.getFilm().getAnno() != null 
                && form.getData().getYear() < form.getFilm().getAnno()) {
            throw new ProiezioneFilmFuturoException(form, form.getFilm());
        }

        // 3. Aggiorna i campi
        proiezioneCorrente.setData(form.getData());
        proiezioneCorrente.setOra(form.getOra());
        proiezioneCorrente.setStato(form.getStato());
        proiezioneCorrente.setFestival(form.getFestival());
        proiezioneCorrente.setFilm(form.getFilm());
        proiezioneCorrente.setSala(form.getSala());

        return this.proiezioneRepository.save(proiezioneCorrente);
    }
    
    @Transactional
    public void deleteProiezione(Long id) {
        Proiezione proiezione = this.proiezioneRepository.findById(id).orElse(null);
        if (proiezione != null) {
            // Sgancia i riferimenti bidirezionali in memoria se presenti
            if (proiezione.getFilm() != null && proiezione.getFilm().getProiezioni() != null) {
                proiezione.getFilm().getProiezioni().remove(proiezione);
            }
            this.proiezioneRepository.delete(proiezione);
        }
    }
    
    @Transactional(readOnly = true)
    public boolean isFuoriDateFestival(Proiezione proiezione, Festival festival) {
        if (proiezione == null || festival == null 
                || proiezione.getData() == null 
                || festival.getDataInizio() == null 
                || festival.getDataFine() == null) {
            return false;
        }
        return proiezione.getData().isBefore(festival.getDataInizio()) 
            || proiezione.getData().isAfter(festival.getDataFine());
    }

    @Transactional
    public void cancellaProiezioniFuoriDate(Festival festival) {
        if (festival == null) {
            return;
        }

        List<Proiezione> proiezioni = this.proiezioneRepository.findByFestival(festival);
        if (proiezioni != null) {
            for (Proiezione p : proiezioni) {
                if (isFuoriDateFestival(p, festival) && p.getStato() != Proiezione.StatoProiezione.CANCELLED) {
                    p.setStato(Proiezione.StatoProiezione.CANCELLED);
                    this.proiezioneRepository.save(p);
                }
            }
        }
    }
}