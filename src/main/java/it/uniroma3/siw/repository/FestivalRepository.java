package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Festival;
@Repository
public interface FestivalRepository extends CrudRepository<Festival, Long>{

    boolean existsByNomeAndAnnoAndCitta(String nome, Integer anno, String citta);

    boolean existsByNomeAndAnnoAndCittaAndIdNot(String nome, Integer anno, String citta, Long id);

	boolean existsByNomeAndAnno(String trim, Integer anno);

	boolean existsByNomeAndAnnoAndIdNot(String trim, Integer anno, Long id);
	
	// 2. Per la strategia JOIN FETCH:
    // Fa una query JPQL esplicita caricando festival, film e registi con una singola SELECT
    @Query("SELECT DISTINCT f FROM Festival f " +
           "LEFT JOIN FETCH f.film m " +
           "LEFT JOIN FETCH m.regista " +
           "WHERE f.id = :id")
    Optional<Festival> findByIdWithFilmsAndDirectorsFetch(@Param("id") Long id);


    // 3. Per la strategia ENTITY GRAPH:
    // 'attributePaths' dice a JPA di caricare subito (in EAGER) 'film' e 'film.regista'
    @EntityGraph(attributePaths = {"film", "film.regista"})
    @Query("SELECT f FROM Festival f WHERE f.id = :id")
    Optional<Festival> findByIdWithFilmsAndDirectorsGraph(@Param("id") Long id);

	 
}