package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Comanda {
    private final int numeroTavolo;
    private final List<Piatto> piattiOrdinati;

    public Comanda(int numeroTavolo) {
        Validazione.verificaAlmeno(numeroTavolo, 1, "Il numero del tavolo");
        this.numeroTavolo = numeroTavolo;
        this.piattiOrdinati = new ArrayList<>();
    }

    public void aggiungiPiatto(Piatto p) {
        Validazione.verificaNonNullo(p, "Il piatto da aggiungere alla comanda");
        this.piattiOrdinati.add(p);
    }

    public List<Piatto> getPiattiOrdinati() {
        return Collections.unmodifiableList(this.piattiOrdinati);
    }

    public int getNumeroTavolo() {
        return this.numeroTavolo;
    }

    public void stampaScontrino() {
        System.out.println("\n=== ORDINE TAVOLO " + this.numeroTavolo + " ===");
        for (int i = 0; i < this.piattiOrdinati.size(); i++) {
            Piatto piattoCorrente = this.piattiOrdinati.get(i);
            piattoCorrente.mostraDettagli();
        }
    }
}
