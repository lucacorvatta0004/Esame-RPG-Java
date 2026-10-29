package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;

public class Comanda {
    private int numeroTavolo;
    // la lista della spesa che contiene tutti i piatti di questo tavolo
    private ArrayList<Piatto> piattiOrdinati;

    public Comanda(int numeroTavolo) {
        Validazione.verificaAlmeno(numeroTavolo, 1, "Il numero del tavolo");
        this.numeroTavolo = numeroTavolo;
        this.piattiOrdinati = new ArrayList<>(); // preparo la scatola vuota
    }

    // metodo semplice per aggiungere un piatto alla lista
    public void aggiungiPiatto(Piatto p) {
        this.piattiOrdinati.add(p);
    }

    public ArrayList<Piatto> getPiattiOrdinati() {
        return this.piattiOrdinati;
    }

    public int getNumeroTavolo() {
        return this.numeroTavolo;
    }

    public void stampaScontrino() {
        System.out.println("\n=== ORDINE TAVOLO " + this.numeroTavolo + " ===");
        // ciclo for scolastico per stampare tutto quello che c'è nella lista
        for (int i = 0; i < this.piattiOrdinati.size(); i++) {
            Piatto piattoCorrente = this.piattiOrdinati.get(i);
            piattoCorrente.mostraDettagli();
        }
    }
}