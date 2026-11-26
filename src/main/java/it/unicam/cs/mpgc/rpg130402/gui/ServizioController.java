package it.unicam.cs.mpgc.rpg130402.gui;

import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Partita;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Il controller della schermata del servizio (servizio.fxml). Il servizio è lo stesso della console
 * ({@link Partita#giocaServizio}), ma gira in un thread a parte: mentre aspetta il clic del giocatore,
 * ad aspettare è quel thread e non quello di JavaFX, così la finestra non si blocca.
 * Gli eventi del servizio arrivano con {@link OsservatoreServizioDaGui}, la scelta dell'azione con {@link SceltaAzioneDaGui}.
 * Tutti i metodi di questa classe girano sul thread JavaFX e ricevono solo testi già pronti.
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

        Thread threadDelServizio = new Thread(() -> osservatore.servizioFinito(partita.giocaServizio(scelta, osservatore)));
        threadDelServizio.setDaemon(true);
        threadDelServizio.start();
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

    /** L'ultima riga della cronaca; i pulsanti restano spenti. Il passaggio alla schermata di fine servizio arriva dopo. */
    void mostraFineServizio(String riga) {
        aggiungiAllaCronaca(riga);
        mostraTitoloPiatto("Nessun altro piatto");
        mostraPiatto("", "");
        azioni.setDisable(true);
    }
}
