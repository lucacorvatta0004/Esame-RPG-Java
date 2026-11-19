package it.unicam.cs.mpgc.rpg130402.model;

/**
 * A che punto è la carriera dello chef dopo un servizio (vedi {@link EsitoServizio#statoCarriera()}).
 * Solo con {@link #IN_CORSO} si gioca un altro servizio: gli altri stati chiudono la carriera.
 */
public enum StatoCarriera {
    /** La carriera continua: si può giocare un altro servizio. */
    IN_CORSO,
    /** La reputazione è arrivata a 0: game over. */
    LICENZIATO,
    /** Al tavolo dell'ispettore Michelin un piatto è bruciato o il tavolo è andato via: game over, anche con vite rimaste. */
    BOCCIATO_DALL_ISPETTORE,
    /** Il tavolo dell'ispettore Michelin ha ricevuto tutti i piatti riusciti e in tempo: il ristorante è stellato, vittoria. */
    RISTORANTE_STELLATO;

    /** La carriera è finita, con una vittoria o con un game over. */
    public boolean isFinita() {
        return this != IN_CORSO;
    }

    public boolean isVittoria() {
        return this == RISTORANTE_STELLATO;
    }
}
