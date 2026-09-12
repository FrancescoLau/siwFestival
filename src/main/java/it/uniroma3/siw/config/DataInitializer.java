package it.uniroma3.siw.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Proiezione;
import it.uniroma3.siw.model.Proiezione.StatoProiezione;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Regista;
import it.uniroma3.siw.model.Sala;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.FestivalService;
import it.uniroma3.siw.service.FilmService;
import it.uniroma3.siw.service.ProiezioneService;
import it.uniroma3.siw.service.RecensioneService;
import it.uniroma3.siw.service.RegistaService;
import it.uniroma3.siw.service.SalaService;
import it.uniroma3.siw.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {

    private  FilmService filmService;
    private FestivalService festivalService;
    private RegistaService registaService;
    private SalaService salaService;
    private ProiezioneService proiezioneService;
	private RecensioneService recensioneService;
	private UserService userService;

    public DataInitializer(FilmService filmService, FestivalService festivalService, RegistaService registaService, SalaService salaService,
    		ProiezioneService proiezioneService, RecensioneService recensioneService, UserService userService) {
        this.filmService = filmService;
        this.festivalService = festivalService;
        this.registaService=registaService;
        this.salaService=salaService;
        this.proiezioneService=proiezioneService;
        this.recensioneService=recensioneService;
        this.userService=userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Inserimento Film (non associato ad alcun festival
    	Regista nolan = new Regista();
        nolan.setNome("Christopher");
        nolan.setCognome("Nolan");
        nolan.setDataNascita(LocalDate.of(1970, 7, 30)); 
        nolan.setNazionalita("Statunitense");
        nolan.setFilmDiretti(new ArrayList<>());
        
        Regista scorsese = new Regista();
        scorsese.setNome("Martin");
        scorsese.setCognome("Scorsese");
        scorsese.setDataNascita(LocalDate.of(1942, 11, 17)); 
        scorsese.setNazionalita("Statunitense");
        scorsese.setFilmDiretti(new ArrayList<>());
        
        Regista spielberg = new Regista();
        spielberg.setNome("Steven");
        spielberg.setCognome("Spielberg");
        spielberg.setDataNascita(LocalDate.of(1946, 12, 18)); 
        spielberg.setNazionalita("Statunitense");
        spielberg.setFilmDiretti(new ArrayList<>());
    	      
        this.registaService.save(nolan);
        this.registaService.save(scorsese);
        this.registaService.save(spielberg);
        
    	// 2. Inizializzazione Festival
        Festival festivalVenezia = new Festival();
        festivalVenezia.setNome("Mostra Internazionale d'Arte Cinematografica");
        festivalVenezia.setAnno(2024);
        festivalVenezia.setCitta("Venezia");
        festivalVenezia.setDataInizio(LocalDate.of(2024, 9, 2));
        festivalVenezia.setDataFine(LocalDate.of(2024, 9, 9));
        festivalVenezia.setDescrizione("Uno dei festival cinematografici più prestigiosi al mondo.");
        festivalVenezia.setFilm(new ArrayList<>());
        this.festivalService.save(festivalVenezia);

        Festival festivalCannes = new Festival();
        festivalCannes.setNome("Festival di Cannes");
        festivalCannes.setAnno(2025);
        festivalCannes.setCitta("Cannes");
        festivalCannes.setDataInizio(LocalDate.of(2025, 9, 2));
        festivalCannes.setDataFine(LocalDate.of(2025, 9, 13));
        festivalCannes.setDescrizione("Celebre festival cinematografico sulla Costa Azzurra.");
        festivalCannes.setFilm(new ArrayList<>());
        this.festivalService.save(festivalCannes);

        Festival festivalRoma = new Festival();
        festivalRoma.setNome("Festa del Cinema di Roma");
        festivalRoma.setAnno(2025);
        festivalRoma.setCitta("Roma");
        festivalRoma.setDataInizio(LocalDate.of(2025, 9, 2));
        festivalRoma.setDataFine(LocalDate.of(2025, 10, 2));
        festivalRoma.setDescrizione("Rassegna cinematografica autunnale all'Auditorium Parco della Musica.");
        festivalRoma.setFilm(new ArrayList<>());
        this.festivalService.save(festivalRoma);             
        
        Sala sala1 = new Sala();
        sala1.setNome("Sala Uno");
        sala1.setIndirizzo("Via Roma 1");
        sala1.setCapienza(200);
        
        Sala sala2 = new Sala();
        sala2.setNome("Sala Due");
        sala2.setIndirizzo("Via La Spezia 2");
        sala2.setCapienza(150);
        
        this.salaService.saveSala(sala1);
        this.salaService.saveSala(sala2);
        
        Film filmInception = new Film();
        filmInception.setTitolo("Inception");
        filmInception.setAnno(2010);
        filmInception.setGenere("Fantascienza");
        filmInception.setDurata(148);
        filmInception.setPaeseProduzione("USA");
        filmInception.setFestivals(new ArrayList<>());
        filmInception.setRegista(nolan);
        filmInception.getFestivals().add(festivalVenezia);
        if (!this.filmService.alreadyExists(filmInception)) {
            this.filmService.save(filmInception);
        }

        Film filmInterstellar = new Film();
        filmInterstellar.setTitolo("Interstellar");
        filmInterstellar.setAnno(2014);
        filmInterstellar.setGenere("Fantascienza");
        filmInterstellar.setDurata(169);
        filmInterstellar.setPaeseProduzione("USA");
        filmInterstellar.setFestivals(new ArrayList<>());
        filmInterstellar.setRegista(nolan);
        filmInterstellar.getFestivals().add(festivalCannes);
        if (!this.filmService.alreadyExists(filmInterstellar)) {
            this.filmService.save(filmInterstellar);
        }

        Film filmGoodfellas = new Film();
        filmGoodfellas.setTitolo("Quei bravi ragazzi");
        filmGoodfellas.setAnno(1990);
        filmGoodfellas.setGenere("Drammatico");
        filmGoodfellas.setDurata(145);
        filmGoodfellas.setPaeseProduzione("USA");
        filmGoodfellas.setFestivals(new ArrayList<>());
        filmGoodfellas.setRegista(scorsese);
        filmGoodfellas.getFestivals().add(festivalVenezia);
        if (!this.filmService.alreadyExists(filmGoodfellas)) {
            this.filmService.save(filmGoodfellas);
        }

        Film filmTaxiDriver = new Film();
        filmTaxiDriver.setTitolo("Taxi Driver");
        filmTaxiDriver.setAnno(1976);
        filmTaxiDriver.setGenere("Drammatico");
        filmTaxiDriver.setDurata(114);
        filmTaxiDriver.setPaeseProduzione("USA");
        filmTaxiDriver.setFestivals(new ArrayList<>());
        filmTaxiDriver.setRegista(scorsese);
        filmTaxiDriver.getFestivals().add(festivalCannes);
        if (!this.filmService.alreadyExists(filmTaxiDriver)) {
            this.filmService.save(filmTaxiDriver);
        }

        Film filmJurassicPark = new Film();
        filmJurassicPark.setTitolo("Jurassic Park");
        filmJurassicPark.setAnno(1993);
        filmJurassicPark.setGenere("Avventura");
        filmJurassicPark.setDurata(127);
        filmJurassicPark.setPaeseProduzione("USA");
        filmJurassicPark.setFestivals(new ArrayList<>());
        filmJurassicPark.setRegista(spielberg);
        filmJurassicPark.getFestivals().add(festivalRoma);
        if (!this.filmService.alreadyExists(filmJurassicPark)) {
            this.filmService.save(filmJurassicPark);
        }

        // ==========================================
        // 5. Inizializzazione Proiezioni (1 Esistente + 3 Nuove)
        // ==========================================
        Proiezione proiezione1 = new Proiezione();
        proiezione1.setFestival(festivalVenezia);
        proiezione1.setFilm(filmInception);
        proiezione1.setSala(sala1);
        proiezione1.setStato(StatoProiezione.COMPLETED);
        proiezione1.setData(LocalDate.of(2024, 9, 1));
        proiezione1.setOra(LocalTime.of(20, 30));
        this.proiezioneService.save(proiezione1);

        Proiezione proiezione2 = new Proiezione();
        proiezione2.setFestival(festivalVenezia);
        proiezione2.setFilm(filmGoodfellas);
        proiezione2.setSala(sala2);
        proiezione2.setStato(StatoProiezione.COMPLETED);
        proiezione2.setData(LocalDate.of(2024, 9, 3));
        proiezione2.setOra(LocalTime.of(18, 00));
        this.proiezioneService.save(proiezione2);

        Proiezione proiezione3 = new Proiezione();
        proiezione3.setFestival(festivalCannes);
        proiezione3.setFilm(filmInterstellar);
        proiezione3.setSala(sala1);
        proiezione3.setStato(StatoProiezione.COMPLETED);
        proiezione3.setData(LocalDate.of(2025, 5, 16));
        proiezione3.setOra(LocalTime.of(21, 00));
        this.proiezioneService.save(proiezione3);

        Proiezione proiezione4 = new Proiezione();
        proiezione4.setFestival(festivalRoma);
        proiezione4.setFilm(filmJurassicPark);
        proiezione4.setSala(sala2);
        proiezione4.setStato(StatoProiezione.COMPLETED);
        proiezione4.setData(LocalDate.of(2025, 10, 18));
        proiezione4.setOra(LocalTime.of(17, 30));
        this.proiezioneService.save(proiezione4);
        
        Recensione recensione=new Recensione();
        recensione.setFilm(filmJurassicPark);
        User user=this.userService.findByEmail("user@festival.com");
        recensione.setUtente(user);
        recensione.setVoto(5);
        recensione.setTesto("Bellissimo");
        this.recensioneService.save(recensione, user, filmJurassicPark);
    }
           
}