package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Risultato della preparazione di un singolo piatto.
 * reputazioneDopo è la reputazione dello chef subito dopo questo piatto.
 */
public record EsitoPiatto(Piatto piatto, boolean riuscito, int reputazioneDopo) {
}
