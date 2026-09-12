package it.uniroma3.siw.service;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.exception.FestivalDataFineBeforeDataInizioException;
import it.uniroma3.siw.exception.FilmAnnoPosterioreFestivalException;
import it.uniroma3.siw.exception.DuplicateFestivalException;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.repository.FestivalRepository;
import it.uniroma3.siw.repository.FilmRepository;
import it.uniroma3.siw.repository.ProiezioneRepository;

@Service
public class FestivalService {

    private FestivalRepository festivalRepository;
    private FilmRepository filmRepository;
    private ProiezioneRepository proiezioneRepository;
	private ProiezioneService proiezioneService;

    public FestivalService(FestivalRepository festivalRepository, FilmRepository filmRepository, ProiezioneRepository proiezioneRepository, ProiezioneService proiezioneService) {
        this.festivalRepository = festivalRepository;
        this.filmRepository = filmRepository;
        this.proiezioneRepository=proiezioneRepository;
        this.proiezioneService=proiezioneService;
    }

    @Transactional(readOnly = true)
    public Festival getFestival(Long id) {
        Optional<Festival> result = this.festivalRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional(readOnly = true)
    public Iterable<Festival> findAll() {
        return this.festivalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Film> getAllFilms() {
        return (List<Film>) this.filmRepository.findAll();
    }

    @Transactional
    public Festival save(Festival festival) throws DuplicateFestivalException {
        if (festival.getDataInizio() != null) {
            festival.setAnno(festival.getDataInizio().getYear());
        }

        if (festival.getNome() != null && festival.getAnno() != null && festival.getCitta() != null) {
            if (this.festivalRepository.existsByNomeAndAnnoAndCitta(
                    festival.getNome().trim(), festival.getAnno(), festival.getCitta().trim())) {
                throw new DuplicateFestivalException(festival.getNome(), festival.getAnno(), festival.getCitta());
            }
        }

        // 1. Carica i film dal DB UNA SOLA VOLTA e convalida le date
        List<Film> managedFilms = new ArrayList<>();
        if (festival.getFilm() != null) {
            for (Film f : festival.getFilm()) {
                Film filmDb = this.filmRepository.findById(f.getId()).orElse(null);
                if (filmDb != null) {
                    if (festival.getDataFine() != null && filmDb.getAnno() > festival.getDataFine().getYear()) {
                        throw new FilmAnnoPosterioreFestivalException();
                    }
                    managedFilms.add(filmDb);
                }
            }
        }

        // 2. Salva il festival
        festival.setFilm(managedFilms);
        Festival savedFestival = this.festivalRepository.save(festival);

        // 3. Aggiorna il lato inverso riutilizzando i film già in memoria
        for (Film filmDb : managedFilms) {
            if (!filmDb.getFestivals().contains(savedFestival)) {
                filmDb.getFestivals().add(savedFestival);
            }
        }

        return savedFestival;
    }

    @Transactional
    public Festival updateFestival(Festival existingFestival, Festival festivalForm) 
            throws DuplicateFestivalException {
        
        Integer nuovoAnno = (festivalForm.getDataInizio() != null) 
                ? festivalForm.getDataInizio().getYear() 
                : null;

        // Controllo duplicati escludendo se stesso
        if (nuovoAnno != null && this.festivalRepository.existsByNomeAndAnnoAndCittaAndIdNot(
                festivalForm.getNome().trim(), 
                nuovoAnno, 
                festivalForm.getCitta().trim(), 
                existingFestival.getId())) {
            throw new DuplicateFestivalException(festivalForm.getNome(), nuovoAnno, festivalForm.getCitta());
        }

        // Controllo: nessun film uscito dopo la fine del festival
        if (festivalForm.getDataFine() != null && festivalForm.getFilm() != null) {
            int annoFine = festivalForm.getDataFine().getYear();
            for (Film f : festivalForm.getFilm()) {
                Film filmDb = (f.getAnno() != null) ? f : this.filmRepository.findById(f.getId()).orElse(null);
                if (filmDb != null && filmDb.getAnno() != null && filmDb.getAnno() > annoFine) {
                    throw new FilmAnnoPosterioreFestivalException();
                }
            }
        }

        // Aggiornamento campi
        existingFestival.setNome(festivalForm.getNome());
        existingFestival.setCitta(festivalForm.getCitta());
        existingFestival.setDataInizio(festivalForm.getDataInizio());
        existingFestival.setAnno(nuovoAnno);
        existingFestival.setDataFine(festivalForm.getDataFine());
        existingFestival.setDescrizione(festivalForm.getDescrizione());

        // Cancellazione proiezioni fuori date
        this.proiezioneService.cancellaProiezioniFuoriDate(existingFestival);
        
        // Sincronizzazione Film <-> Festival
        List<Film> oldFilms = existingFestival.getFilm() != null ? new ArrayList<>(existingFestival.getFilm()) : new ArrayList<>();
        List<Film> newFilms = festivalForm.getFilm() != null ? festivalForm.getFilm() : new ArrayList<>();

        for (Film oldFilm : oldFilms) {
            if (!newFilms.contains(oldFilm)) {
                Film managedOldFilm = this.filmRepository.findById(oldFilm.getId()).orElse(null);
                if (managedOldFilm != null && managedOldFilm.getFestivals() != null) {
                    managedOldFilm.getFestivals().remove(existingFestival);
                    this.filmRepository.save(managedOldFilm);
                }

                // Annullamento proiezioni con metodi standard
                List<Proiezione> proiezioniDaAnnullare = this.proiezioneRepository.findByFestivalAndFilm(existingFestival, oldFilm);
                for (Proiezione p : proiezioniDaAnnullare) {
                    p.setStato(Proiezione.StatoProiezione.CANCELLED);
                    this.proiezioneRepository.save(p);
                }
            }
        }

        List<Film> managedNewFilms = new ArrayList<>();
        for (Film newFilm : newFilms) {
            Film managedNewFilm = this.filmRepository.findById(newFilm.getId()).orElse(null);
            if (managedNewFilm != null) {
                if (managedNewFilm.getFestivals() == null) {
                    managedNewFilm.setFestivals(new ArrayList<>());
                }
                if (!managedNewFilm.getFestivals().contains(existingFestival)) {
                    managedNewFilm.getFestivals().add(existingFestival);
                    this.filmRepository.save(managedNewFilm);
                }
                managedNewFilms.add(managedNewFilm);
            }
        }

        existingFestival.setFilm(managedNewFilms);
        return this.festivalRepository.save(existingFestival);
    }

    @Transactional
    public void deleteFestival(Long id) {
        Festival festival = this.festivalRepository.findById(id).orElse(null);
        if (festival != null) {
            // 1. Elimina prima le proiezioni del festival
            if (festival.getProiezioni() != null) {
                for (Proiezione p : festival.getProiezioni()) {
                    this.proiezioneRepository.delete(p);
                }
            }

            // 2. Rimuovi il festival dalla lista di ciascun film PRIMA di fare il clear
            if (festival.getFilm() != null) {
                for (Film f : festival.getFilm()) {
                    Film managedFilm = this.filmRepository.findById(f.getId()).orElse(null);
                    if (managedFilm != null && managedFilm.getFestivals() != null) {
                        managedFilm.getFestivals().remove(festival);
                        this.filmRepository.save(managedFilm);
                    }
                }
                festival.getFilm().clear();
            }

            // 3. Elimina il festival
            this.festivalRepository.delete(festival);
        }
    }

	public Festival findById(Long id) {
		return festivalRepository.findById(id).orElse(null);
	}
	
	@Transactional(readOnly = true)
    public List<Film> findFilmsByFestivalId(Long festivalId) {
        if (festivalId == null) {
            return Collections.emptyList();
        }
        Festival festival = this.findById(festivalId);
        return (festival != null && festival.getFilm() != null) 
                ? festival.getFilm() 
                : Collections.emptyList();
    }
}