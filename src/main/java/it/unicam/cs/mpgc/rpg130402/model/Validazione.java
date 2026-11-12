package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;
import java.util.Objects;

// controlli comuni sui parametri del model, scritti in un solo punto
final class Validazione {

    private Validazione() {
    }

    static void verificaNonNullo(Object oggetto, String descrizione) {
        if (oggetto == null) {
            throw new IllegalArgumentException(descrizione + " non può essere null");
        }
    }

    static void verificaTestoNonVuoto(String testo, String descrizione) {
        if (testo == null || testo.isBlank()) {
            throw new IllegalArgumentException(descrizione + " non può essere vuoto");
        }
    }

    static void verificaListaNonVuota(List<?> lista, String descrizione) {
        verificaNonNullo(lista, descrizione);
        if (lista.isEmpty()) {
            throw new IllegalArgumentException(descrizione + " non può essere vuota");
        }
        if (lista.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(descrizione + " non può contenere elementi null");
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
