package it.uniroma3.siw.service;

import java.time.LocalDate;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.exception.DuplicateFilmException;
import it.uniroma3.siw.exception.FilmAnnoPosterioreFestivalException;
import it.uniroma3.siw.exception.FilmFutureException;
import it.uniroma3.siw.exception.RegistaDateIncompatibiliException;
import it.uniroma3.siw.exception.FilmRegistaNonCompatibileException;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.repository.FestivalRepository;
import it.uniroma3.siw.repository.FilmRepository;
import it.uniroma3.siw.repository.ProiezioneRepository;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
public class FilmService {

    private FilmRepository filmRepository;
	private ProiezioneRepository proiezioneRepository;
	private RecensioneRepository recensioneRepository;
	private FestivalRepository festivalRepository;

    public FilmService(FilmRepository filmRepository, ProiezioneRepository proiezioneRepository, RecensioneRepository recensioneRepository, FestivalRepository festivalRepository) {
        this.filmRepository = filmRepository;
        this.proiezioneRepository=proiezioneRepository;
        this.recensioneRepository=recensioneRepository;
        this.festivalRepository=festivalRepository;
    }

    @Transactional(readOnly = true)
    public Iterable<Film> findAll() {
        return this.filmRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Film findById(Long id) {
        
        Film film = this.filmRepository.findByIdWithProiezioni(id).orElse(null);
        if (film != null) {
            
            this.filmRepository.findByIdWithFestivals(id);
            
            this.filmRepository.findByIdWithRecensioni(id);
        }
        return film;
    }

    @Transactional(readOnly = true)
    public boolean alreadyExists(Film film) {
        if (film.getTitolo() == null || film.getAnno() == null) {
            return false;
        }

        // Se l'id è presente (modifica), esclude il film stesso dal controllo
        if (film.getId() != null) {
            return this.filmRepository.existsByTitoloAndAnnoAndIdNot(film.getTitolo().trim(), film.getAnno(), film.getId());
        }

        return this.filmRepository.existsByTitoloAndAnno(film.getTitolo().trim(), film.getAnno());
    }
    
    
    
    @Transactional
    public Film save(Film film) throws DuplicateFilmException, RegistaDateIncompatibiliException {
        if (this.alreadyExists(film)) {
            throw new DuplicateFilmException();
        }

        if (film.getRegista() != null && film.getAnno() != null) {
            Regista r = film.getRegista();
            if (r.getDataNascita() != null && film.getAnno() < r.getDataNascita().getYear()) {
                throw new RegistaDateIncompatibiliException();
            }
            if (r.getDataMorte() != null && film.getAnno() > r.getDataMorte().getYear()) {
                throw new RegistaDateIncompatibiliException();
            }
        }

        // Controllo coerenza con i festival a cui il film è associato
        if (film.getFestivals() != null && film.getAnno() != null) {
            for (Festival festival : film.getFestivals()) {
                Festival fDb = (festival.getDataFine() != null) 
                        ? festival 
                        : this.festivalRepository.findById(festival.getId()).orElse(null);
                if (fDb != null && fDb.getDataFine() != null && film.getAnno() > fDb.getDataFine().getYear()) {
                    throw new FilmAnnoPosterioreFestivalException();
                }
            }
        }

        return this.filmRepository.save(film);
    }

    @Transactional
    public Film updateFilm(Film filmCorrente, Film filmAggiornato) throws DuplicateFilmException, RegistaDateIncompatibiliException {

        if (!filmCorrente.getTitolo().equalsIgnoreCase(filmAggiornato.getTitolo()) 
                || !filmCorrente.getAnno().equals(filmAggiornato.getAnno())) {
            if (this.alreadyExists(filmAggiornato)) {
                throw new DuplicateFilmException();
            }
        }

        if (filmAggiornato.getRegista() != null && filmAggiornato.getAnno() != null) {
            Regista regista = filmAggiornato.getRegista();
            
            if (regista.getDataNascita() != null && filmAggiornato.getAnno() < regista.getDataNascita().getYear()) {
                throw new RegistaDateIncompatibiliException();
            }
            if (regista.getDataMorte() != null && filmAggiornato.getAnno() > regista.getDataMorte().getYear()) {
                throw new RegistaDateIncompatibiliException();
            }
        }

        // Controllo coerenza con i festival a cui il film è associato
        if (filmAggiornato.getFestivals() != null && filmAggiornato.getAnno() != null) {
            for (Festival festival : filmAggiornato.getFestivals()) {
                Festival fDb = (festival.getDataFine() != null) 
                        ? festival 
                        : this.festivalRepository.findById(festival.getId()).orElse(null);
                if (fDb != null && fDb.getDataFine() != null && filmAggiornato.getAnno() > fDb.getDataFine().getYear()) {
                    throw new FilmAnnoPosterioreFestivalException();
                }
            }
        }

        filmCorrente.setTitolo(filmAggiornato.getTitolo());
        filmCorrente.setAnno(filmAggiornato.getAnno());
        filmCorrente.setGenere(filmAggiornato.getGenere());
        filmCorrente.setDurata(filmAggiornato.getDurata());
        filmCorrente.setPaeseProduzione(filmAggiornato.getPaeseProduzione());
        filmCorrente.setFestivals(filmAggiornato.getFestivals());
        filmCorrente.setRegista(filmAggiornato.getRegista());

        return this.filmRepository.save(filmCorrente);
    }
    
    
    @Transactional
    public void deleteFilm(Long id) {
        Film film = this.filmRepository.findById(id).orElse(null);
        if (film != null) {
            // 1. Rimuovi le proiezioni collegate al film
            this.proiezioneRepository.deleteByFilm(film);
            
            // 2. Rimuovi le recensioni collegate
            this.recensioneRepository.deleteByFilm(film);

            // 3. Sgancia il film dai festival associati
            if (film.getFestivals() != null) {
                for (Festival festival : film.getFestivals()) {
                    festival.getFilm().remove(film);
                }
            }
            
            this.filmRepository.delete(film);
        }
    }
}