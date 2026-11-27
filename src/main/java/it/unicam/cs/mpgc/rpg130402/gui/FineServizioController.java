package it.unicam.cs.mpgc.rpg130402.gui;

import it.unicam.cs.mpgc.rpg130402.model.Carriera;
import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.EsitoServizio;
import it.unicam.cs.mpgc.rpg130402.persistence.RepositoryPartita;
import it.unicam.cs.mpgc.rpg130402.persistence.SalvataggioException;
import it.unicam.cs.mpgc.rpg130402.ui.Testo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Il controller della schermata di fine servizio (fine-servizio.fxml), quando la carriera continua:
 * come è andato il servizio, l'esperienza guadagnata e il salvataggio, che si fa qui dopo ogni servizio come in console.
 * Questa schermata non sa quale schermata viene dopo: "Un altro servizio" chiama l'azione ricevuta con {@link #setUnAltroServizio}.
 */
public class FineServizioController {
    private static final String VERDE = "#2e7d32";
    private static final String ROSSO = "#c62828";

    @FXML
    private Label tavoliUsciti;
    @FXML
    private Label tavoliServiti;
    @FXML
    private Label tavoliAndatiVia;
    @FXML
    private VBox riquadroEsperienza;
    @FXML
    private Label esperienzaGuadagnata;
    @FXML
    private Label livelloEdEsperienza;
    @FXML
    private Label salvataggio;

    private Runnable unAltroServizio;

    /**
     * Mostra l'esito del servizio e la carriera dopo il servizio. Il servizio finisce quando ogni tavolo
     * è uscito, servito o andato via: per questo i due numeri insieme fanno tutti i tavoli.
     */
    public void inizializza(EsitoServizio esito, Carriera carriera) {
        tavoliUsciti.setText("Tutti i " + esito.tavoli().size() + " tavoli sono usciti dal ristorante.");
        tavoliServiti.setText("Tavoli serviti: " + esito.tavoliServiti());
        tavoliAndatiVia.setText("Andati via senza mangiare: " + esito.tavoliAndatiVia() + vitePerse(esito.tavoliAndatiVia()));
        mostraEsperienza(esito.esperienzaGuadagnata());
        livelloEdEsperienza.setText("Livello " + carriera.livello() + " · esperienza "
                + carriera.esperienza() + " / " + carriera.esperienzaPerSalire());
    }

    /** Come in console: si salva lo chef dopo ogni servizio; se non si riesce, al posto di "Carriera salvata" c'è il motivo. */
    public void salva(RepositoryPartita repository, Chef chef) {
        try {
            repository.salva(chef);
            salvataggio.setText("Carriera salvata");
        } catch (SalvataggioException e) {
            salvataggio.setText("ERRORE: impossibile salvare la partita (" + e.getMessage() + ").");
            salvataggio.setTextFill(Color.web(ROSSO));
        }
    }

    /** Cosa fare con "Un altro servizio": per esempio tornare alla schermata prima del servizio. */
    public void setUnAltroServizio(Runnable unAltroServizio) {
        this.unAltroServizio = unAltroServizio;
    }

    /** Ogni tavolo andato via è costato una vita: "(-1 vita)", "(-2 vite)"; niente se non se n'è andato nessuno. */
    private static String vitePerse(int tavoliAndatiVia) {
        return tavoliAndatiVia == 0 ? "" : " (-" + Testo.conNumero(tavoliAndatiVia, "vita", "vite") + ")";
    }

    /** Il numero grande, verde se positivo o zero, rosso se l'esperienza del servizio è persa. */
    private void mostraEsperienza(int esperienza) {
        String colore = esperienza >= 0 ? VERDE : ROSSO;
        esperienzaGuadagnata.setText(Testo.conSegno(esperienza));
        esperienzaGuadagnata.setTextFill(Color.web(colore));
        riquadroEsperienza.setStyle("-fx-border-color: " + colore + "; -fx-border-width: 3; -fx-border-radius: 8;");
    }

    @FXML
    private void iniziaUnAltroServizio() {
        unAltroServizio.run();
    }

    @FXML
    private void esci() {
        Platform.exit();
    }
}
