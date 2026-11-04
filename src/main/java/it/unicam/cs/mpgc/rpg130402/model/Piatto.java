package it.unicam.cs.mpgc.rpg130402.model;

public class Piatto {
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

    public void mostraDettagli() {
        System.out.println("Piatto: " + nomePiatto + " | Difficolta': " + difficoltaStelle + " stelle (Richiede " + getMaestriaRichiesta() + " maestria)");
    }
}