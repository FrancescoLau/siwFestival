package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import it.uniroma3.siw.model.Sala;
import it.uniroma3.siw.service.SalaService;
import it.uniroma3.siw.validation.SalaValidator;
import jakarta.validation.Valid;

@Controller
public class SalaController {

    private final SalaService salaService;
    private final SalaValidator salaValidator;

    public SalaController(SalaService salaService, SalaValidator salaValidator) {
        this.salaService = salaService;
        this.salaValidator = salaValidator;
    }

    @GetMapping("/sale")
    public String listSale(Model model) {
    	List<Sala>sale=this.salaService.findAll();
        model.addAttribute("sale", this.salaService.findAll());
        
        return "sale";
    }

    @GetMapping("/admin/formNewSala")
    public String showFormNewSala(Model model) {
        model.addAttribute("sala", new Sala());
        return "admin/formNewSala";
    }

    @PostMapping("/admin/sala")
    public String newSala(@Valid @ModelAttribute("sala") Sala sala,
                          BindingResult bindingResult,
                          Model model) {
        this.salaValidator.validate(sala, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin/formNewSala";
        }
        this.salaService.saveSala(sala);
        return "redirect:/sale";
    }

    @GetMapping("/admin/sala/edit/{id}")
    public String showEditSalaForm(@PathVariable("id") Long id, Model model) {
        Sala sala = this.salaService.getSala(id);
        if (sala == null) {
            return "redirect:/sale";
        }
        model.addAttribute("sala", sala);
        return "admin/formEditSala";
    }

    @PostMapping("/admin/sala/edit/{id}")
    public String updateSala(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("sala") Sala salaForm,
                             BindingResult bindingResult,
                             Model model) {
        Sala existingSala = this.salaService.getSala(id);
        if (existingSala == null) {
            return "redirect:/sale";
        }
        salaForm.setId(id);
        this.salaValidator.validate(salaForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/formEditSala";
        }
        this.salaService.updateSala(existingSala, salaForm);
        return "redirect:/sale";
    }
}