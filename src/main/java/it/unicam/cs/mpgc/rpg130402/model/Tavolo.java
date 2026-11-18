package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Un tavolo di clienti durante un servizio: la sua comanda, quando si siede, quanto è disposto ad aspettare
 * e quali piatti ha già ricevuto. Il tavolo numero N si siede (N − 1) × {@value #MINUTI_TRA_DUE_ARRIVI} minuti
 * dopo l'inizio del servizio e aspetta al massimo {@value #PAZIENZA_IN_MINUTI} minuti da quando si siede.
 * Da fuori del model si può solo leggere: i piatti preparati e l'uscita del tavolo li segna il {@link Servizio}.
 */
public class Tavolo {
    /** Ogni quanti minuti si siede un tavolo nuovo. */
    public static final int MINUTI_TRA_DUE_ARRIVI = 25;
    /** Quanti minuti aspetta un tavolo, da quando si siede, prima di andarsene. */
    public static final int PAZIENZA_IN_MINUTI = 40;

    private final Comanda comanda;
    private final List<EsitoPiatto> esitiPiatti;
    private boolean andatoVia;

    /** @throws IllegalArgumentException se la comanda è null o non ha piatti */
    Tavolo(Comanda comanda) {
        Validazione.verificaNonNullo(comanda, "La comanda del tavolo");
        Validazione.verificaListaNonVuota(comanda.getPiattiOrdinati(), "La lista dei piatti del tavolo");
        this.comanda = comanda;
        this.esitiPiatti = new ArrayList<>();
    }

    public int getNumero() {
        return comanda.getNumeroTavolo();
    }

    public Comanda getComanda() {
        return comanda;
    }

    /** Il minuto del servizio in cui il tavolo si siede: da qui inizia a contare la sua pazienza. */
    public int getMinutoArrivo() {
        return (getNumero() - 1) * MINUTI_TRA_DUE_ARRIVI;
    }

    /** L'ultimo minuto in cui il tavolo aspetta ancora: dopo questo minuto, se aspetta dei piatti, se ne va. */
    public int getMinutoLimite() {
        return getMinutoArrivo() + PAZIENZA_IN_MINUTI;
    }

    public int getNumeroPiatti() {
        return comanda.getPiattiOrdinati().size();
    }

    /** I piatti già preparati per il tavolo, riusciti o bruciati. */
    public int getPiattiPreparati() {
        return esitiPiatti.size();
    }

    public int piattiMancanti() {
        return getNumeroPiatti() - getPiattiPreparati();
    }

    public int piattiRiusciti() {
        return (int) esitiPiatti.stream()
                .filter(EsitoPiatto::riuscito)
                .count();
    }

    public int piattiBruciati() {
        return getPiattiPreparati() - piattiRiusciti();
    }

    /** L'esperienza data dai piatti preparati per il tavolo (negativa se persa): i piatti mancanti non contano. */
    public int esperienzaGuadagnata() {
        return esitiPiatti.stream()
                .mapToInt(esitoPiatto -> esitoPiatto.progresso().esperienzaGuadagnata())
                .sum();
    }

    /** Il tavolo ha ricevuto tutti i piatti della comanda. */
    public boolean isServito() {
        return piattiMancanti() == 0;
    }

    public boolean isAndatoVia() {
        return andatoVia;
    }

    /** Il prossimo piatto da preparare, nell'ordine della comanda. */
    Piatto prossimoPiatto() {
        verificaInAttesa();
        return comanda.getPiattiOrdinati().get(getPiattiPreparati());
    }

    /** Al minuto dato il tavolo ha aspettato più della sua pazienza. */
    boolean haFinitoLaPazienza(int minuto) {
        return minuto > getMinutoLimite();
    }

    void registraPiatto(EsitoPiatto esitoPiatto) {
        Validazione.verificaNonNullo(esitoPiatto, "L'esito del piatto servito");
        verificaInAttesa();
        esitiPiatti.add(esitoPiatto);
    }

    void vaVia() {
        verificaInAttesa();
        andatoVia = true;
    }

    /** Solo un tavolo che aspetta ancora dei piatti può riceverne uno o andarsene. */
    private void verificaInAttesa() {
        if (isServito() || isAndatoVia()) {
            throw new IllegalStateException("Il Tavolo " + getNumero() + " non aspetta più nessun piatto");
        }
    }
}
