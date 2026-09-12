package it.uniroma3.siw.exception;

public class SalaNonDisponibileException extends RuntimeException{
	public SalaNonDisponibileException(String nome) {
		super("La sala "+ nome + "non e' disponibile nella data e orario selezionati");
	}

}
