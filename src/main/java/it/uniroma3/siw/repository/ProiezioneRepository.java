package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.model.Sala;
@Repository
public interface ProiezioneRepository extends CrudRepository<Proiezione, Long> {
    List<Proiezione> findByFestival(Festival festival);
    List<Proiezione> findBySalaAndData(Sala sala, LocalDate data);
    boolean existsBySalaAndDataAndOra(Sala sala, LocalDate data, LocalTime ora);
    boolean existsBySalaAndDataAndOraAndIdNot(Sala sala, LocalDate data, LocalTime ora, Long id);
	void deleteByFilm(Film film);
	List<Proiezione> findByFilm(Film film);    
	List<Proiezione> findBySala(Sala sala);    
 boolean existsByDataAndOraAndSala(LocalDate data, LocalTime ora, Sala sala);  
	 void deleteByFestival(Festival festival);
	 
	 @Query("SELECT p FROM Proiezione p JOIN FETCH p.film WHERE p.sala = :sala AND p.data = :data AND p.stato <> :stato")
	 List<Proiezione> findBySalaAndDataAndStatoNot(@Param("sala") Sala sala, 
	                                              @Param("data") LocalDate data, 
	                                              @Param("stato") Proiezione.StatoProiezione stato);
	 
	 @Query("SELECT p FROM Proiezione p JOIN FETCH p.film WHERE p.sala = :sala AND p.data BETWEEN :inizio AND :fine AND p.stato <> :stato")
	 List<Proiezione> findBySalaAndDataBetweenAndStatoNot(@Param("sala") Sala sala,
	                                                     @Param("inizio") LocalDate inizio,
	                                                     @Param("fine") LocalDate fine,
	                                                     @Param("stato") Proiezione.StatoProiezione stato);
	
	 @Query("SELECT COUNT(p) FROM Proiezione p WHERE p.sala = :sala AND p.stato = :stato")
	 long countBySalaAndStato(@Param("sala") Sala sala, @Param("stato") Proiezione.StatoProiezione stato);
	 
	
	 List<Proiezione> findByFestivalAndFilm(Festival festival, Film film);
}