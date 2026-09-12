package it.uniroma3.siw.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.service.FestivalService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class FestivalRestController {

    private final FestivalService festivalService;

    public FestivalRestController(FestivalService festivalService) {
        this.festivalService = festivalService;
    }

    /**
     * Recupera la lista di tutti i festival
     * GET /api/festivals
     */
    @GetMapping("/api/festivals")
    public ResponseEntity<Iterable<Festival>> getAllFestivals() {
        Iterable<Festival> festivals = this.festivalService.findAll();
        return new ResponseEntity<>(festivals, HttpStatus.OK);
    }

    /**
     * Recupera il dettaglio di un singolo festival dato il suo ID
     * GET /api/festivals/{id}
     */
    @GetMapping("/api/festivals/{id}")
    public ResponseEntity<Festival> getFestivalById(@PathVariable("id") Long id) {
        Festival festival = this.festivalService.getFestival(id);
        if (festival == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(festival, HttpStatus.OK);
    }
}