package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * L'ordinazione di un tavolo: il numero del tavolo e i piatti ordinati.
 * Una comanda può essere dell'ispettore Michelin in incognito: lo sa solo il model
 * (vedi {@link #isDellIspettore()}), da fuori è una comanda come le altre.
 */
public class Comanda {
    private final int numeroTavolo;
    private final List<Piatto> piattiOrdinati;
    private final boolean dellIspettore;

    public Comanda(int numeroTavolo) {
        this(numeroTavolo, false);
    }

    private Comanda(int numeroTavolo, boolean dellIspettore) {
        Validazione.verificaAlmeno(numeroTavolo, 1, "Il numero del tavolo");
        this.numeroTavolo = numeroTavolo;
        this.piattiOrdinati = new ArrayList<>();
        this.dellIspettore = dellIspettore;
    }

    /** Una comanda, ancora senza piatti, dell'ispettore Michelin seduto al tavolo dato. */
    static Comanda dellIspettore(int numeroTavolo) {
        return new Comanda(numeroTavolo, true);
    }

    public void aggiungiPiatto(Piatto piatto) {
        Validazione.verificaNonNullo(piatto, "Il piatto da aggiungere alla comanda");
        this.piattiOrdinati.add(piatto);
    }

    public List<Piatto> getPiattiOrdinati() {
        return Collections.unmodifiableList(this.piattiOrdinati);
    }

    public int getNumeroTavolo() {
        return this.numeroTavolo;
    }

    /** A ordinare è l'ispettore Michelin. Visibile solo nel model: per il giocatore resta un segreto fino alla fine. */
    boolean isDellIspettore() {
        return this.dellIspettore;
    }
}
