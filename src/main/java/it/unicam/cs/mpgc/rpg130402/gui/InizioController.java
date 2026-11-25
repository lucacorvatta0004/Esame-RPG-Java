package it.unicam.cs.mpgc.rpg130402.gui;

import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.persistence.RepositoryPartita;
import it.unicam.cs.mpgc.rpg130402.persistence.SalvataggioException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Il controller della schermata iniziale (inizio.fxml): continuare la carriera salvata o iniziarne una nuova.
 * Legge il salvataggio dal repository e crea lo chef con il costruttore del model, che decide se il nome va bene.
 */
public class InizioController {
    @FXML
    private Label salvataggioNonValido;
    @FXML
    private TitledPane riquadroCarrieraSalvata;
    @FXML
    private Label datiCarrieraSalvata;
    @FXML
    private TextField campoNome;
    @FXML
    private Label erroreNome;
    @FXML
    private Label avvisoSostituzione;

    private Optional<Chef> chefSalvato;
    private Consumer<Chef> avviaCarriera;

    /**
     * Carica il salvataggio e mostra i riquadri giusti. Lo chef scelto (salvato o nuovo) passa ad avviaCarriera:
     * questa schermata non sa quale schermata viene dopo.
     */
    public void inizializza(RepositoryPartita repository, Consumer<Chef> avviaCarriera) {
        this.avviaCarriera = avviaCarriera;
        try {
            chefSalvato = repository.carica();
        } catch (SalvataggioException e) {
            chefSalvato = Optional.empty();
            salvataggioNonValido.setText("Salvataggio non valido (" + e.getMessage() + "): inizia una nuova carriera.");
            mostra(salvataggioNonValido);
        }
        chefSalvato.ifPresent(this::mostraCarrieraSalvata);
    }

    private void mostraCarrieraSalvata(Chef chef) {
        datiCarrieraSalvata.setText(chef.getNome() + ", " + chef.getGrado().getNome()
                + ", livello " + chef.getCarriera().livello());
        mostra(riquadroCarrieraSalvata);
        avvisoSostituzione.setText("La carriera salvata di " + chef.getNome() + " verrà sostituita.");
        mostra(avvisoSostituzione);
    }

    @FXML
    private void continuaLaCarriera() {
        avviaCarriera.accept(chefSalvato.orElseThrow());
    }

    /** Come in console: la regola sul nome resta nel costruttore di Chef, qui si mostra solo il suo messaggio. */
    @FXML
    private void iniziaNuovaCarriera() {
        Chef chefNuovo;
        try {
            chefNuovo = new Chef(campoNome.getText().strip());
        } catch (IllegalArgumentException e) {
            erroreNome.setText("Nome non valido: " + e.getMessage() + ".");
            mostra(erroreNome);
            return;
        }
        avviaCarriera.accept(chefNuovo);
    }

    /** I nodi nascosti nel file FXML non occupano spazio finché non si mostrano. */
    private static void mostra(Node nodo) {
        nodo.setManaged(true);
        nodo.setVisible(true);
    }
}
