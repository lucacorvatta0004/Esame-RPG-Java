package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Come è andata la preparazione di un piatto con un'azione: se il piatto è riuscito, con quali statistiche
 * è stato cucinato davvero (bonus della mossa compresi) e il motivo dell'esito, una frase da mostrare
 * al giocatore (per esempio "serve 30 maestria, hai 25"). Il motivo lo scrive la regola che ha deciso l'esito,
 * quindi ogni mossa spiega il proprio: chi lo mostra non deve sapere quale mossa è stata usata.
 */
public record Preparazione(boolean riuscito, Statistiche statisticheUsate, String motivo) {

    public Preparazione {
        Validazione.verificaNonNullo(statisticheUsate, "Le statistiche usate");
        Validazione.verificaTestoNonVuoto(motivo, "Il motivo dell'esito");
    }

    /** La regola di base: il piatto riesce se la maestria usata arriva a quella richiesta dal piatto. */
    public static Preparazione secondoMaestria(Piatto piatto, Statistiche statistiche) {
        boolean riuscito = statistiche.maestria() >= piatto.getMaestriaRichiesta();
        String motivo = "serve " + piatto.getMaestriaRichiesta() + " maestria, hai " + statistiche.maestria();
        return new Preparazione(riuscito, statistiche, motivo);
    }
}
