package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Il menu del ristorante e le ordinazioni dei clienti: a ogni servizio crea le comande dei tavoli,
 * scegliendo a caso i piatti, i piatti sfida e se arriva l'ispettore Michelin.
 */
public class Menu {
    private static final int PIATTI_PER_TAVOLO = 3;
    private static final int CENTO_PER_CENTO = 100;

    private final List<Piatto> listaPiatti;
    private final int stelleDeiPiattiPiuDifficili;
    private final Random generatore;

    /**
     * Il menu con i piatti ricevuti da fuori (per esempio quelli del catalogo): il menu non sa da dove arrivano.
     * La lista viene copiata, quindi cambiarla dopo non cambia il menu.
     *
     * @throws IllegalArgumentException se la lista è null, è vuota o contiene null
     */
    public Menu(List<Piatto> piatti) {
        this(piatti, new Random());
    }

    /** Usa il generatore ricevuto: con un seme fisso (per esempio nei test) le comande sono ripetibili. */
    public Menu(List<Piatto> piatti, Random generatore) {
        Validazione.verificaListaNonVuota(piatti, "La lista dei piatti del menu");
        Validazione.verificaNonNullo(generatore, "Il generatore casuale");
        this.listaPiatti = List.copyOf(piatti);
        this.stelleDeiPiattiPiuDifficili = listaPiatti.stream()
                .mapToInt(Piatto::getDifficoltaStelle)
                .max()
                .orElseThrow();
        this.generatore = generatore;
    }

    /**
     * Le comande di un servizio per lo chef del grado dato: una per tavolo, numerati da 1, ognuna con
     * {@value #PIATTI_PER_TAVOLO} piatti. Ogni piatto ha al massimo le stelle del grado, ma con la percentuale
     * di piatti sfida del grado è di una stella in più. Con la percentuale dell'ispettore del grado,
     * l'ispettore Michelin prende il posto di un tavolo a caso e ordina solo i piatti più difficili del menu.
     *
     * @throws IllegalStateException se nel menu mancano i piatti adatti al grado o i suoi piatti sfida
     */
    public List<Comanda> generaComandeDelServizio(Grado grado) {
        Validazione.verificaNonNullo(grado, "Il grado dello chef");
        List<Comanda> comande = new ArrayList<>(IntStream.rangeClosed(1, grado.getTavoliPerServizio())
                .mapToObj(numeroTavolo -> comandaCasuale(numeroTavolo, grado))
                .toList());
        if (succede(grado.getPercentualeIspettore())) {
            int numeroTavolo = 1 + generatore.nextInt(comande.size());
            comande.set(numeroTavolo - 1, comandaDellIspettore(numeroTavolo));
        }
        return List.copyOf(comande);
    }

    private Comanda comandaCasuale(int numeroTavolo, Grado grado) {
        return riempi(new Comanda(numeroTavolo), () -> piattoPer(grado));
    }

    /** Di solito un piatto fino alle stelle del grado; con la percentuale del grado un piatto sfida, di una stella in più. */
    private Piatto piattoPer(Grado grado) {
        int stelleMassime = grado.getStelleMassime();
        if (succede(grado.getPercentualePiattiSfida())) {
            return estraiPiattoCasuale(piattiTra(stelleMassime + 1, stelleMassime + 1));
        }
        return estraiPiattoCasuale(piattiTra(1, stelleMassime));
    }

    private Comanda comandaDellIspettore(int numeroTavolo) {
        List<Piatto> piattiPiuDifficili = piattiTra(stelleDeiPiattiPiuDifficili, stelleDeiPiattiPiuDifficili);
        return riempi(Comanda.dellIspettore(numeroTavolo), () -> estraiPiattoCasuale(piattiPiuDifficili));
    }

    private Comanda riempi(Comanda comanda, Supplier<Piatto> prossimoPiatto) {
        for (int i = 0; i < PIATTI_PER_TAVOLO; i++) {
            comanda.aggiungiPiatto(prossimoPiatto.get());
        }
        return comanda;
    }

    /** Vero con la probabilità data, in percentuale: 0 mai, 100 sempre. */
    private boolean succede(int percentuale) {
        return generatore.nextInt(CENTO_PER_CENTO) < percentuale;
    }

    private List<Piatto> piattiTra(int stelleMinime, int stelleMassime) {
        List<Piatto> piattiAdatti = this.listaPiatti.stream()
                .filter(piatto -> piatto.getDifficoltaStelle() >= stelleMinime)
                .filter(piatto -> piatto.getDifficoltaStelle() <= stelleMassime)
                .toList();
        if (piattiAdatti.isEmpty()) {
            throw new IllegalStateException("Nel menu non ci sono piatti con stelle da "
                    + stelleMinime + " a " + stelleMassime);
        }
        return piattiAdatti;
    }

    private Piatto estraiPiattoCasuale(List<Piatto> piatti) {
        int indiceRandom = this.generatore.nextInt(piatti.size());
        return piatti.get(indiceRandom);
    }
}
