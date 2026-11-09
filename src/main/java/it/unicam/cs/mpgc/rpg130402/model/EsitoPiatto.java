package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Risultato della preparazione di un singolo piatto.
 * La preparazione dice se è riuscito e con quali statistiche è stato cucinato;
 * minutiPreparazione dipende dalla velocità usata; reputazioneDopo è la reputazione
 * dello chef subito dopo questo piatto.
 */
public record EsitoPiatto(Piatto piatto, Preparazione preparazione, int minutiPreparazione, int reputazioneDopo) {

    public boolean riuscito() {
        return preparazione.riuscito();
    }

    /** La maestria usata davvero sul piatto, con i bonus o i malus della mossa. */
    public int maestriaUsata() {
        return preparazione.statisticheUsate().maestria();
    }
}
