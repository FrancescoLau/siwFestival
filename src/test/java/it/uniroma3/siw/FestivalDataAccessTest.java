package it.uniroma3.siw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Festival;
import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.repository.FestivalRepository;
import jakarta.persistence.EntityManager;

@SpringBootTest
public class FestivalDataAccessTest {

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    public void testStrategieAccessoDati() {
        Long festivalId = 1L; // Usa l'id di un festival con dei film associati

        // ==========================================
        // 1. STRATEGIA: LAZY
        // ==========================================
        entityManager.clear(); // serve solo a svuotare la memoria per partire da zero
        System.out.println("\n--- INIZIO TEST: LAZY (osserva le query sotto) ---");
        
        long tInizioLazy = System.currentTimeMillis();
        
        Festival fLazy = festivalRepository.findById(festivalId).orElse(null);
        int numFilm = 0;
        if (fLazy != null) {
            for (Film film : fLazy.getFilm()) {
                // Leggere il cognome forza Hibernate a caricare il regista
                String cognome = film.getRegista().getCognome();
                numFilm++;
            }
        }
        
        long tempoLazy = System.currentTimeMillis() - tInizioLazy;
        System.out.println("--- FINE TEST: LAZY ---");


        // ==========================================
        // 2. STRATEGIA: JOIN FETCH
        // ==========================================
        entityManager.clear();
        System.out.println("\n--- INIZIO TEST: JOIN FETCH (osserva le query sotto) ---");
        
        long tInizioFetch = System.currentTimeMillis();
        
        Festival fFetch = festivalRepository.findByIdWithFilmsAndDirectorsFetch(festivalId).orElse(null);
        if (fFetch != null) {
            for (Film film : fFetch.getFilm()) {
                String cognome = film.getRegista().getCognome();
            }
        }
        
        long tempoFetch = System.currentTimeMillis() - tInizioFetch;
        System.out.println("--- FINE TEST: JOIN FETCH ---");


        // ==========================================
        // 3. STRATEGIA: ENTITY GRAPH
        // ==========================================
        entityManager.clear();
        System.out.println("\n--- INIZIO TEST: ENTITY GRAPH (osserva le query sotto) ---");
        
        long tInizioGraph = System.currentTimeMillis();
        
        Festival fGraph = festivalRepository.findByIdWithFilmsAndDirectorsGraph(festivalId).orElse(null);
        if (fGraph != null) {
            for (Film film : fGraph.getFilm()) {
                String cognome = film.getRegista().getCognome();
            }
        }
        
        long tempoGraph = System.currentTimeMillis() - tInizioGraph;
        System.out.println("--- FINE TEST: ENTITY GRAPH ---");


        // ==========================================
        // RIEPILOGO FINALE STAMPATO A VIDEO
        // ==========================================
        System.out.println("\n================ RIEPILOGO ==================");
        System.out.println("Numero film caricati: " + numFilm);
        System.out.println("Tempo LAZY:         " + tempoLazy + " ms  (ha generato 1 + N query)");
        System.out.println("Tempo JOIN FETCH:   " + tempoFetch + " ms (ha generato 1 sola query)");
        System.out.println("Tempo ENTITY GRAPH: " + tempoGraph + " ms (ha generato 1 sola query)");
        System.out.println("=============================================\n");
    }
}