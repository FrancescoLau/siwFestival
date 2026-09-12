package it.uniroma3.siw.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.exception.DuplicateFilmException;
import it.uniroma3.siw.exception.FilmAnnoPosterioreFestivalException;
import it.uniroma3.siw.exception.FilmFutureException;
import it.uniroma3.siw.exception.RegistaDateIncompatibiliException;
import it.uniroma3.siw.exception.FilmRegistaNonCompatibileException;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.FestivalService;
import it.uniroma3.siw.service.FilmService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.RegistaService;
import it.uniroma3.siw.validation.FilmValidator;
import jakarta.validation.Valid;

@Controller
public class FilmController {

    private FilmService filmService;
    private FestivalService festivalService;
    private FilmValidator filmValidator;
	private RegistaService registaService;
	private RecensioneService recensioneService;
	private CredentialsService credentialsService;

    public FilmController(FilmService filmService, 
                          FestivalService festivalService, 
                          FilmValidator filmValidator, RegistaService registaService,
                          RecensioneService recensioneService, CredentialsService credentialsService) {
        this.filmService = filmService;
        this.festivalService = festivalService;
        this.filmValidator = filmValidator;
        this.registaService=registaService;
        this.recensioneService = recensioneService;
        this.credentialsService=credentialsService;
    }

    @GetMapping("/film")
    public String list(Model model) {
        model.addAttribute("films", this.filmService.findAll());
        return "film";
    }
    
    @GetMapping("/film/{id}")
    public String infoFilm(@PathVariable("id") Long id,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Film film = this.filmService.findById(id);
        if (film == null) {
            return "redirect:/film";
        }
        model.addAttribute("film", film);

        // Se l'utente è loggato, cerchiamo se ha già lasciato una recensione
        if (userDetails != null) {
        	
            Credentials credentials = this.credentialsService.getCredentials(userDetails.getUsername());
            if (credentials != null && credentials.getUser() != null) {
                this.recensioneService.findByUtenteAndFilm(credentials.getUser(), film)
                        .ifPresent(recensione -> model.addAttribute("miaRecensione", recensione));
            }
        }

        return "infoFilm";
    }

    @GetMapping("/admin/formNewFilm")
    public String showFilmForm(Model model) {
        model.addAttribute("film", new Film());
        model.addAttribute("festivals", this.festivalService.findAll());
        model.addAttribute("registi", this.registaService.findAll());
        return "admin/formNewFilm";
    }

    @PostMapping("/admin/film")
    public String newFilm(@Valid @ModelAttribute("film") Film film,
                          BindingResult bindingResult,
                          Model model) {
        this.filmValidator.validate(film, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("registi", this.registaService.findAll());
            model.addAttribute("festivals", this.festivalService.findAll());
            return "admin/formNewFilm";
        }

        try {
            this.filmService.save(film);
            return "redirect:/film";
        } catch (DuplicateFilmException e) {
            bindingResult.reject("film.duplicate");
        } catch (RegistaDateIncompatibiliException e) {
            bindingResult.reject("film.regista.incompatibile");
        } catch (FilmAnnoPosterioreFestivalException e) {
            bindingResult.reject("film.festival.annoIncompatibile");
        }

        model.addAttribute("registi", this.registaService.findAll());
        model.addAttribute("festivals", this.festivalService.findAll());
        return "admin/formNewFilm";
    }

    @PostMapping("/admin/film/edit/{id}")
    public String updateFilm(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("film") Film filmAggiornato,
                             BindingResult bindingResult,
                             Model model) {
        Film filmCorrente = this.filmService.findById(id);
        if (filmCorrente == null) {
            return "redirect:/film";
        }

        filmAggiornato.setId(id);
        this.filmValidator.validate(filmAggiornato, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("registi", this.registaService.findAll());
            model.addAttribute("festivals", this.festivalService.findAll());
            return "admin/formEditFilm";
        }

        try {
            this.filmService.updateFilm(filmCorrente, filmAggiornato);
            return "redirect:/film/" + id;
        } catch (DuplicateFilmException e) {
            bindingResult.reject("film.duplicate");
        } catch (RegistaDateIncompatibiliException e) {
            bindingResult.reject("film.regista.incompatibile");
        } catch (FilmAnnoPosterioreFestivalException e) {
            bindingResult.reject("film.festival.annoIncompatibile");
        }

        model.addAttribute("registi", this.registaService.findAll());
        model.addAttribute("festivals", this.festivalService.findAll());
        return "admin/formEditFilm";
    }

    @GetMapping("/admin/film/edit/{id}")
    public String showEditFilmForm(@PathVariable("id") Long id, Model model) {
        // 1. Ordina al service di trovare il film da modificare
        Film film = this.filmService.findById(id);
        if (film == null) {
            return "redirect:/film";
        }
        model.addAttribute("film", film);
        model.addAttribute("festivals", this.festivalService.findAll());
        model.addAttribute("registi", this.registaService.findAll());
        return "admin/formEditFilm";
    }

    

    @PostMapping("/admin/film/delete/{id}")
    public String deleteFilm(@PathVariable("id") Long id) {
        this.filmService.deleteFilm(id);
        return "redirect:/film";
    }
}