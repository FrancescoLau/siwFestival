package it.uniroma3.siw.exception;

import java.time.LocalDate;

public class FestivalDataFineBeforeDataInizioException extends RuntimeException{
	public FestivalDataFineBeforeDataInizioException(LocalDate dataInizio, LocalDate dataFine) {
		super("La data d'inizio del festival deve essere precedente alla data di fine");
	}
}
