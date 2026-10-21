package it.unicam.cs.mpgc.rpg130402;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        // Setup del giocatore
        Chef giocatore = new Chef("Luca", "Capo Partita");

        // Gli rido' il coltello cosi non va in Game Over al 100%
        Equipaggiamento coltelloChef = new Equipaggiamento("Coltello in Ceramica", 10, 15);
        giocatore.equipaggia(coltelloChef);

        giocatore.mostraStatistiche();

        // Preparo il menu della serata
        Menu menuDelGiorno = new Menu();

        System.out.println("\n--- INIZIO SERVIZIO ---");

        // Faccio estrarre 3 piatti per provare il servizio e vedere la reputazione
        Comanda ordine1 = menuDelGiorno.estraiComandaCasuale();
        ordine1.mostraDettagli();
        giocatore.cucina(ordine1);

        Comanda ordine2 = menuDelGiorno.estraiComandaCasuale();
        ordine2.mostraDettagli();
        giocatore.cucina(ordine2);

        Comanda ordine3 = menuDelGiorno.estraiComandaCasuale();
        ordine3.mostraDettagli();
        giocatore.cucina(ordine3);
    }
}