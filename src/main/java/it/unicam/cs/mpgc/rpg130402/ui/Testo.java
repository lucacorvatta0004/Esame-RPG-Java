package it.unicam.cs.mpgc.rpg130402.ui;

/**
 * Piccole regole sui testi mostrati al giocatore, scritte in un solo punto.
 * Sta nella ui perché riguarda come si scrivono le cose sullo schermo, non le regole del gioco.
 */
public final class Testo {

    private Testo() {
    }

    /** Il numero seguito dalla parola giusta: "1 stella", "3 stelle", "0 stelle". */
    public static String conNumero(int numero, String singolare, String plurale) {
        String parola = (numero == 1) ? singolare : plurale;
        return numero + " " + parola;
    }

    /** Il numero con il segno davanti anche se positivo: "+20", "-15". */
    public static String conSegno(int numero) {
        return String.format("%+d", numero);
    }
}
