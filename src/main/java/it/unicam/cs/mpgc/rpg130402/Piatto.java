package it.unicam.cs.mpgc.rpg130402;

public class Piatto {
    private String nomePiatto;
    private int difficoltaStelle;

    public Piatto(String nomePiatto, int difficoltaStelle) {
        this.nomePiatto = nomePiatto;
        this.difficoltaStelle = difficoltaStelle;
    }

    public String getNomePiatto() { return nomePiatto; }
    public int getDifficoltaStelle() { return difficoltaStelle; }

    // traduco le stelle in un punteggio numerico vero per il check con la maestria
    public int getMaestriaRichiesta() {
        return difficoltaStelle * 10;
    }

    // debug per far capire al giocatore i requisiti
    public void mostraDettagli() {
        System.out.println("Piatto: " + nomePiatto + " | Difficolta': " + difficoltaStelle + " stelle (Richiede " + getMaestriaRichiesta() + " maestria)");
    }
}