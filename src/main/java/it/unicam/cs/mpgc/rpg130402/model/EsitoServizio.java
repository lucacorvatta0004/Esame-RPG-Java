package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;

/**
 * Risultato del servizio di un tavolo: gli esiti dei piatti preparati, in ordine,
 * e se lo chef è stato licenziato durante il servizio.
 */
public record EsitoServizio(List<EsitoPiatto> esitiPiatti, boolean chefLicenziato) {

    public EsitoServizio {
        esitiPiatti = List.copyOf(esitiPiatti);
    }

    public int piattiRiusciti() {
        return (int) esitiPiatti.stream()
                .filter(EsitoPiatto::riuscito)
                .count();
    }

    public int piattiBruciati() {
        return esitiPiatti.size() - piattiRiusciti();
    }

    /** L'esperienza guadagnata in tutto il servizio: la somma di quella dei piatti (negativa se persa). */
    public int esperienzaGuadagnata() {
        return esitiPiatti.stream()
                .mapToInt(esitoPiatto -> esitoPiatto.progresso().esperienzaGuadagnata())
                .sum();
    }
}
