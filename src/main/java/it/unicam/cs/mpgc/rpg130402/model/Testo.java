package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Piccole regole sui testi mostrati al giocatore, scritte in un solo punto.
 * Sta nel model solo perché alcune stampe sono ancora qui: si sposterà nella ui insieme a loro.
 */
public final class Testo {

    private Testo() {
    }

    /** Il numero seguito dalla parola giusta: "1 stella", "3 stelle", "0 stelle". */
    public static String conNumero(int numero, String singolare, String plurale) {
        String parola = (numero == 1) ? singolare : plurale;
        return numero + " " + parola;
    }
}
