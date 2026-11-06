package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Risultato della preparazione di un singolo piatto.
 * minutiPreparazione dipende dalla velocità usata; reputazioneDopo è la reputazione
 * dello chef subito dopo questo piatto.
 */
public record EsitoPiatto(Piatto piatto, boolean riuscito, int minutiPreparazione, int reputazioneDopo) {
}
