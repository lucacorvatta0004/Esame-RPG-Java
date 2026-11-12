package it.unicam.cs.mpgc.rpg130402.persistence;

/**
 * I dati fissi del gioco mancano o sono rovinati: è un errore di chi ha preparato il gioco, non del giocatore,
 * e il gioco non può partire. Per questo è unchecked, a differenza di {@link SalvataggioException}.
 * Il messaggio dice quale file (e quale voce) correggere; l'errore tecnico originale resta in getCause().
 */
public class CatalogoException extends RuntimeException {

    public CatalogoException(String messaggio) {
        super(messaggio);
    }

    public CatalogoException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
