package it.uniroma3.siw.exception;

public class FilmRegistaNonCompatibileException extends RuntimeException {

    public FilmRegistaNonCompatibileException(String titolo, Integer anno) {
        super("Il film '" + titolo + "' (" + anno + ") ha una data non compatibile con il regista associato o ha superato il centenario della sua nascita.");
    }
}