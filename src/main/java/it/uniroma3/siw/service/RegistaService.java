package it.uniroma3.siw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.exception.RegistaDateIncompatibiliException;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.repository.RegistaRepository;

@Service
public class RegistaService {

    private final RegistaRepository registaRepository;

    public RegistaService(RegistaRepository registaRepository) {
        this.registaRepository = registaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Regista> findById(Long id) {
        return this.registaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean alreadyExists(Regista regista) {
        if (regista.getNome() == null || regista.getCognome() == null || regista.getDataNascita() == null) {
            return false;
        }
        return this.registaRepository.existsByNomeAndCognomeAndDataNascita(
            regista.getNome().trim(),
            regista.getCognome().trim(),
            regista.getDataNascita()
        );
    }

    @Transactional
    public Regista save(Regista regista) {
        return this.registaRepository.save(regista);
    }

	public Iterable<Regista> findAll() {
		return this.registaRepository.findAll();
	}
	
	
	@Transactional
    public Regista updateRegista(Regista existingRegista, Regista registaForm) 
            throws RegistaDateIncompatibiliException {

        LocalDate nuovaNascita = registaForm.getDataNascita();
        LocalDate nuovaMorte = registaForm.getDataMorte();

        if (existingRegista.getFilmDiretti() != null) {
            for (Film film : existingRegista.getFilmDiretti()) {
                if (film.getAnno() != null) {
                    if (nuovaNascita != null && film.getAnno() < nuovaNascita.getYear()) {
                        throw new RegistaDateIncompatibiliException();
                    }
                    if (nuovaMorte != null && film.getAnno() > nuovaMorte.getYear()) {
                        throw new RegistaDateIncompatibiliException();
                    }
                }
            }
        }

        existingRegista.setNome(registaForm.getNome());
        existingRegista.setCognome(registaForm.getCognome());
        existingRegista.setNazionalita(registaForm.getNazionalita());
        existingRegista.setDataNascita(nuovaNascita);
        existingRegista.setDataMorte(nuovaMorte);

        return this.registaRepository.save(existingRegista);
    }
}