package it.uniroma3.siw.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.model.Sala;
import it.uniroma3.siw.repository.ProiezioneRepository;
import it.uniroma3.siw.repository.SalaRepository;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
	private ProiezioneRepository proiezioneRepository;

    public SalaService(SalaRepository salaRepository, ProiezioneRepository proiezioneRepository) {
        this.salaRepository = salaRepository;
        this.proiezioneRepository=proiezioneRepository;
    }

    @Transactional(readOnly = true)
    public List<Sala> findAll() {
        return (List<Sala>) this.salaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Sala getSala(Long id) {
        Optional<Sala> sala = this.salaRepository.findById(id);
        return sala.orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean alreadyExists(Sala sala) {
        if (sala.getNome() == null || sala.getIndirizzo() == null) {
            return false;
        }
        if (sala.getId() != null) {
            return this.salaRepository.existsByNomeAndIndirizzoAndIdNot(
                sala.getNome().trim(), sala.getIndirizzo().trim(), sala.getId()
            );
        }
        return this.salaRepository.existsByNomeAndIndirizzo(
            sala.getNome().trim(), sala.getIndirizzo().trim()
        );
    }

    @Transactional
    public Sala saveSala(Sala sala) {
        return this.salaRepository.save(sala);
    }

    @Transactional
    public Sala updateSala(Sala existingSala, Sala salaForm) {
        existingSala.setNome(salaForm.getNome());
        existingSala.setIndirizzo(salaForm.getIndirizzo());
        existingSala.setCapienza(salaForm.getCapienza());
        return this.salaRepository.save(existingSala);
    }

	public Sala findById(Long id) {
		return salaRepository.findById(id).orElse(null);
	}
	
	
}