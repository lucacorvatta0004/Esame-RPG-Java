package it.unicam.cs.mpgc.rpg130402.model;

public class Piatto {
    private static final int LAVORO_PER_STELLA = 100;

    private final String nomePiatto;
    private final int difficoltaStelle;

    public Piatto(String nomePiatto, int difficoltaStelle) {
        Validazione.verificaTestoNonVuoto(nomePiatto, "Il nome del piatto");
        Validazione.verificaIntervallo(difficoltaStelle, 1, 5, "La difficoltà in stelle del piatto");
        this.nomePiatto = nomePiatto;
        this.difficoltaStelle = difficoltaStelle;
    }

    public String getNomePiatto() { return nomePiatto; }
    public int getDifficoltaStelle() { return difficoltaStelle; }

    /** Maestria minima che lo chef deve avere per non bruciare il piatto: 10 punti per stella. */
    public int getMaestriaRichiesta() {
        return difficoltaStelle * 10;
    }

    /**
     * Minuti per preparare il piatto alla velocità data (almeno 1): più stelle = più lavoro,
     * più velocità = meno tempo. Arrotondato per eccesso, quindi sempre almeno 1 minuto.
     */
    public int minutiDiPreparazione(int velocita) {
        Validazione.verificaAlmeno(velocita, 1, "La velocità");
        return Math.ceilDiv(difficoltaStelle * LAVORO_PER_STELLA, velocita);
    }

    public void mostraDettagli() {
        System.out.println("Piatto: " + nomePiatto + " | Difficoltà: " + Testo.conNumero(difficoltaStelle, "stella", "stelle") + " (Richiede " + getMaestriaRichiesta() + " maestria)");
    }
}