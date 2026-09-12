package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Regista;

public interface RegistaRepository extends CrudRepository<Regista, Long>{
	
	boolean existsByNomeAndCognomeAndDataNascita(String nome, String cognome, LocalDate dataNascita);
	
	boolean existsByNomeAndCognomeAndDataNascitaAndIdNot(String nome, String cognome, LocalDate dataNascita, Long id);

}
