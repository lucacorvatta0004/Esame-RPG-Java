package it.unicam.cs.mpgc.rpg130402.model;

// controlli comuni usati dai costruttori del model, scritti una volta sola
final class Validazione {

    private Validazione() {
    }

    static void verificaTestoNonVuoto(String testo, String descrizione) {
        if (testo == null || testo.isBlank()) {
            throw new IllegalArgumentException(descrizione + " non può essere vuoto");
        }
    }

    static void verificaAlmeno(int valore, int minimo, String descrizione) {
        if (valore < minimo) {
            throw new IllegalArgumentException(descrizione + " deve essere almeno " + minimo + " (valore ricevuto: " + valore + ")");
        }
    }

    static void verificaIntervallo(int valore, int minimo, int massimo, String descrizione) {
        if (valore < minimo || valore > massimo) {
            throw new IllegalArgumentException(descrizione + " deve essere da " + minimo + " a " + massimo + " (valore ricevuto: " + valore + ")");
        }
    }
}
