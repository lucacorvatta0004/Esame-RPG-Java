package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;
import java.util.stream.IntStream;

/**
 * La carriera dello chef giocata un servizio dopo l'altro, finché il giocatore vuole
 * o finché lo chef non viene licenziato. Ogni servizio si prepara con il grado che lo chef ha
 * all'inizio del servizio: quanti tavoli arrivano e fino a quante stelle possono avere i loro piatti.
 */
public class Partita {
    private static final int PIATTI_PER_TAVOLO = 3;

    private final Chef chef;
    private final Menu menu;
    private final SceltaAzione sceltaAzione;

    public Partita(Chef chef, Menu menu, SceltaAzione sceltaAzione) {
        Validazione.verificaNonNullo(chef, "Lo chef della partita");
        Validazione.verificaNonNullo(menu, "Il menu della partita");
        Validazione.verificaNonNullo(sceltaAzione, "La scelta delle azioni");
        this.chef = chef;
        this.menu = menu;
        this.sceltaAzione = sceltaAzione;
    }

    /**
     * Gioca un servizio intero con tavoli nuovi e racconta all'osservatore ogni evento mentre accade.
     * Restituisce come è andato il servizio, anche se si è fermato per il game over.
     *
     * @throws IllegalStateException se lo chef è già stato licenziato: la carriera è finita
     */
    public EsitoServizio giocaServizio(OsservatoreServizio osservatore) {
        if (chef.isLicenziato()) {
            throw new IllegalStateException("La carriera di " + chef.getNome() + " è finita: è stato licenziato");
        }
        return new Servizio(chef, sceltaAzione, preparaComande()).gioca(osservatore);
    }

    /** Una comanda per ogni tavolo del servizio, numerati da 1, con piatti adatti al grado dello chef. */
    private List<Comanda> preparaComande() {
        Grado grado = chef.getGrado();
        return IntStream.rangeClosed(1, grado.getTavoliPerServizio())
                .mapToObj(numeroTavolo -> menu.generaComandaCasuale(numeroTavolo, PIATTI_PER_TAVOLO, grado.getStelleMassime()))
                .toList();
    }
}
