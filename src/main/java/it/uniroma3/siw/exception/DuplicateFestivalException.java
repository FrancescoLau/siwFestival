package it.uniroma3.siw.exception;

public class DuplicateFestivalException extends RuntimeException {

    public DuplicateFestivalException(String nome, Integer anno, String citta) {
        super("Il festival '" + nome + "' (" + anno + ", " + citta + ") è già presente nel sistema");
    }
}