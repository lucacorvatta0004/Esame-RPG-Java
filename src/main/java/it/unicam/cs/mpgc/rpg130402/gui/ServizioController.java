package it.unicam.cs.mpgc.rpg130402.gui;

import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.EsitoServizio;
import it.unicam.cs.mpgc.rpg130402.model.Partita;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * Il controller della schermata del servizio (servizio.fxml). Il servizio è lo stesso della console
 * ({@link Partita#giocaServizio}), ma gira in un thread a parte: mentre aspetta il clic del giocatore,
 * ad aspettare è quel thread e non quello di JavaFX, così la finestra non si blocca.
 * Gli eventi del servizio arrivano con {@link OsservatoreServizioDaGui}, la scelta dell'azione con {@link SceltaAzioneDaGui}.
 * Durante il servizio i metodi di questa classe girano sul thread JavaFX e ricevono solo testi già pronti.
 */
public class ServizioController {
    @FXML
    private Label minuto;
    @FXML
    private Label vite;
    @FXML
    private ListView<String> tavoli;
    @FXML
    private TitledPane riquadroPiatto;
    @FXML
    private Label nomePiatto;
    @FXML
    private Label dettagliPiatto;
    @FXML
    private VBox azioni;
    @FXML
    private ListView<String> cronaca;

    private SceltaAzioneDaGui sceltaAzione;
    private Consumer<EsitoServizio> fineServizio;

    /** Il testo di un pulsante e la descrizione dell'azione, mostrata nel tooltip. */
    record PulsanteAzione(String testo, String descrizione) {
    }

    /**
     * Mostra lo stato iniziale e fa partire il servizio nel suo thread. Il thread è daemon:
     * se il giocatore chiude la finestra mentre il servizio aspetta un clic, il programma finisce lo stesso.
     */
    public void inizializza(Chef chef, Partita partita) {
        mostraMinuto(OsservatoreServizioDaGui.testoMinuto(0));
        mostraVite(OsservatoreServizioDaGui.testoVite(chef.getReputazione()));
        SceltaAzioneDaGui scelta = new SceltaAzioneDaGui(this);
        OsservatoreServizioDaGui osservatore = new OsservatoreServizioDaGui(this, chef.getGrado().getTavoliPerServizio());
        this.sceltaAzione = scelta;

        Thread threadDelServizio = new Thread(() -> servizioFinito(partita.giocaServizio(scelta, osservatore), osservatore));
        threadDelServizio.setDaemon(true);
        threadDelServizio.start();
    }

    /**
     * Cosa fare appena il servizio finisce, se la carriera continua: per esempio salvare e preparare la schermata
     * di fine servizio, da aprire con il pulsante "Continua" (vedi {@link #mostraContinua}).
     * Questa schermata non sa quale schermata viene dopo.
     */
    public void setFineServizio(Consumer<EsitoServizio> fineServizio) {
        this.fineServizio = fineServizio;
    }

    /**
     * L'ultima cosa che fa il thread del servizio: la riga "Servizio finito" in cronaca. Se la carriera continua,
     * passa l'esito al thread JavaFX con Platform.runLater: da lì in poi il model lo usa solo lui, perché il servizio
     * è finito. Se la carriera è finita restano i pulsanti spenti (la schermata di fine carriera arriva dopo).
     */
    private void servizioFinito(EsitoServizio esito, OsservatoreServizioDaGui osservatore) {
        osservatore.servizioFinito(esito);
        if (!esito.statoCarriera().isFinita()) {
            Platform.runLater(() -> fineServizio.accept(esito));
        }
    }

    /** Al posto dei pulsanti delle mosse un solo pulsante "Continua", che esegue l'azione ricevuta (per esempio la schermata dopo). */
    public void mostraContinua(Runnable continua) {
        Button bottone = new Button("Continua");
        bottone.setOnAction(evento -> continua.run());
        azioni.getChildren().setAll(bottone);
        azioni.setDisable(false);
    }

    void mostraMinuto(String testo) {
        minuto.setText(testo);
    }

    void mostraVite(String testo) {
        vite.setText(testo);
    }

    void mostraTavoli(List<String> righe) {
        tavoli.getItems().setAll(righe);
    }

    void aggiungiAllaCronaca(String riga) {
        cronaca.getItems().add(riga);
        cronaca.scrollTo(cronaca.getItems().size() - 1);
    }

    void mostraTitoloPiatto(String titolo) {
        riquadroPiatto.setText(titolo);
    }

    void mostraPiatto(String nome, String dettagli) {
        nomePiatto.setText(nome);
        dettagliPiatto.setText(dettagli);
    }

    /** Un pulsante per ogni azione disponibile, con accanto una "i": tutti e due mostrano la descrizione nel tooltip. */
    void mostraAzioni(List<PulsanteAzione> pulsanti) {
        azioni.getChildren().clear();
        for (int numero = 0; numero < pulsanti.size(); numero++) {
            int numeroAzione = numero;
            PulsanteAzione pulsante = pulsanti.get(numero);
            Button bottone = new Button(pulsante.testo());
            bottone.setTooltip(new Tooltip(pulsante.descrizione()));
            bottone.setOnAction(evento -> scegli(numeroAzione));
            Label informazioni = new Label("(i)");
            informazioni.setTooltip(new Tooltip(pulsante.descrizione()));
            azioni.getChildren().add(new HBox(5, bottone, informazioni));
        }
        azioni.setDisable(false);
    }

    /** Dopo un clic i pulsanti si spengono: un secondo clic non sceglie un'altra azione per lo stesso piatto. */
    private void scegli(int numeroAzione) {
        azioni.setDisable(true);
        sceltaAzione.azioneScelta(numeroAzione);
    }

    /** L'ultima riga della cronaca; i pulsanti delle mosse si spengono. */
    void mostraFineServizio(String riga) {
        aggiungiAllaCronaca(riga);
        mostraTitoloPiatto("Nessun altro piatto");
        mostraPiatto("", "");
        azioni.setDisable(true);
    }
}
