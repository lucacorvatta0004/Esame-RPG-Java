package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Chi vuole sapere cosa succede durante un servizio, mentre succede: per esempio la console o la GUI che lo mostrano.
 * Il {@link Servizio} chiama questi metodi nel momento in cui le cose accadono; chi li implementa li usa
 * per mostrarle e non può cambiare il servizio (da fuori del model un {@link Tavolo} si può solo leggere).
 * La fine del servizio e della carriera (game over o vittoria) non sono qui: le dice l'{@link EsitoServizio}
 * restituito da {@link Partita#giocaServizio}. Anche quale tavolo è dell'ispettore Michelin si sa solo dall'esito:
 * durante il servizio l'ispettore è in incognito e i suoi eventi sono uguali a quelli degli altri tavoli.
 */
public interface OsservatoreServizio {

    /** Un tavolo si è seduto, al suo minuto di arrivo (anche mentre lo chef cucinava): da lì conta la sua pazienza. */
    void tavoloArrivato(Tavolo tavolo);

    /** Lo chef sta per preparare il prossimo piatto del tavolo; subito dopo il giocatore sceglie l'azione. */
    void piattoDaPreparare(Tavolo tavolo, int minuto);

    /** Un piatto è pronto, riuscito o bruciato: l'esito dice anche tempo, esperienza e reputazione. */
    void piattoPreparato(EsitoPiatto esitoPiatto);

    /** Il tavolo ha ricevuto tutti i suoi piatti, al minuto dato. */
    void tavoloServito(Tavolo tavolo, int minuto);

    /** Il tavolo ha aspettato oltre il suo minuto limite ed è andato via: lo chef ha perso una vita. */
    void tavoloAndatoVia(Tavolo tavolo, int reputazioneDopo);
}
