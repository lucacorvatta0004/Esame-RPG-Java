package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;
import java.util.function.Predicate;

/**
 * Risultato di un servizio: i suoi tavoli, in ordine di arrivo, e se lo chef è stato licenziato durante il servizio.
 * Se il servizio si è fermato per il game over, alcuni tavoli possono essere ancora in attesa
 * o non ancora arrivati: questi non sono né serviti né andati via.
 */
public record EsitoServizio(List<Tavolo> tavoli, boolean chefLicenziato) {

    public EsitoServizio {
        tavoli = List.copyOf(tavoli);
    }

    public int tavoliServiti() {
        return contaTavoli(Tavolo::isServito);
    }

    public int tavoliAndatiVia() {
        return contaTavoli(Tavolo::isAndatoVia);
    }

    /** L'esperienza guadagnata in tutto il servizio: la somma di quella dei tavoli (negativa se persa). */
    public int esperienzaGuadagnata() {
        return tavoli.stream()
                .mapToInt(Tavolo::esperienzaGuadagnata)
                .sum();
    }

    private int contaTavoli(Predicate<Tavolo> condizione) {
        return (int) tavoli.stream()
                .filter(condizione)
                .count();
    }
}
