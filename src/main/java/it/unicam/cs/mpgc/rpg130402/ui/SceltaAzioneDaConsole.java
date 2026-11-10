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
    private final Scanner tastiera;

    /** Usa lo stesso Scanner del resto del programma: due Scanner su System.in si rubano l'input. */
    public SceltaAzioneDaConsole(Scanner tastiera) {
        this.tastiera = tastiera;
    }

    @Override
    public Azione scegli(Piatto piatto, List<Azione> azioniDisponibili) {
        System.out.println("\n-> Prossimo piatto: " + piatto.getNomePiatto() + " ("
                + Testo.conNumero(piatto.getDifficoltaStelle(), "stella", "stelle")
                + ", richiede " + piatto.getMaestriaRichiesta() + " maestria)");
        mostraMenu(azioniDisponibili);
        int numeroScelto = leggiNumeroDa1A(azioniDisponibili.size());
        return azioniDisponibili.get(numeroScelto - 1);
    }

    private void mostraMenu(List<Azione> azioni) {
        System.out.println("Scegli l'azione:");
        for (int i = 0; i < azioni.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + descrivi(azioni.get(i)));
        }
    }

    private String descrivi(Azione azione) {
        String descrizione = azione.getNome() + ": " + azione.getDescrizione();
        OptionalInt usiRimasti = azione.getUsiRimasti();
        if (usiRimasti.isPresent()) {
            descrizione += " (usi rimasti: " + usiRimasti.getAsInt() + ")";
        }
        return descrizione;
    }

    /** Chiede un numero finché non arriva un numero intero da 1 a massimo. */
    private int leggiNumeroDa1A(int massimo) {
        while (true) {
            System.out.print("Numero dell'azione: ");
            String riga = tastiera.nextLine().strip();
            if (isNumeroDa1A(riga, massimo)) {
                return Integer.parseInt(riga);
            }
            System.out.println("Scelta non valida: scrivi un numero da 1 a " + massimo + ".");
        }
    }

    private boolean isNumeroDa1A(String testo, int massimo) {
        try {
            int numero = Integer.parseInt(testo);
            return numero >= 1 && numero <= massimo;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
