package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Sala;
@Repository
public interface SalaRepository extends CrudRepository<Sala, Long> {
    boolean existsByNomeAndIndirizzo(String nome, String indirizzo);
    boolean existsByNomeAndIndirizzoAndIdNot(String nome, String indirizzo, Long id);
    
    
    
    
}