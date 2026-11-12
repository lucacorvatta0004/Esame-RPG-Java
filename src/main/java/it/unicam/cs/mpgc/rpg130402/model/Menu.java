package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;
import java.util.Random;

public class Menu {
    private final List<Piatto> listaPiatti;
    private final Random generatore;

    /**
     * Il menu con i piatti ricevuti da fuori (per esempio quelli del catalogo): il menu non sa da dove arrivano.
     * La lista viene copiata, quindi cambiarla dopo non cambia il menu.
     *
     * @throws IllegalArgumentException se la lista è null, è vuota o contiene null
     */
    public Menu(List<Piatto> piatti) {
        Validazione.verificaListaNonVuota(piatti, "La lista dei piatti del menu");
        this.listaPiatti = List.copyOf(piatti);
        this.generatore = new Random();
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
