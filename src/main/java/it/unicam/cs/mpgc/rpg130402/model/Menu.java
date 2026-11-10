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

        this.listaPiatti.add(new Piatto("Spaghetti al Pomodoro", 1));
        this.listaPiatti.add(new Piatto("Carbonara", 2));
        this.listaPiatti.add(new Piatto("Risotto ai Funghi", 3));
        this.listaPiatti.add(new Piatto("Filetto alla Wellington", 4));
        this.listaPiatti.add(new Piatto("Soufflé al Cioccolato", 5));
    }

    /**
     * Crea una comanda con piatti estratti a caso tra quelli che hanno al massimo stelleMassime stelle,
     * così le ordinazioni sono proporzionate a chi cucina (vedi {@link Grado#getStelleMassime()}).
     *
     * @throws IllegalStateException se nel menu non c'è nessun piatto con al massimo stelleMassime stelle
     */
    public Comanda generaComandaCasuale(int numeroTavolo, int numeroPiatti, int stelleMassime) {
        Validazione.verificaAlmeno(numeroPiatti, 1, "Il numero di piatti della comanda");
        Validazione.verificaAlmeno(stelleMassime, 1, "Il numero massimo di stelle dei piatti");
        List<Piatto> piattiAdatti = piattiFino(stelleMassime);
        Comanda nuovaComanda = new Comanda(numeroTavolo);

        for (int i = 0; i < numeroPiatti; i++) {
            Piatto piattoEstratto = estraiPiattoCasuale(piattiAdatti);
            nuovaComanda.aggiungiPiatto(piattoEstratto);
        }

        return nuovaComanda;
    }

    private List<Piatto> piattiFino(int stelleMassime) {
        List<Piatto> piattiAdatti = this.listaPiatti.stream()
                .filter(piatto -> piatto.getDifficoltaStelle() <= stelleMassime)
                .toList();
        if (piattiAdatti.isEmpty()) {
            throw new IllegalStateException("Nel menu non ci sono piatti fino a "
                    + Testo.conNumero(stelleMassime, "stella", "stelle"));
        }
        return piattiAdatti;
    }

    private Piatto estraiPiattoCasuale(List<Piatto> piatti) {
        int indiceRandom = this.generatore.nextInt(piatti.size());
        return piatti.get(indiceRandom);
    }
}
