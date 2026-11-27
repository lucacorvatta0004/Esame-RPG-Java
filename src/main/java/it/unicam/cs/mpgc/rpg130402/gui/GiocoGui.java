package it.unicam.cs.mpgc.rpg130402.gui;

import it.unicam.cs.mpgc.rpg130402.model.Attrezzatura;
import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Cucina;
import it.unicam.cs.mpgc.rpg130402.model.EsitoServizio;
import it.unicam.cs.mpgc.rpg130402.model.Menu;
import it.unicam.cs.mpgc.rpg130402.model.Partita;
import it.unicam.cs.mpgc.rpg130402.persistence.Catalogo;
import it.unicam.cs.mpgc.rpg130402.persistence.CatalogoException;
import it.unicam.cs.mpgc.rpg130402.persistence.CatalogoJson;
import it.unicam.cs.mpgc.rpg130402.persistence.RepositoryPartita;
import it.unicam.cs.mpgc.rpg130402.persistence.RepositoryPartitaJson;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

/**
 * L'applicazione JavaFX: una sola finestra in cui si mostra una schermata alla volta.
 * Ogni schermata è un file FXML (la vista) con il suo controller. Qui si creano le classi concrete
 * del catalogo e del salvataggio, come fa {@link it.unicam.cs.mpgc.rpg130402.Main} per la console,
 * e si passa da una schermata all'altra: i controller non si conoscono tra loro.
 */
public class GiocoGui extends Application {
    private Stage finestra;
    private Cucina cucina;
    private RepositoryPartita repository;
    // la carriera scelta nella schermata iniziale: a ogni avvio se ne gioca una sola, un servizio dopo l'altro
    private Chef chef;
    private Partita partita;

    /**
     * Come in console: se i dati fissi del gioco mancano o sono rovinati si mostra il problema e si esce,
     * senza aprire la finestra.
     */
    @Override
    public void start(Stage finestra) {
        Catalogo catalogo;
        try {
            catalogo = new CatalogoJson();
        } catch (CatalogoException e) {
            System.err.println("ERRORE: il gioco non può partire, i suoi dati sono rovinati (" + e.getMessage() + ").");
            Platform.exit();
            return;
        }
        this.finestra = finestra;
        this.cucina = new Cucina(new Menu(catalogo.getPiatti()), new Attrezzatura(catalogo.getAttrezzi()));
        this.repository = new RepositoryPartitaJson(Path.of("salvataggio.json"), catalogo);

        finestra.setTitle("Battle of the Brigata");
        mostraInizio();
        finestra.show();
    }

    private void mostraInizio() {
        FXMLLoader caricatore = carica("inizio.fxml");
        InizioController controller = caricatore.getController();
        controller.inizializza(repository, this::iniziaCarriera);
        mostra(caricatore);
    }

    /** Lo chef scelto nella schermata iniziale gioca la sua partita con la cucina del catalogo. */
    private void iniziaCarriera(Chef chefScelto) {
        this.chef = chefScelto;
        this.partita = new Partita(chefScelto, cucina);
        mostraPrimaDelServizio();
    }

    /** Prima di ogni servizio: statistiche e attrezzi dello chef di adesso, poi la schermata del servizio. */
    private void mostraPrimaDelServizio() {
        FXMLLoader caricatore = carica("prima-del-servizio.fxml");
        PrimaDelServizioController controller = caricatore.getController();
        controller.inizializza(chef, partita);
        controller.setAvviaServizio(this::mostraServizio);
        mostra(caricatore);
    }

    /** Dopo la scelta degli attrezzi: la schermata del servizio, che fa partire il servizio della partita. */
    private void mostraServizio() {
        FXMLLoader caricatore = carica("servizio.fxml");
        ServizioController controller = caricatore.getController();
        controller.inizializza(chef, partita);
        controller.setFineServizio(esito -> controller.mostraContinua(preparaFineServizio(esito)));
        mostra(caricatore);
    }

    /**
     * Il servizio è appena finito e la carriera continua: come in console si salva subito. La schermata di fine
     * servizio si prepara adesso e si apre con "Continua": restituisce l'azione che la mostra.
     * Da lì "Un altro servizio" riporta alla schermata prima del servizio con la stessa partita.
     */
    private Runnable preparaFineServizio(EsitoServizio esito) {
        FXMLLoader caricatore = carica("fine-servizio.fxml");
        FineServizioController controller = caricatore.getController();
        controller.inizializza(esito, chef.getCarriera());
        controller.salva(repository, chef);
        controller.setUnAltroServizio(this::mostraPrimaDelServizio);
        return () -> mostra(caricatore);
    }

    /**
     * Legge il file FXML, che sta tra le risorse nella stessa cartella-package di questa classe,
     * e crea il suo controller. Un file mancante è un errore di chi ha scritto il gioco, non del giocatore.
     */
    private FXMLLoader carica(String fileFxml) {
        FXMLLoader caricatore = new FXMLLoader(getClass().getResource(fileFxml));
        try {
            caricatore.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile caricare la schermata " + fileFxml, e);
        }
        return caricatore;
    }

    /** Mette la schermata nella finestra, dopo che il controller l'ha riempita, così la finestra prende la misura giusta. */
    private void mostra(FXMLLoader caricatore) {
        Parent schermata = caricatore.getRoot();
        finestra.setScene(new Scene(schermata));
        finestra.sizeToScene();
    }
}
