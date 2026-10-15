package it.unicam.cs.mpgc.rpg130402;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        // creo un pg di test per vedere se la classe Chef funziona
        Chef giocatore = new Chef("Luca", "Capo Partita");

        System.out.println("Situazione iniziale:");
        giocatore.mostraStatistiche();

        // simulo di aver passato il primo livello
        giocatore.saliDiLivello();

        System.out.println("\nDopo il servizio:");
        giocatore.mostraStatistiche();
    }
}