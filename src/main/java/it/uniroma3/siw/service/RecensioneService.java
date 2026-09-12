package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.FilmRepository;
import it.uniroma3.siw.repository.RecensioneRepository;

@Service
public class RecensioneService {

    private final RecensioneRepository recensioneRepository;
    private final FilmRepository filmRepository;
    private final CredentialsService credentialsService;

    public RecensioneService(RecensioneRepository recensioneRepository,
                             FilmRepository filmRepository,
                             CredentialsService credentialsService) {
        this.recensioneRepository = recensioneRepository;
        this.filmRepository = filmRepository;
        this.credentialsService = credentialsService;
    }

    @Transactional(readOnly = true)
    public boolean existsByUtenteAndFilm(User utente, Film film) {
        if (utente == null || film == null || utente.getId() == null || film.getId() == null) {
            return false;
        }
        return this.recensioneRepository.existsByUtenteIdAndFilmId(utente.getId(), film.getId());
    }

    @Transactional(readOnly = true)
    public Optional<Recensione> findByUtenteAndFilm(User utente, Film film) {
        if (utente == null || film == null || utente.getId() == null || film.getId() == null) {
            return Optional.empty();
        }
        return this.recensioneRepository.findByUtenteIdAndFilmId(utente.getId(), film.getId());
    }

    @Transactional(readOnly = true)
    public Recensione getRecensione(Long id) {
        return this.recensioneRepository.findById(id).orElse(null);
    }

    /**
     * Verifica se l'utente ha i permessi per inserire una recensione (ruolo DEFAULT e nessuna recensione già presente per il film).
     */
    @Transactional(readOnly = true)
    public boolean canUserReview(String username, Film film) {
        if (username == null || film == null) {
            return false;
        }
        Credentials credentials = this.credentialsService.getCredentials(username);
        if (credentials == null || credentials.getUser() == null) {
            return false;
        }
        if (!Credentials.DEFAULT_ROLE.equals(credentials.getRole())) {
            return false;
        }
        return !existsByUtenteAndFilm(credentials.getUser(), film);
    }

    /**
     * Recupera l'utente associato alle credenziali.
     */
    @Transactional(readOnly = true)
    public User getReviewer(String username) {
        Credentials credentials = this.credentialsService.getCredentials(username);
        return (credentials != null) ? credentials.getUser() : null;
    }
    
    public boolean isReviewOwner(Recensione recensione, String username) {
        if (recensione == null || username == null) {
            return false;
        }
        Credentials credentials = this.credentialsService.getCredentials(username);
        if (credentials == null) {
            return false;
        }
        // Se è ADMIN ha sempre i permessi di gestione
        if (Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
            return true;
        }
        // Altrimenti deve essere l'autore effettivo
        return recensione.getUtente() != null && credentials.getUser() != null && recensione.getUtente().getId().equals(credentials.getUser().getId());
    }

    /**
     * Salva la nuova recensione mantenendo la sincronizzazione bidirezionale.
     */
    @Transactional
    public Recensione save(Recensione formRecensione, User utente, Film film) {
        if (utente == null || film == null || utente.getId() == null || film.getId() == null) {
            return null;
        }

        if (this.recensioneRepository.existsByUtenteIdAndFilmId(utente.getId(), film.getId())) {
            return null;
        }

        Recensione nuovaRecensione = new Recensione();
        nuovaRecensione.setId(null);
        nuovaRecensione.setVoto(formRecensione.getVoto());
        nuovaRecensione.setTesto(formRecensione.getTesto());
        nuovaRecensione.setData(LocalDate.now());
        nuovaRecensione.setUtente(utente);
        nuovaRecensione.setFilm(film);

        if (film.getRecensioni() == null) {
            film.setRecensioni(new ArrayList<>());
        }
        film.getRecensioni().add(nuovaRecensione);

        return this.recensioneRepository.save(nuovaRecensione);
    }

    /**
     * Aggiorna una recensione solo se l'utente che effettua l'operazione ne è il proprietario.
     */
    @Transactional
    public Long updateRecensione(Long id, Recensione formRecensione, String username) {
        Recensione existingRecensione = getRecensione(id); 
        if (existingRecensione == null || !isReviewOwner(existingRecensione, username)) { 
            return null;
        }

        existingRecensione.setVoto(formRecensione.getVoto()); 
        existingRecensione.setTesto(formRecensione.getTesto()); 
        existingRecensione.setData(LocalDate.now()); 
        this.recensioneRepository.save(existingRecensione); 

        return (existingRecensione.getFilm() != null) ? existingRecensione.getFilm().getId() : null;
    }

    /**
     * Elimina una recensione e sincronizza la lista del Film solo se l'utente è il legittimo proprietario.
     * Restituisce l'ID del film associato per consentire il corretto redirect.
     */
    @Transactional
    public Long deleteRecensione(Long id, String username) {
        Recensione recensione = getRecensione(id);
        if (recensione == null || !isReviewOwner(recensione, username)) {
            return null;
        }

        Film film = recensione.getFilm();
        Long filmId = (film != null) ? film.getId() : null;

        if (film != null && film.getRecensioni() != null) {
            film.getRecensioni().remove(recensione);
        }
        recensione.setFilm(null);
        this.recensioneRepository.delete(recensione);

        return filmId;
    }
}