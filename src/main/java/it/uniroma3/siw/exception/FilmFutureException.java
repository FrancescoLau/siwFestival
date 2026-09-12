package it.uniroma3.siw.exception;

public class FilmFutureException extends RuntimeException {

	  public FilmFutureException(String titolo, Integer anno) {
	    super("Non e possibile inserire film oltre l'anno corrente");
	  }
	}


