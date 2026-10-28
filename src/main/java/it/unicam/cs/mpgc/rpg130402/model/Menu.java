package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;
import java.util.Random;

public class Menu {
    private ArrayList<Piatto> listaPiatti;

    public Menu() {
        this.listaPiatti = new ArrayList<>();

        // popolo il menu con dei piatti base
        this.listaPiatti.add(new Piatto("Spaghetti al Pomodoro", 1));
        this.listaPiatti.add(new Piatto("Carbonara", 2));
        this.listaPiatti.add(new Piatto("Risotto ai Funghi", 3));
        this.listaPiatti.add(new Piatto("Filetto alla Wellington", 4));
        this.listaPiatti.add(new Piatto("Soufflé al Cioccolato", 5));
    }

    // estrae un singolo piatto casuale dal menu
    public Piatto estraiPiattoCasuale() {
        Random generatore = new Random();
        int indiceRandom = generatore.nextInt(this.listaPiatti.size());
        return this.listaPiatti.get(indiceRandom);
    }

    // genera un tavolo (Comanda) con un numero N di piatti casuali
    public Comanda generaComandaCasuale(int numeroTavolo, int numeroPiatti) {
        Comanda nuovaComanda = new Comanda(numeroTavolo);

        // ciclo for per aggiungere i piatti uno alla volta
        for (int i = 0; i < numeroPiatti; i++) {
            Piatto piattoEstratto = estraiPiattoCasuale();
            nuovaComanda.aggiungiPiatto(piattoEstratto);
        }

        return nuovaComanda;
    }
}