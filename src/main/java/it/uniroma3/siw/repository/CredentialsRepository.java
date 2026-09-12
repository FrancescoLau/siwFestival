package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Credentials;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long>{
	
	@Query("SELECT c FROM Credentials c LEFT JOIN FETCH c.user WHERE c.username = :username")
    Optional<Credentials> findByUsername(@Param("username") String username);
	
	public boolean existsByUsername(String username);
	
	public Integer countByRole(String role);
	
}
