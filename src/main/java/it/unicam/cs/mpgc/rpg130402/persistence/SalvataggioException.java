package it.unicam.cs.mpgc.rpg130402.persistence;

/**
 * Il salvataggio non si può usare: non si riesce a leggerlo, scriverlo o cancellarlo,
 * oppure è rovinato o ha valori non validi.
 * Il messaggio è in italiano e si può mostrare al giocatore; l'errore tecnico originale resta in getCause().
 */
public class SalvataggioException extends Exception {

    public SalvataggioException(String messaggio) {
        super(messaggio);
    }

    public SalvataggioException(String messaggio, Throwable causa) {
        super(messaggio, causa);
    }
}
