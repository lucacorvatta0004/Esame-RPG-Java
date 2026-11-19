package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Risultato di un servizio: i suoi tavoli, in ordine di arrivo, e se lo chef è stato licenziato durante il servizio.
 * Da qui si sa anche come sta la carriera (vedi {@link #statoCarriera()}) e a quale tavolo era seduto
 * l'ispettore Michelin, se è venuto. Se il servizio si è fermato prima della fine (game over o giudizio
 * dell'ispettore), alcuni tavoli possono essere ancora in attesa o non ancora arrivati: questi non sono
 * né serviti né andati via.
 */
public record EsitoServizio(List<Tavolo> tavoli, boolean chefLicenziato) {

    public EsitoServizio {
        tavoli = List.copyOf(tavoli);
    }

    /**
     * Come sta la carriera dopo il servizio. Il giudizio dell'ispettore Michelin, appena c'è, conta più
     * del licenziamento: un piatto bruciato o un'attesa troppo lunga al suo tavolo lo bocciano anche con vite
     * rimaste; tutti i piatti riusciti e in tempo rendono il ristorante stellato.
     */
    public StatoCarriera statoCarriera() {
        Optional<Tavolo> tavoloDellIspettore = tavoloDellIspettore();
        if (tavoloDellIspettore.isPresent() && tavoloDellIspettore.get().isDeluso()) {
            return StatoCarriera.BOCCIATO_DALL_ISPETTORE;
        }
        if (tavoloDellIspettore.isPresent() && tavoloDellIspettore.get().isServito()) {
            return StatoCarriera.RISTORANTE_STELLATO;
        }
        return chefLicenziato ? StatoCarriera.LICENZIATO : StatoCarriera.IN_CORSO;
    }

    /** Il tavolo dove era seduto l'ispettore Michelin in incognito, oppure vuoto se in questo servizio non è venuto. */
    public Optional<Tavolo> tavoloDellIspettore() {
        return tavoli.stream()
                .filter(Tavolo::isDellIspettore)
                .findFirst();
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
