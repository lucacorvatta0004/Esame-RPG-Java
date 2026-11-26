package it.unicam.cs.mpgc.rpg130402.gui;

import it.unicam.cs.mpgc.rpg130402.model.Attrezzo;
import it.unicam.cs.mpgc.rpg130402.model.Carriera;
import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;
import it.unicam.cs.mpgc.rpg130402.model.Partita;
import it.unicam.cs.mpgc.rpg130402.model.Statistiche;
import it.unicam.cs.mpgc.rpg130402.model.Tavolo;
import it.unicam.cs.mpgc.rpg130402.model.TipoAttrezzo;
import it.unicam.cs.mpgc.rpg130402.ui.Testo;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Il controller della schermata prima del servizio (prima-del-servizio.fxml): mostra lo chef e gli attrezzi
 * sbloccati, e per ogni tipo con più attrezzi una tendina. Maestria e velocità le calcola il model
 * con gli attrezzi selezionati ({@link Chef#getStatisticheCon}); la scelta si applica con {@link Partita#scegliAttrezzi}.
 * Questa schermata non sa quale schermata viene dopo: alla fine chiama l'azione ricevuta con {@link #setAvviaServizio}.
 */
public class PrimaDelServizioController {
    @FXML
    private Label nome;
    @FXML
    private Label grado;
    @FXML
    private Label livello;
    @FXML
    private Label esperienza;
    @FXML
    private Label vite;
    @FXML
    private Label maestria;
    @FXML
    private Label velocita;
    @FXML
    private GridPane elencoAttrezzi;
    @FXML
    private Label tavoliDiOggi;

    private Chef chef;
    private Partita partita;
    private Runnable avviaServizio;
    private final List<Attrezzo> attrezziUnici = new ArrayList<>();
    private final Map<TipoAttrezzo, ComboBox<Attrezzo>> tendine = new EnumMap<>(TipoAttrezzo.class);

    public void inizializza(Chef chef, Partita partita) {
        this.chef = chef;
        this.partita = partita;
        mostraChef();
        partita.getAttrezziSbloccatiPerTipo().forEach(this::aggiungiRigaAttrezzi);
        aggiornaStatistiche();
        tavoliDiOggi.setText("Oggi arrivano " + Testo.conNumero(chef.getGrado().getTavoliPerServizio(), "tavolo", "tavoli")
                + ", uno ogni " + Tavolo.MINUTI_TRA_DUE_ARRIVI + " minuti. Ogni tavolo aspetta al massimo "
                + Tavolo.PAZIENZA_IN_MINUTI + " minuti.");
    }

    /** Cosa fare dopo che la scelta degli attrezzi è stata applicata: per esempio aprire la schermata del servizio. */
    public void setAvviaServizio(Runnable avviaServizio) {
        this.avviaServizio = avviaServizio;
    }

    private void mostraChef() {
        Carriera carriera = chef.getCarriera();
        nome.setText(chef.getNome());
        grado.setText(chef.getGrado().getNome());
        livello.setText(String.valueOf(carriera.livello()));
        esperienza.setText(carriera.esperienza() + " / " + carriera.esperienzaPerSalire());
        vite.setText("♥".repeat(chef.getReputazione()));
    }

    /** Una riga per tipo: con un solo attrezzo il suo nome, con più attrezzi una tendina. */
    private void aggiungiRigaAttrezzi(List<Attrezzo> attrezziDelTipo) {
        TipoAttrezzo tipo = attrezziDelTipo.getFirst().getTipo();
        Node scelta;
        if (attrezziDelTipo.size() == 1) {
            attrezziUnici.add(attrezziDelTipo.getFirst());
            scelta = new Label(descrivi(attrezziDelTipo.getFirst()));
        } else {
            ComboBox<Attrezzo> tendina = creaTendina(attrezziDelTipo);
            tendine.put(tipo, tendina);
            scelta = tendina;
        }
        elencoAttrezzi.addRow(elencoAttrezzi.getRowCount(), new Label(tipo.getNome() + ":"), scelta);
    }

    /** Parte selezionato l'attrezzo che lo chef usa già, altrimenti il primo; a ogni cambio le statistiche si aggiornano. */
    private ComboBox<Attrezzo> creaTendina(List<Attrezzo> attrezziDelTipo) {
        ComboBox<Attrezzo> tendina = new ComboBox<>();
        tendina.getItems().setAll(attrezziDelTipo);
        tendina.setConverter(new StringConverter<>() {
            @Override
            public String toString(Attrezzo attrezzo) {
                return attrezzo == null ? "" : descrivi(attrezzo);
            }

            @Override
            public Attrezzo fromString(String testo) {
                throw new UnsupportedOperationException("La tendina degli attrezzi non si può scrivere");
            }
        });
        tendina.setValue(attrezziDelTipo.stream()
                .filter(chef.getEquipaggiamento()::contiene)
                .findFirst()
                .orElse(attrezziDelTipo.getFirst()));
        tendina.valueProperty().addListener((valore, prima, dopo) -> aggiornaStatistiche());
        return tendina;
    }

    private void aggiornaStatistiche() {
        Statistiche statistiche = chef.getStatisticheCon(new Equipaggiamento(attrezziSelezionati()));
        maestria.setText(String.valueOf(statistiche.maestria()));
        velocita.setText(String.valueOf(statistiche.velocita()));
    }

    private List<Attrezzo> attrezziSelezionati() {
        return Stream.concat(attrezziUnici.stream(), tendine.values().stream().map(ComboBox::getValue))
                .toList();
    }

    /** Il nome con i bonus diversi da zero, per esempio "Padella in Rame (+5 maestria)". */
    private static String descrivi(Attrezzo attrezzo) {
        List<String> bonus = new ArrayList<>();
        if (attrezzo.getBonusMaestria() > 0) {
            bonus.add(Testo.conSegno(attrezzo.getBonusMaestria()) + " maestria");
        }
        if (attrezzo.getBonusVelocita() > 0) {
            bonus.add(Testo.conSegno(attrezzo.getBonusVelocita()) + " velocità");
        }
        return bonus.isEmpty() ? attrezzo.getNome() : attrezzo.getNome() + " (" + String.join(", ", bonus) + ")";
    }

    /**
     * Applica alla partita gli attrezzi scelti: per ogni tipo con più attrezzi la partita chiede quale usare
     * e la risposta è quello selezionato nella tendina. Poi parte il servizio.
     */
    @FXML
    private void iniziaIlServizio() {
        partita.scegliAttrezzi((attrezziDelTipo, equipaggiamentoAttuale) ->
                tendine.get(attrezziDelTipo.getFirst().getTipo()).getValue());
        avviaServizio.run();
    }
}
