package it.uniroma3.siw.exception;

import it.uniroma3.siw.model.Film;
import it.uniroma3.siw.model.Proiezione;

public class ProiezioneFilmFuturoException extends RuntimeException {

    public ProiezioneFilmFuturoException(Proiezione proiezione, Film film) {
        super("L'anno del film non puo essere successivo alla proiezione");
    }
}