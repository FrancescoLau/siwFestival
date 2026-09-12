package it.uniroma3.siw.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.FilmService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.validation.RecensioneValidator;
import jakarta.validation.Valid;

@Controller
public class RecensioneController {

    private final RecensioneService recensioneService;
    private final FilmService filmService;
    private final RecensioneValidator recensioneValidator;

    public RecensioneController(RecensioneService recensioneService,
                                FilmService filmService,
                                RecensioneValidator recensioneValidator) {
        this.recensioneService = recensioneService;
        this.filmService = filmService;
        this.recensioneValidator = recensioneValidator;
    }

    @GetMapping("/film/{id}/nuovaRecensione")
    public String showFormNewRecensione(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Film film = this.filmService.findById(id);
        if (film == null) {
            return "redirect:/film";
        }

        // Verifica business rules: ruolo DEFAULT e recensione non presente
        if (!this.recensioneService.canUserReview(userDetails.getUsername(), film)) {
            return "redirect:/film/" + id;
        }

        model.addAttribute("film", film);
        model.addAttribute("recensione", new Recensione());
        return "formNewRecensione";
    }

    @PostMapping("/film/{id}/nuovaRecensione")
    public String save(@PathVariable("id") Long id, 
                       @Valid @ModelAttribute("recensione") Recensione recensione, 
                       BindingResult bindingResult, 
                       @AuthenticationPrincipal UserDetails userDetails, 
                       Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Film film = this.filmService.findById(id);
        if (film == null) {
            return "redirect:/film";
        }

        if (!this.recensioneService.canUserReview(userDetails.getUsername(), film)) {
            return "redirect:/film/" + id;
        }

        User utente = this.recensioneService.getReviewer(userDetails.getUsername());
        recensione.setFilm(film);
        recensione.setUtente(utente);

        this.recensioneValidator.validate(recensione, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("film", film);
            return "formNewRecensione";
        }

        this.recensioneService.save(recensione, utente, film);
        return "redirect:/film/" + id;
    }

    @GetMapping("/recensione/edit/{id}")
    public String showFormEditRecensione(@PathVariable("id") Long id, 
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Recensione recensione = this.recensioneService.getRecensione(id);
        if (recensione == null) {
            return "redirect:/film";
        }

        // Controllo ownership delegato al service
        if (!this.recensioneService.isReviewOwner(recensione, userDetails.getUsername())) {
            return "redirect:/film/" + (recensione.getFilm() != null ? recensione.getFilm().getId() : "");
        }

        model.addAttribute("recensione", recensione);
        model.addAttribute("film", recensione.getFilm());
        return "formEditRecensione";
    }

    @PostMapping("/recensione/edit/{id}")
    public String updateRecensione(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("recensione") Recensione formRecensione,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Recensione existingRecensione = this.recensioneService.getRecensione(id);
        if (existingRecensione == null) {
            return "redirect:/film";
        }

        if (!this.recensioneService.isReviewOwner(existingRecensione, userDetails.getUsername())) {
            return "redirect:/film/" + (existingRecensione.getFilm() != null ? existingRecensione.getFilm().getId() : "");
        }

        this.recensioneValidator.validate(formRecensione, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("film", existingRecensione.getFilm());
            return "formEditRecensione";
        }

        this.recensioneService.updateRecensione(id, formRecensione, userDetails.getUsername());
        return "redirect:/film/" + existingRecensione.getFilm().getId();
    }

    @PostMapping("/recensione/delete/{id}")
    public String deleteRecensione(@PathVariable("id") Long id,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        Long filmId = this.recensioneService.deleteRecensione(id, userDetails.getUsername());
        
        if (filmId != null) {
            return "redirect:/film/" + filmId;
        } else {
            return "redirect:/film";
        }
    }
    
    @GetMapping("/film/{id}/recensioni")
    public String visualizzaRecensioniFilm(@PathVariable("id") Long id, Model model) {
        Film film = this.filmService.findById(id);
        if (film == null) {
            return "redirect:/film";
        }
        model.addAttribute("film", film);
        model.addAttribute("recensioni", film.getRecensioni());
        return "recensioniFilm";
    }
}