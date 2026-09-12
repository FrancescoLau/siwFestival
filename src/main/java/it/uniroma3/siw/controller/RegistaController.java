package it.uniroma3.siw.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.exception.RegistaDateIncompatibiliException;
import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.repository.ProiezioneRepository;
import it.uniroma3.siw.repository.RegistaRepository;
import it.uniroma3.siw.service.RegistaService;
import it.uniroma3.siw.validation.RegistaValidator;
import jakarta.validation.Valid;

@Controller
public class RegistaController {
	private RegistaService registaService;
	private RegistaValidator registaValidator;
	public RegistaController(RegistaService registaService, RegistaValidator registaValidator) {
		this.registaService=registaService;
		this.registaValidator=registaValidator;
	}
	
	
	@GetMapping("/registi")
	public String list(Model model) {
		model.addAttribute("registi", this.registaService.findAll());
		return "registi";
	}
	
	@GetMapping("/regista/{id}")
    public String infoRegista(@PathVariable("id") Long id, Model model) {
        
        Regista regista = this.registaService.findById(id).orElse(null);
        if (regista == null) {
            return "redirect:/film";
        }
        model.addAttribute("regista", regista);
        return "infoRegista";
    }
	
	@GetMapping("/admin/nuovoRegista")
    public String showFormNewRegista(Model model) {
        model.addAttribute("regista", new Regista());
        return "admin/formNewRegista";
    }
	
	@PostMapping("/admin/nuovoRegista")
    public String newRegista(@Valid @ModelAttribute("regista") Regista regista,
                             BindingResult bindingResult,
                             Model model) {
        this.registaValidator.validate(regista, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/formNewRegista";
        }

        this.registaService.save(regista);
        return "redirect:/registi";
    }
	
	
	@GetMapping("/admin/regista/edit/{id}")
    public String showFormEditRegista(@PathVariable("id") Long id, Model model) {
        Regista regista = this.registaService.findById(id).orElse(null);
        if (regista == null) {
            return "redirect:/registi";
        }
        model.addAttribute("regista", regista);
        return "admin/formEditRegista";
    }

	@PostMapping("/admin/regista/edit/{id}")
	public String updateRegista(@PathVariable("id") Long id,
	                            @Valid @ModelAttribute("regista") Regista registaForm,
	                            BindingResult bindingResult,
	                            Model model) {
	    Regista existingRegista = this.registaService.findById(id).orElse(null);
	    if (existingRegista == null) {
	        return "redirect:/registi";
	    }

	    if (bindingResult.hasErrors()) {
	        return "admin/formEditRegista";
	    }

	    try {
	        this.registaService.updateRegista(existingRegista, registaForm);
	        return "redirect:/regista/" + id;
	    } catch (RegistaDateIncompatibiliException e) {
	        bindingResult.reject("regista.date.incompatibili");
	        return "admin/formEditRegista";
	    }
	}
}
