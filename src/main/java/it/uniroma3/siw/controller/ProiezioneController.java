package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.exception.ProiezioneFilmFuturoException;
import it.uniroma3.siw.exception.SalaNonDisponibileException;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.service.FestivalService;
import it.uniroma3.siw.service.FilmService;
import it.uniroma3.siw.service.ProiezioneService;
import it.uniroma3.siw.service.SalaService;
import it.uniroma3.siw.validation.ProiezioneValidator;
import jakarta.validation.Valid;

@Controller
public class ProiezioneController {

    private final ProiezioneService proiezioneService;
    private final FestivalService festivalService;
    private final FilmService filmService;
    private final SalaService salaService;
    private final ProiezioneValidator proiezioneValidator;

    public ProiezioneController(ProiezioneService proiezioneService,
                                FestivalService festivalService,
                                FilmService filmService,
                                SalaService salaService,
                                ProiezioneValidator proiezioneValidator) {
        this.proiezioneService = proiezioneService;
        this.festivalService = festivalService;
        this.filmService = filmService;
        this.salaService = salaService;
        this.proiezioneValidator = proiezioneValidator;
    }

    @GetMapping("/proiezioni")
    public String listProiezioni(Model model) {
        model.addAttribute("proiezioni", this.proiezioneService.findAll());
        return "proiezioni";
    }

    @GetMapping("/admin/formNewProiezione")
    public String showFormNewProiezione(@RequestParam(value = "festivalId", required = false) Long festivalId,
                                        Model model) {
        Proiezione proiezione = new Proiezione();

        if (festivalId != null) {
            Festival festival = this.festivalService.findById(festivalId);
            if (festival != null) {
                proiezione.setFestival(festival);
                model.addAttribute("films", this.festivalService.findFilmsByFestivalId(festivalId));
            } else {
                model.addAttribute("films", List.of());
            }
        } else {
            model.addAttribute("films", List.of());
        }
        List<Proiezione.StatoProiezione> stati = List.of(
        	    Proiezione.StatoProiezione.COMPLETED, 
        	    Proiezione.StatoProiezione.SCHEDULED
        	);
        model.addAttribute("proiezione", proiezione);
        model.addAttribute("festivals", this.festivalService.findAll());
        model.addAttribute("sale", this.salaService.findAll());
        model.addAttribute("stati", stati);
        return "admin/formNewProiezione";
    }

    @PostMapping("/admin/proiezione")
    public String newProiezione(@Valid @ModelAttribute("proiezione") Proiezione proiezione,
                                BindingResult bindingResult,
                                Model model) {

        if (proiezione.getFilm() != null && proiezione.getFilm().getId() != null) {
            proiezione.setFilm(this.filmService.findById(proiezione.getFilm().getId()));
        }
        if (proiezione.getSala() != null && proiezione.getSala().getId() != null) {
            proiezione.setSala(this.salaService.findById(proiezione.getSala().getId()));
        }
        if (proiezione.getFestival() != null && proiezione.getFestival().getId() != null) {
            proiezione.setFestival(this.festivalService.findById(proiezione.getFestival().getId()));
        }

        this.proiezioneValidator.validate(proiezione, bindingResult);

        if (bindingResult.hasErrors()) {
            Long festivalId = (proiezione.getFestival() != null) ? proiezione.getFestival().getId() : null;
            model.addAttribute("films", this.festivalService.findFilmsByFestivalId(festivalId));
            model.addAttribute("festivals", this.festivalService.findAll());
            model.addAttribute("sale", this.salaService.findAll());
            model.addAttribute("stati", Proiezione.StatoProiezione.values());
            return "admin/formNewProiezione";
        }

        try {
            this.proiezioneService.save(proiezione);
            return "redirect:/proiezioni";
        } catch (SalaNonDisponibileException s) {
            bindingResult.reject("proiezione.salaOccupata");
            Long festivalId = (proiezione.getFestival() != null) ? proiezione.getFestival().getId() : null;
            model.addAttribute("films", this.festivalService.findFilmsByFestivalId(festivalId));
            model.addAttribute("festivals", this.festivalService.findAll());
            model.addAttribute("sale", this.salaService.findAll());
            model.addAttribute("stati", Proiezione.StatoProiezione.values());
            return "admin/formNewProiezione";
        } catch (ProiezioneFilmFuturoException s) {
            bindingResult.reject("proiezione.filmFuturo");
            Long festivalId = (proiezione.getFestival() != null) ? proiezione.getFestival().getId() : null;
            model.addAttribute("films", this.festivalService.findFilmsByFestivalId(festivalId));
            model.addAttribute("festivals", this.festivalService.findAll());
            model.addAttribute("sale", this.salaService.findAll());
            model.addAttribute("stati", Proiezione.StatoProiezione.values());
            return "admin/formNewProiezione"; // <-- Non fare redirect, ritorna la vista formNewProiezione
        }
    }

