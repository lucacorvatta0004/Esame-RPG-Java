package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Menu {
    private final List<Piatto> listaPiatti;
    private final Random generatore;

    public Menu() {
        this.listaPiatti = new ArrayList<>();
        this.generatore = new Random();

        // popolo il menu con dei piatti base
        this.listaPiatti.add(new Piatto("Spaghetti al Pomodoro", 1));
        this.listaPiatti.add(new Piatto("Carbonara", 2));
        this.listaPiatti.add(new Piatto("Risotto ai Funghi", 3));
        this.listaPiatti.add(new Piatto("Filetto alla Wellington", 4));
        this.listaPiatti.add(new Piatto("Soufflé al Cioccolato", 5));
    }

    // estrae un singolo piatto casuale dal menu
    public Piatto estraiPiattoCasuale() {
        int indiceRandom = this.generatore.nextInt(this.listaPiatti.size());
        return this.listaPiatti.get(indiceRandom);
    }

    // genera un tavolo (Comanda) con un numero N di piatti casuali
    public Comanda generaComandaCasuale(int numeroTavolo, int numeroPiatti) {
        Validazione.verificaAlmeno(numeroPiatti, 1, "Il numero di piatti della comanda");
        Comanda nuovaComanda = new Comanda(numeroTavolo);

        // ciclo for per aggiungere i piatti uno alla volta
        for (int i = 0; i < numeroPiatti; i++) {
            Piatto piattoEstratto = estraiPiattoCasuale();
            nuovaComanda.aggiungiPiatto(piattoEstratto);
        }

        return nuovaComanda;
    }
}