package it.unicam.cs.mpgc.rpg130402.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Un elenco numerato da 1 nel terminale, da cui il giocatore sceglie una voce scrivendone il numero.
 * Lo usano tutte le scelte da console, così il modo di chiedere un numero è scritto una volta sola.
 */
final class ElencoNumerato {
    private final Scanner tastiera;
    private final String richiesta;

    /** La richiesta è il testo mostrato prima di leggere il numero, per esempio "Numero dell'azione: ". */
    ElencoNumerato(Scanner tastiera, String richiesta) {
        this.tastiera = tastiera;
        this.richiesta = richiesta;
    }

    /**
     * Mostra le voci (almeno una), ognuna con la sua descrizione, e chiede il numero finché non è valido.
     * Restituisce la voce scelta.
     */
    <T> T scegli(List<T> voci, Function<T, String> descrizione) {
        for (int i = 0; i < voci.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + descrizione.apply(voci.get(i)));
        }
        return voci.get(leggiNumeroDa1A(voci.size()) - 1);
    }

    /** Chiede un numero finché non arriva un numero intero da 1 a massimo. */
    private int leggiNumeroDa1A(int massimo) {
        while (true) {
            System.out.print(richiesta);
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
