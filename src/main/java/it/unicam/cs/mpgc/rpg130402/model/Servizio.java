package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * Un servizio in cucina, con il suo orologio in minuti simulati.
 * I tavoli si siedono uno dopo l'altro (vedi {@link Tavolo}); lo chef cucina un piatto alla volta per il primo
 * tavolo arrivato tra quelli che aspettano, e ogni piatto fa avanzare l'orologio dei suoi minuti di preparazione.
 * Un piatto iniziato non si interrompe: un tavolo se ne va solo tra un piatto e l'altro.
 * Le azioni si creano all'inizio del servizio, quindi gli usi delle speciali valgono per tutto il servizio.
 * Il servizio si ferma appena la carriera finisce: con il licenziamento o con il giudizio dell'ispettore Michelin
 * (vedi {@link EsitoServizio#statoCarriera()}).
 * Si gioca una volta sola, dalla {@link Partita}.
 */
class Servizio {
    private final Chef chef;
    private final SceltaAzione sceltaAzione;
    private final List<Azione> azioni;
    private final List<Tavolo> tavoli;
    private final Queue<Tavolo> tavoliInArrivo;
    private final Queue<Tavolo> tavoliInAttesa;
    private int minuto;

    /** Le comande sono una per tavolo; i tavoli arrivano nell'ordine dei loro minuti di arrivo. */
    Servizio(Chef chef, SceltaAzione sceltaAzione, List<Comanda> comande) {
        Validazione.verificaNonNullo(chef, "Lo chef del servizio");
        Validazione.verificaNonNullo(sceltaAzione, "La scelta delle azioni");
        Validazione.verificaListaNonVuota(comande, "Le comande del servizio");
        this.chef = chef;
        this.sceltaAzione = sceltaAzione;
        this.azioni = chef.getGrado().creaAzioniPerServizio();
        this.tavoli = comande.stream()
                .map(Tavolo::new)
                .sorted(Comparator.comparingInt(Tavolo::getMinutoArrivo))
                .toList();
        this.tavoliInArrivo = new ArrayDeque<>(tavoli);
        this.tavoliInAttesa = new ArrayDeque<>();
        this.minuto = 0;
    }

    /**
     * Gioca il servizio e racconta all'osservatore ogni evento mentre accade.
     * Finisce quando ogni tavolo è stato servito o è andato via, oppure subito quando la carriera finisce:
     * lo chef viene licenziato o l'ispettore Michelin ha dato il suo giudizio.
     *
     * @throws IllegalStateException se la scelta restituisce un'azione che non è tra quelle disponibili
     */
    EsitoServizio gioca(OsservatoreServizio osservatore) {
        Validazione.verificaNonNullo(osservatore, "L'osservatore del servizio");
        while (isInCorso()) {
            accogliTavoliArrivati(osservatore);
            mandaViaTavoliSenzaPazienza(osservatore);
            if (isInCorso()) {
                avanza(osservatore);
            }
        }
        return esitoFinora();
    }

    /** Il servizio va avanti finché ci sono tavoli da aspettare o da servire e la carriera non è finita. */
    private boolean isInCorso() {
        boolean ciSonoTavoli = !tavoliInArrivo.isEmpty() || !tavoliInAttesa.isEmpty();
        return ciSonoTavoli && !isCarrieraFinita();
    }

    /** Come è andato il servizio fino a questo momento: la regola della fine della carriera sta nell'esito. */
    private EsitoServizio esitoFinora() {
        return new EsitoServizio(tavoli, chef.isLicenziato());
    }

    private boolean isCarrieraFinita() {
        return esitoFinora().statoCarriera().isFinita();
    }

    /** Fa sedere i tavoli che sono arrivati entro il minuto attuale, anche mentre lo chef cucinava. */
    private void accogliTavoliArrivati(OsservatoreServizio osservatore) {
        while (!tavoliInArrivo.isEmpty() && tavoliInArrivo.peek().getMinutoArrivo() <= minuto) {
            Tavolo tavolo = tavoliInArrivo.poll();
            tavoliInAttesa.add(tavolo);
            osservatore.tavoloArrivato(tavolo);
        }
    }

    /** Ogni tavolo che ha superato il suo minuto limite se ne va e costa una vita; se la carriera finisce ci si ferma. */
    private void mandaViaTavoliSenzaPazienza(OsservatoreServizio osservatore) {
        Iterator<Tavolo> iteratore = tavoliInAttesa.iterator();
        while (iteratore.hasNext() && !isCarrieraFinita()) {
            Tavolo tavolo = iteratore.next();
            if (tavolo.haFinitoLaPazienza(minuto)) {
                iteratore.remove();
                tavolo.vaVia();
                chef.perdiReputazione();
                osservatore.tavoloAndatoVia(tavolo, chef.getReputazione());
            }
        }
    }

    /** Se nessun tavolo aspetta, l'orologio salta all'arrivo del prossimo; altrimenti lo chef cucina un piatto. */
    private void avanza(OsservatoreServizio osservatore) {
        if (tavoliInAttesa.isEmpty()) {
            minuto = tavoliInArrivo.peek().getMinutoArrivo();
        } else {
            cucinaProssimoPiatto(osservatore);
        }
    }

    /** Cucina il prossimo piatto del primo tavolo in attesa: l'orologio avanza dei minuti di preparazione. */
    private void cucinaProssimoPiatto(OsservatoreServizio osservatore) {
        Tavolo tavolo = tavoliInAttesa.peek();
        Piatto piatto = tavolo.prossimoPiatto();
        osservatore.piattoDaPreparare(tavolo, minuto);

        EsitoPiatto esitoPiatto = chef.cucina(piatto, scegliAzione(piatto));
        minuto += esitoPiatto.minutiPreparazione();
        tavolo.registraPiatto(esitoPiatto);
        osservatore.piattoPreparato(esitoPiatto);

        if (tavolo.isServito()) {
            tavoliInAttesa.remove();
            osservatore.tavoloServito(tavolo, minuto);
        }
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
