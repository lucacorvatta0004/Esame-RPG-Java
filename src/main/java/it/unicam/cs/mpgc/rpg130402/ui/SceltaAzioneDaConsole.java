package it.unicam.cs.mpgc.rpg130402.ui;

import it.unicam.cs.mpgc.rpg130402.model.Azione;
import it.unicam.cs.mpgc.rpg130402.model.Piatto;
import it.unicam.cs.mpgc.rpg130402.model.SceltaAzione;
import it.unicam.cs.mpgc.rpg130402.model.Testo;

import java.util.List;
import java.util.OptionalInt;
import java.util.Scanner;

/** Il giocatore sceglie l'azione scrivendone il numero nel terminale. */
public class SceltaAzioneDaConsole implements SceltaAzione {
    private final ElencoNumerato elenco;

    /** Usa lo stesso Scanner del resto del programma: due Scanner su System.in si rubano l'input. */
    public SceltaAzioneDaConsole(Scanner tastiera) {
        this.elenco = new ElencoNumerato(tastiera, "Numero dell'azione: ");
    }

    @Override
    public Azione scegli(Piatto piatto, List<Azione> azioniDisponibili) {
        System.out.println("-> Prossimo piatto: " + piatto.getNomePiatto() + " ("
                + Testo.conNumero(piatto.getDifficoltaStelle(), "stella", "stelle")
                + ", richiede " + piatto.getMaestriaRichiesta() + " maestria)");
        System.out.println("Scegli l'azione:");
        return elenco.scegli(azioniDisponibili, this::descrivi);
    }

    private String descrivi(Azione azione) {
        String descrizione = azione.getNome() + ": " + azione.getDescrizione();
        OptionalInt usiRimasti = azione.getUsiRimasti();
        if (usiRimasti.isPresent()) {
            descrizione += " (usi rimasti: " + usiRimasti.getAsInt() + ")";
        }
        return descrizione;
    }
}
