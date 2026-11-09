package it.unicam.cs.mpgc.rpg130402.model;

import java.util.Random;

/** Azione base: lo chef improvvisa e il piatto riesce una volta su due, qualunque sia la maestria. */
public class Improvvisazione implements Azione {
    private final Random generatore;

    public Improvvisazione() {
        this(new Random());
    }

    /** Usa il generatore ricevuto: con un seme fisso (per esempio nei test) i risultati sono ripetibili. */
    public Improvvisazione(Random generatore) {
        Validazione.verificaNonNullo(generatore, "Il generatore casuale");
        this.generatore = generatore;
    }

    @Override
    public String getNome() {
        return "Improvvisazione";
    }

    @Override
    public String getDescrizione() {
        return "riesce una volta su due, la maestria non conta";
    }

    @Override
    public Preparazione esegui(Piatto piatto, Statistiche statistiche) {
        return new Preparazione(generatore.nextBoolean(), statistiche);
    }
}