    @PostMapping("/admin/proiezione/edit/{id}")
    public String updateProiezione(@PathVariable("id") Long id,
                                  @Valid @ModelAttribute("proiezione") Proiezione proiezioneForm,
                                  BindingResult bindingResult,
                                  Model model) {
        Proiezione existing = this.proiezioneService.getProiezione(id);
        if (existing == null) {
            return "redirect:/proiezioni";
        }

        proiezioneForm.setId(id);
        this.proiezioneValidator.validate(proiezioneForm, bindingResult);

        if (bindingResult.hasErrors()) {
            Long festivalId = (proiezioneForm.getFestival() != null) ? proiezioneForm.getFestival().getId() : null;
            model.addAttribute("films", this.festivalService.findFilmsByFestivalId(festivalId));
            model.addAttribute("proiezione", proiezioneForm);
            model.addAttribute("festivals", this.festivalService.findAll());
            model.addAttribute("sale", this.salaService.findAll());
            model.addAttribute("stati", Proiezione.StatoProiezione.values());
            return "admin/formEditProiezione";
        }

        try {
            this.proiezioneService.updateProiezione(existing, proiezioneForm);
            return "redirect:/proiezioni";
        } catch (SalaNonDisponibileException s) {
            bindingResult.reject("proiezione.salaOccupata");
            Long festivalId = (proiezioneForm.getFestival() != null) ? proiezioneForm.getFestival().getId() : null;
            model.addAttribute("films", this.festivalService.findFilmsByFestivalId(festivalId));
            model.addAttribute("proiezione", proiezioneForm);
            model.addAttribute("festivals", this.festivalService.findAll());
            model.addAttribute("sale", this.salaService.findAll());
            model.addAttribute("stati", Proiezione.StatoProiezione.values());
            return "admin/formEditProiezione";
        } catch (ProiezioneFilmFuturoException s) {
            bindingResult.reject("proiezione.filmFuturo");
            Long festivalId = (proiezioneForm.getFestival() != null) ? proiezioneForm.getFestival().getId() : null;
            model.addAttribute("films", this.festivalService.findFilmsByFestivalId(festivalId));
            model.addAttribute("proiezione", proiezioneForm);
            model.addAttribute("festivals", this.festivalService.findAll());
            model.addAttribute("sale", this.salaService.findAll());
            model.addAttribute("stati", Proiezione.StatoProiezione.values());
            return "admin/formEditProiezione"; // <-- Ritorna formEditProiezione, non formNewProiezione
        }
    }
    
    
    @GetMapping("/admin/proiezione/edit/{id}")
    public String showEditProiezioneForm(@PathVariable("id") Long id, Model model) {
        Proiezione proiezione = this.proiezioneService.getProiezione(id);
        if (proiezione == null) {
            return "redirect:/proiezioni";
        }

        Long festivalId = (proiezione.getFestival() != null) ? proiezione.getFestival().getId() : null;
        model.addAttribute("films", this.festivalService.findFilmsByFestivalId(festivalId));
        model.addAttribute("proiezione", proiezione);
        model.addAttribute("festivals", this.festivalService.findAll());
        model.addAttribute("sale", this.salaService.findAll());
        model.addAttribute("stati", Proiezione.StatoProiezione.values());
        return "admin/formEditProiezione";
    }

    

    @PostMapping("/admin/proiezione/delete/{id}")
    public String deleteProiezione(@PathVariable("id") Long id) {
        Proiezione proiezione = this.proiezioneService.getProiezione(id);
        Long filmId = (proiezione != null && proiezione.getFilm() != null) ? proiezione.getFilm().getId() : null;
        
        this.proiezioneService.deleteProiezione(id);
        
        if (filmId == null) {
            return "redirect:/film/" + filmId;
        }
        return "redirect:/proiezioni";
    }

    @GetMapping("/festival/{id}/proiezioni")
    public String proiezioniFestival(@PathVariable("id") Long id, Model model) {
        Festival festivalCorrente = this.festivalService.findById(id);
        if (festivalCorrente == null) {
            return "redirect:/festival";
        }
        model.addAttribute("festival", festivalCorrente);
        model.addAttribute("proiezioni", festivalCorrente.getProiezioni());
        return "proiezioniFestival";
    }
}