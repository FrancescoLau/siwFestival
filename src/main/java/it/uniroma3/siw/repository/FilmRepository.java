package it.uniroma3.siw.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.uniroma3.siw.model.Film;

@Repository
public interface FilmRepository extends CrudRepository<Film, Long>{
	
	boolean existsByTitoloAndAnno(String titolo, Integer anno);

	boolean existsByTitoloAndAnnoAndIdNot(String titolo, Integer anno, Long id);
	
	@Query("SELECT DISTINCT f FROM Film f " +
	           "LEFT JOIN FETCH f.regista " +
	           "LEFT JOIN FETCH f.proiezioni p " +
	           "LEFT JOIN FETCH p.sala " +
	           "LEFT JOIN FETCH p.festival " +
	           "WHERE f.id = :id")
	    Optional<Film> findByIdWithProiezioni(@Param("id") Long id);

	    
	    @Query("SELECT DISTINCT f FROM Film f " +
	           "LEFT JOIN FETCH f.festivals " +
	           "WHERE f.id = :id")
	    Optional<Film> findByIdWithFestivals(@Param("id") Long id);

	     
	    @Query("SELECT DISTINCT f FROM Film f " +
	           "LEFT JOIN FETCH f.recensioni " +
	           "WHERE f.id = :id")
	    Optional<Film> findByIdWithRecensioni(@Param("id") Long id);
}
