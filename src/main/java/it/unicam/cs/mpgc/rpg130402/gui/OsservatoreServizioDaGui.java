package it.unicam.cs.mpgc.rpg130402.gui;

import it.unicam.cs.mpgc.rpg130402.model.EsitoPiatto;
import it.unicam.cs.mpgc.rpg130402.model.EsitoServizio;
import it.unicam.cs.mpgc.rpg130402.model.OsservatoreServizio;
import it.unicam.cs.mpgc.rpg130402.model.Tavolo;
import it.unicam.cs.mpgc.rpg130402.ui.MessaggiDelProgresso;
import it.unicam.cs.mpgc.rpg130402.ui.Testo;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Racconta il servizio nella schermata del servizio, mentre succede. Per ogni servizio se ne crea uno nuovo.
 * I metodi girano nel thread del servizio: leggono il model e preparano subito i testi, poi li passano
 * alla finestra con Platform.runLater. Così il thread JavaFX riceve solo testi già pronti e non legge mai
 * il model mentre il servizio lo sta cambiando.
 */
class OsservatoreServizioDaGui implements OsservatoreServizio {
    private final ServizioController schermata;
    private final int numeroTavoli;
    private final List<Tavolo> tavoliArrivati = new ArrayList<>();
    private int minuto;

    /** Il numero di tavoli serve a mostrare "in arrivo" quelli che non si sono ancora seduti. */
    OsservatoreServizioDaGui(ServizioController schermata, int numeroTavoli) {
        this.schermata = schermata;
        this.numeroTavoli = numeroTavoli;
    }

    static String testoMinuto(int minuto) {
        return "Minuto " + minuto;
    }

    static String testoVite(int reputazione) {
        return "♥".repeat(reputazione);
    }

    @Override
    public void tavoloArrivato(Tavolo tavolo) {
        // se nessuno aspettava, l'orologio del servizio è saltato all'arrivo; altrimenti il tavolo è arrivato mentre lo chef cucinava
        minuto = Math.max(minuto, tavolo.getMinutoArrivo());
        tavoliArrivati.add(tavolo);
        String testoMinuto = testoMinuto(minuto);
        Platform.runLater(() -> schermata.mostraMinuto(testoMinuto));
        racconta("Il tavolo " + tavolo.getNumero() + " si è seduto al minuto " + tavolo.getMinutoArrivo()
                + " e aspetta fino al minuto " + tavolo.getMinutoLimite() + ".");
        aggiornaTavoli();
    }

    @Override
    public void piattoDaPreparare(Tavolo tavolo, int minuto) {
        this.minuto = minuto;
        String titolo = "Prossimo piatto · tavolo " + tavolo.getNumero();
        String testoMinuto = testoMinuto(minuto);
        Platform.runLater(() -> {
            schermata.mostraTitoloPiatto(titolo);
            schermata.mostraMinuto(testoMinuto);
        });
    }

    @Override
    public void piattoPreparato(EsitoPiatto esitoPiatto) {
        minuto += esitoPiatto.minutiPreparazione();
        String tempoEdEsperienza = Testo.conNumero(esitoPiatto.minutiPreparazione(), "minuto", "minuti") + ", "
                + Testo.conSegno(esitoPiatto.progresso().esperienzaGuadagnata()) + " esperienza";
        String nome = esitoPiatto.piatto().getNomePiatto();
        if (esitoPiatto.riuscito()) {
            racconta("Piatto riuscito: " + nome + ", " + tempoEdEsperienza + ".");
        } else {
            racconta("Piatto bruciato: " + nome + " (" + esitoPiatto.motivo() + "), " + tempoEdEsperienza + ", -1 vita.");
        }
        MessaggiDelProgresso.di(esitoPiatto).forEach(this::racconta);
        String testoMinuto = testoMinuto(minuto);
        String testoVite = testoVite(esitoPiatto.reputazioneDopo());
        Platform.runLater(() -> {
            schermata.mostraMinuto(testoMinuto);
            schermata.mostraVite(testoVite);
        });
        aggiornaTavoli();
    }

    @Override
    public void tavoloServito(Tavolo tavolo, int minuto) {
        this.minuto = minuto;
        racconta("Tavolo " + tavolo.getNumero() + " servito: "
                + Testo.conNumero(tavolo.piattiRiusciti(), "piatto riuscito", "piatti riusciti")
                + " su " + tavolo.getNumeroPiatti() + ".");
        aggiornaTavoli();
    }

    @Override
    public void tavoloAndatoVia(Tavolo tavolo, int reputazioneDopo) {
        racconta("Il tavolo " + tavolo.getNumero() + " ha aspettato troppo ed è andato via senza "
                + Testo.conNumero(tavolo.piattiMancanti(), "piatto", "piatti") + ": -1 vita.");
        String testoVite = testoVite(reputazioneDopo);
        Platform.runLater(() -> schermata.mostraVite(testoVite));
        aggiornaTavoli();
    }

    /** L'ultimo evento, quando il servizio è finito: lo chiama il thread del servizio con l'esito appena ricevuto. */
    void servizioFinito(EsitoServizio esito) {
        String riga = rigaConMinuto("Servizio finito: "
                + Testo.conNumero(esito.tavoliServiti(), "tavolo servito", "tavoli serviti") + " su " + esito.tavoli().size()
                + ", " + Testo.conNumero(esito.tavoliAndatiVia(), "andato via", "andati via") + ".");
        Platform.runLater(() -> schermata.mostraFineServizio(riga));
    }

    private void racconta(String evento) {
        String riga = rigaConMinuto(evento);
        Platform.runLater(() -> schermata.aggiungiAllaCronaca(riga));
    }

    private String rigaConMinuto(String evento) {
        return "[Minuto " + minuto + "] " + evento;
    }

    /** Una riga per tavolo, nell'ordine dei numeri: quelli non ancora seduti sono "in arrivo". */
    private void aggiornaTavoli() {
        List<String> righe = IntStream.rangeClosed(1, numeroTavoli)
                .mapToObj(this::rigaDelTavolo)
                .toList();
        Platform.runLater(() -> schermata.mostraTavoli(righe));
    }

    private String rigaDelTavolo(int numero) {
        return tavoliArrivati.stream()
                .filter(tavolo -> tavolo.getNumero() == numero)
                .findFirst()
                .map(OsservatoreServizioDaGui::descrivi)
                .orElse("Tavolo " + numero + " · in arrivo");
    }

    private static String descrivi(Tavolo tavolo) {
        String riga = "Tavolo " + tavolo.getNumero() + " · " + tavolo.getPiattiPreparati() + "/" + tavolo.getNumeroPiatti() + " piatti · ";
        if (tavolo.isServito()) {
            return riga + "servito";
        }
        if (tavolo.isAndatoVia()) {
            return riga + "andato via";
        }
        return riga + "entro il minuto " + tavolo.getMinutoLimite();
    }
}
