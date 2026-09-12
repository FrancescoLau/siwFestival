package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.exception.FestivalDataFineBeforeDataInizioException;
import it.uniroma3.siw.exception.FilmAnnoPosterioreFestivalException;
import it.uniroma3.siw.exception.DuplicateFestivalException;
import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.service.FestivalService;
import it.uniroma3.siw.service.ProiezioneService;
import it.uniroma3.siw.validation.FestivalValidator;
import jakarta.validation.Valid;

@Controller
public class FestivalController {

    private FestivalService festivalService;
    private FestivalValidator festivalValidator;
    private ProiezioneService proiezioneService;

    public FestivalController(FestivalService festivalService, FestivalValidator festivalValidator, ProiezioneService proiezioneService) {
        this.festivalService = festivalService;
        this.festivalValidator = festivalValidator;
        this.proiezioneService=proiezioneService;
    }

    @GetMapping("/festival")
    public String list(Model model) {
        model.addAttribute("festivals", this.festivalService.findAll());
        return "festival";
    }

    @GetMapping("/festival/{id}")
    public String infoFestival(@PathVariable("id") Long id, Model model) {
        Festival festival = this.festivalService.getFestival(id);
        if (festival == null) {
            return "redirect:/festival";
        }
        model.addAttribute("festival", festival);
        return "infoFestival";
    }

    @GetMapping("/admin/formNewFestival")
    public String showFestivalForm(Model model) {
        model.addAttribute("festival", new Festival());
        model.addAttribute("films", this.festivalService.getAllFilms());
        return "admin/formNewFestival";
    }

    @PostMapping("/admin/festival")
    public String newFestival(@Valid @ModelAttribute("festival") Festival festival,
                              BindingResult bindingResult,
                              Model model) {
        this.festivalValidator.validate(festival, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("films", this.festivalService.getAllFilms());
            return "admin/formNewFestival";
        }

        try {
            this.festivalService.save(festival);
            return "redirect:/festival";
        } catch (DuplicateFestivalException e) {
            bindingResult.reject("festival.duplicate");
            model.addAttribute("films", this.festivalService.getAllFilms());
            return "admin/formNewFestival";
        } catch (FilmAnnoPosterioreFestivalException e) {
            bindingResult.reject("festival.film.annoPosteriore");
            model.addAttribute("films", this.festivalService.getAllFilms());
            return "admin/formNewFestival";
        }
    }

    @GetMapping("/admin/festival/edit/{id}")
    public String showEditFestivalForm(@PathVariable("id") Long id, Model model) {
        Festival festival = this.festivalService.getFestival(id);
        if (festival == null) {
            return "redirect:/festival";
        }
        model.addAttribute("festival", festival);
        model.addAttribute("films", this.festivalService.getAllFilms());
        return "admin/formEditFestival";
    }

    @PostMapping("/admin/festival/edit/{id}")
    public String updateFestival(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("festival") Festival festivalForm,
                                 BindingResult bindingResult,
                                 Model model) {
        Festival existingFestival = this.festivalService.getFestival(id);
        if (existingFestival == null) {
            return "redirect:/festival";
        }

        festivalForm.setId(id);
        this.festivalValidator.validate(festivalForm, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("films", this.festivalService.getAllFilms());
            return "admin/formEditFestival";
        }

        try {
            this.festivalService.updateFestival(existingFestival, festivalForm);
            return "redirect:/festival/" + id;
        } catch (DuplicateFestivalException e) {
            bindingResult.reject("festival.duplicate");
            model.addAttribute("films", this.festivalService.getAllFilms());
            return "admin/formEditFestival";
        } catch (FilmAnnoPosterioreFestivalException e) {
            bindingResult.reject("festival.film.annoPosteriore");
            model.addAttribute("films", this.festivalService.getAllFilms());
            return "admin/formEditFestival";
        }
    }

    @PostMapping("/admin/festival/delete/{id}")
    public String deleteFestival(@PathVariable("id") Long id) {
        this.festivalService.deleteFestival(id);
        return "redirect:/festival";
    }
}