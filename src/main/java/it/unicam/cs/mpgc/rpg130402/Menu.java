package it.unicam.cs.mpgc.rpg130402;

import java.util.ArrayList;
import java.util.Random; // importo il random base di java

public class Menu {

    // lista base per tenere tutte le comande possibili
    private ArrayList<Comanda> listaPiatti;

    // costruttore
    public Menu() {
        // inizializzo la lista vuota
        this.listaPiatti = new ArrayList<>();
        // riempio il menu chiamando il metodo qui sotto
        caricaMenuBase();
    }

    // metodo semplice per aggiungere i piatti a mano all'inizio
    private void caricaMenuBase() {
        this.listaPiatti.add(new Comanda("Spaghetti al Pomodoro", 1));
        this.listaPiatti.add(new Comanda("Carbonara", 2));
        this.listaPiatti.add(new Comanda("Risotto ai Funghi", 3));
        this.listaPiatti.add(new Comanda("Filetto alla Wellington", 4));
        this.listaPiatti.add(new Comanda("Souffle' al Cioccolato", 5));
    }

    // estrae una comanda a caso per il servizio
    public Comanda estraiComandaCasuale() {
        Random generatore = new Random();

        // prendo un numero a caso tra 0 e la grandezza attuale della lista
        int indiceRandom = generatore.nextInt(this.listaPiatti.size());

        // recupero il piatto a quell'indice e lo salvo in una variabile d'appoggio
        Comanda piattoScelto = this.listaPiatti.get(indiceRandom);

        return piattoScelto;
    }
}