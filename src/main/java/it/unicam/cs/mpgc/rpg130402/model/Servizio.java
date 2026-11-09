package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Un servizio in cucina: lo chef prepara i piatti dei tavoli con le azioni scelte dal giocatore.
 * Le azioni si creano all'inizio del servizio, quindi gli usi delle speciali valgono per tutto il servizio.
 */
public class Servizio {
    private final Chef chef;
    private final SceltaAzione sceltaAzione;
    private final List<Azione> azioni;

    public Servizio(Chef chef, SceltaAzione sceltaAzione) {
        Validazione.verificaNonNullo(chef, "Lo chef del servizio");
        Validazione.verificaNonNullo(sceltaAzione, "La scelta delle azioni");
        this.chef = chef;
        this.sceltaAzione = sceltaAzione;
        this.azioni = chef.getGrado().creaAzioniPerServizio();
    }

    /**
     * Prepara i piatti della comanda nell'ordine in cui sono stati ordinati, ognuno con l'azione scelta.
     * Dopo ogni piatto passa il suo esito a dopoOgniPiatto (per esempio per mostrarlo subito).
     * Se lo chef viene licenziato il servizio si interrompe e i piatti rimanenti non vengono preparati.
     *
     * @throws IllegalStateException se la scelta restituisce un'azione che non è tra quelle disponibili
     */
    public EsitoServizio serviTavolo(Comanda comanda, Consumer<EsitoPiatto> dopoOgniPiatto) {
        Validazione.verificaNonNullo(comanda, "La comanda da servire");
        Validazione.verificaNonNullo(dopoOgniPiatto, "Cosa fare dopo ogni piatto");
        List<EsitoPiatto> esitiPiatti = new ArrayList<>();

        for (Piatto piatto : comanda.getPiattiOrdinati()) {
            EsitoPiatto esitoPiatto = chef.cucina(piatto, scegliAzione(piatto));
            esitiPiatti.add(esitoPiatto);
            dopoOgniPiatto.accept(esitoPiatto);
            if (chef.isLicenziato()) {
                break;
            }
        }

        return new EsitoServizio(esitiPiatti, chef.isLicenziato());
    }

    /** Fa scegliere l'azione e accetta solo una di quelle proposte: sbloccate dal grado e con usi rimasti. */
    private Azione scegliAzione(Piatto piatto) {
        List<Azione> disponibili = azioniDisponibili();
        Azione scelta = sceltaAzione.scegli(piatto, disponibili);
        if (!disponibili.contains(scelta)) {
            throw new IllegalStateException("L'azione scelta deve essere una di quelle disponibili");
        }
        return scelta;
    }

    private List<Azione> azioniDisponibili() {
        return azioni.stream()
                .filter(Azione::isDisponibile)
                .toList();
    }
}
