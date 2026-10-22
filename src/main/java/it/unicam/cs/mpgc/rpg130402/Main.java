package it.unicam.cs.mpgc.rpg130402;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        Chef giocatore = new Chef("Luca", "Capo Partita");

        Equipaggiamento coltelloChef = new Equipaggiamento("Coltello in Ceramica", 10, 15);
        giocatore.equipaggia(coltelloChef);

        giocatore.mostraStatistiche();

        Menu menuDelGiorno = new Menu();

        System.out.println("\n--- INIZIO SERVIZIO TAVOLI ---");

        // Genero una comanda per il Tavolo 1 con 3 piatti casuali
        Comanda tavolo1 = menuDelGiorno.generaComandaCasuale(1, 3);
        tavolo1.stampaScontrino();

        // Lo Chef prova a cucinare l'intero tavolo
        giocatore.cucina(tavolo1);
    }
}