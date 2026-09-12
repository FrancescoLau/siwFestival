package it.uniroma3.siw.service;

import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class UserService {
private UserRepository userRepository;

public UserService(UserRepository userRepository) {
	this.userRepository=userRepository;
}

public User getUser(Long id) {
    // Usa findById del repository e restituisci null se non trovato
    return userRepository.findById(id).orElse(null); 
}

public User saveUser(User user) {
    return userRepository.save(user); 
}

public User findByEmail(String email) {
	return userRepository.findByEmail(email);
}


}
