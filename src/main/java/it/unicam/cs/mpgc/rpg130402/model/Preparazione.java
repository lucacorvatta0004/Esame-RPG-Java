package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Come è andata la preparazione di un piatto con un'azione: se il piatto è riuscito
 * e con quali statistiche è stato cucinato davvero (bonus della mossa compresi).
 */
public record Preparazione(boolean riuscito, Statistiche statisticheUsate) {

    public Preparazione {
        Validazione.verificaNonNullo(statisticheUsate, "Le statistiche usate");
    }

    /** La regola di base: il piatto riesce se la maestria usata arriva a quella richiesta dal piatto. */
    public static Preparazione secondoMaestria(Piatto piatto, Statistiche statistiche) {
        boolean riuscito = statistiche.maestria() >= piatto.getMaestriaRichiesta();
        return new Preparazione(riuscito, statistiche);
    }
}
