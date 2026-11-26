package it.unicam.cs.mpgc.rpg130402.gui;

import it.unicam.cs.mpgc.rpg130402.model.Azione;
import it.unicam.cs.mpgc.rpg130402.model.Piatto;
import it.unicam.cs.mpgc.rpg130402.model.SceltaAzione;
import javafx.application.Platform;

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Il giocatore sceglie l'azione con un pulsante della schermata del servizio.
 * {@link #scegli} gira nel thread del servizio: prepara i testi, li manda alla finestra con Platform.runLater
 * e poi aspetta sulla coda il numero del pulsante premuto. Il clic arriva dal thread JavaFX con {@link #azioneScelta}.
 */
class SceltaAzioneDaGui implements SceltaAzione {
    private final ServizioController schermata;
    // un posto solo: c'è al massimo una scelta in attesa, quella del piatto di adesso
    private final BlockingQueue<Integer> numeroScelto = new ArrayBlockingQueue<>(1);

    SceltaAzioneDaGui(ServizioController schermata) {
        this.schermata = schermata;
    }

    @Override
    public Azione scegli(Piatto piatto, List<Azione> azioniDisponibili) {
        String nome = piatto.getNomePiatto();
        String dettagli = "★".repeat(piatto.getDifficoltaStelle()) + " · richiede " + piatto.getMaestriaRichiesta() + " maestria";
        List<ServizioController.PulsanteAzione> pulsanti = azioniDisponibili.stream()
                .map(SceltaAzioneDaGui::pulsante)
                .toList();
        Platform.runLater(() -> {
            schermata.mostraPiatto(nome, dettagli);
            schermata.mostraAzioni(pulsanti);
        });
        return azioniDisponibili.get(aspettaIlClic());
    }

    /** Chiamato dal thread JavaFX quando il giocatore preme un pulsante: non aspetta mai. */
    void azioneScelta(int numeroAzione) {
        numeroScelto.offer(numeroAzione);
    }

    /** Ferma il thread del servizio finché nella coda non arriva il numero del pulsante premuto. */
    private int aspettaIlClic() {
        try {
            return numeroScelto.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Il servizio è stato interrotto mentre aspettava una scelta", e);
        }
    }

    /** Il nome dell'azione, con gli usi rimasti tra parentesi se ne ha un numero limitato. */
    private static ServizioController.PulsanteAzione pulsante(Azione azione) {
        String testo = azione.getNome();
        OptionalInt usiRimasti = azione.getUsiRimasti();
        if (usiRimasti.isPresent()) {
            testo += " (" + usiRimasti.getAsInt() + ")";
        }
        return new ServizioController.PulsanteAzione(testo, azione.getDescrizione());
    }
}
