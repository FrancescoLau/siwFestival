package it.uniroma3.siw.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;

public interface RecensioneRepository extends CrudRepository<Recensione, Long> {

boolean existsByUtenteIdAndFilmId(Long utenteId, Long filmId);
    
    Optional<Recensione> findByUtenteIdAndFilmId(Long utenteId, Long filmId);

    void deleteByFilm(Film film);

	boolean existsByUtenteAndFilm(User utente, Film film);
	
	public Recensione save(Recensione recensione);
	
	
}