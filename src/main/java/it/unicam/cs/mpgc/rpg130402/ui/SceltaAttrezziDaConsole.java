package it.unicam.cs.mpgc.rpg130402.ui;

import it.unicam.cs.mpgc.rpg130402.model.Attrezzo;
import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;
import it.unicam.cs.mpgc.rpg130402.model.SceltaAttrezzi;

import java.util.List;
import java.util.Scanner;

/** Il giocatore sceglie, per ogni tipo, l'attrezzo del servizio scrivendone il numero nel terminale. */
public class SceltaAttrezziDaConsole implements SceltaAttrezzi {
    private final ElencoNumerato elenco;

    /** Usa lo stesso Scanner del resto del programma: due Scanner su System.in si rubano l'input. */
    public SceltaAttrezziDaConsole(Scanner tastiera) {
        this.elenco = new ElencoNumerato(tastiera, "Numero dell'attrezzo: ");
    }

    @Override
    public Attrezzo scegli(List<Attrezzo> attrezziDelTipo, Equipaggiamento equipaggiamentoAttuale) {
        String tipo = attrezziDelTipo.getFirst().getTipo().getNome();
        System.out.println("\nTipo " + tipo + ": quale usi in questo servizio?");
        return elenco.scegli(attrezziDelTipo, attrezzo -> descrivi(attrezzo, equipaggiamentoAttuale));
    }

    private String descrivi(Attrezzo attrezzo, Equipaggiamento equipaggiamentoAttuale) {
        String descrizione = attrezzo.getNome() + ": " + Testo.conSegno(attrezzo.getBonusVelocita()) + " velocità, "
                + Testo.conSegno(attrezzo.getBonusMaestria()) + " maestria";
        if (equipaggiamentoAttuale.contiene(attrezzo)) {
            descrizione += " (lo usi adesso)";
        }
        return descrizione;
    }
}
