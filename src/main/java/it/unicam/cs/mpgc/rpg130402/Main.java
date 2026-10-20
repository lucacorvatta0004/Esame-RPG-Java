package it.unicam.cs.mpgc.rpg130402;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        // 1. Creo lo Chef
        Chef giocatore = new Chef("Luca", "Capo Partita");

        // 2. Creo l'equipaggiamento e lo assegno allo Chef
        Equipaggiamento coltelloChef = new Equipaggiamento("Coltello in Ceramica", 10, 15);
        giocatore.equipaggia(coltelloChef);

        giocatore.mostraStatistiche();

        // 3. Arrivano i "Nemici" (Le comande)
        Comanda facile = new Comanda("Spaghetti al Pomodoro", 1);
        Comanda difficile = new Comanda("Filetto alla Wellington", 4);

        // 4. Inizia il servizio (Il Combattimento)
        facile.mostraDettagli();
        giocatore.cucina(facile); // Il giocatore vince facilmente

        difficile.mostraDettagli();
        giocatore.cucina(difficile); // Serve un livello più alto per vincerla!
    }
}