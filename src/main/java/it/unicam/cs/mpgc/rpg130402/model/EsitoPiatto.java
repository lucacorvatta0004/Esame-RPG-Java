package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Risultato della preparazione di un singolo piatto.
 * La preparazione dice se è riuscito, con quali statistiche è stato cucinato e il motivo dell'esito;
 * minutiPreparazione dipende dalla velocità usata; progresso dice come è cambiata la carriera
 * (esperienza, livello, grado); reputazioneDopo è la reputazione dello chef subito dopo questo piatto.
 */
public record EsitoPiatto(Piatto piatto, Preparazione preparazione, int minutiPreparazione,
                          Progresso progresso, int reputazioneDopo) {

    public boolean riuscito() {
        return preparazione.riuscito();
    }

    /** Perché il piatto è riuscito o è bruciato, con la regola della mossa usata. */
    public String motivo() {
        return preparazione.motivo();
    }
}
