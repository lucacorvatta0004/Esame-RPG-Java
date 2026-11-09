package it.unicam.cs.mpgc.rpg130402.model;

import java.util.OptionalInt;

/**
 * Una mossa sbloccata dal grado, con un numero limitato di usi per servizio.
 * Gli usi non si ricaricano: per un nuovo servizio si crea una nuova istanza
 * (vedi {@link Grado#creaAzioniPerServizio()}).
 */
public abstract class AzioneSpeciale implements Azione {
    private int usiRimasti;

    protected AzioneSpeciale(int usiPerServizio) {
        Validazione.verificaAlmeno(usiPerServizio, 1, "Gli usi per servizio");
        this.usiRimasti = usiPerServizio;
    }

    @Override
    public final OptionalInt getUsiRimasti() {
        return OptionalInt.of(usiRimasti);
    }

    @Override
    public final boolean isDisponibile() {
        return usiRimasti > 0;
    }

    /** Consuma un uso e poi prepara il piatto con la regola della mossa. */
    @Override
    public final Preparazione esegui(Piatto piatto, Statistiche statistiche) {
        if (!isDisponibile()) {
            throw new IllegalStateException(getNome() + ": usi finiti per questo servizio");
        }
        usiRimasti--;
        return prepara(piatto, statistiche);
    }

    /** La regola della singola mossa, con lo stesso contratto di {@link Azione#esegui}. */
    protected abstract Preparazione prepara(Piatto piatto, Statistiche statistiche);
}
