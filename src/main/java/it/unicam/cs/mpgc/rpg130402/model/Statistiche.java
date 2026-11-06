package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Le statistiche con cui lo chef cucina un piatto: maestria (almeno 0) e velocità (almeno 1).
 */
public record Statistiche(int maestria, int velocita) {
    private static final int MAESTRIA_MINIMA = 0;
    private static final int VELOCITA_MINIMA = 1;

    public Statistiche {
        Validazione.verificaAlmeno(maestria, MAESTRIA_MINIMA, "La maestria");
        Validazione.verificaAlmeno(velocita, VELOCITA_MINIMA, "La velocità");
    }

    /**
     * Restituisce una copia con i bonus aggiunti (anche negativi).
     * Il risultato non scende mai sotto i minimi: maestria 0, velocità 1.
     */
    public Statistiche conBonus(int bonusMaestria, int bonusVelocita) {
        return new Statistiche(
                Math.max(maestria + bonusMaestria, MAESTRIA_MINIMA),
                Math.max(velocita + bonusVelocita, VELOCITA_MINIMA));
    }
}
