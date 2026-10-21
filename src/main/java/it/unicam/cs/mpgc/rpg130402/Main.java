package it.unicam.cs.mpgc.rpg130402;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        // 1. Setup del giocatore
        Chef giocatore = new Chef("Luca", "Capo Partita");
        Equipaggiamento coltelloChef = new Equipaggiamento("Coltello in Ceramica", 10, 15);
        giocatore.equipaggia(coltelloChef);

        giocatore.mostraStatistiche();

        // 2. Preparo il menu della serata (Fase 3)
        Menu menuDelGiorno = new Menu();

        System.out.println("\n--- INIZIO SERVIZIO ---");

        // 3. Invece di scrivere il piatto, me lo faccio dare a caso dal Menu!
        Comanda ordineTavolo1 = menuDelGiorno.estraiComandaCasuale();

        // 4. Mostro l'ordine ed evado la comanda
        ordineTavolo1.mostraDettagli();
        giocatore.cucina(ordineTavolo1);
    }
}