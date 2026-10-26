package it.unicam.cs.mpgc.rpg130402;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        // Preparo gli strumenti per leggere la tastiera e per il salvataggio
        Scanner tastiera = new Scanner(System.in);
        GestoreSalvataggio gestore = new GestoreSalvataggio();
        Chef giocatore = null;

        // Menu iniziale grezzo
        System.out.println("Vuoi caricare l'ultimo salvataggio? (scrivi 'si' o 'no')");
        String risposta = tastiera.nextLine();

        if (risposta.equals("si")) {
            giocatore = gestore.caricaPartita();

            // check di sicurezza: se il file txt non esiste, il metodo restituisce null
            if (giocatore == null) {
                System.out.println("Creo un nuovo Chef di default...");
                giocatore = new Chef("Luca", "Capo Partita");
            }
        } else {
            System.out.println("Inizio una nuova carriera...");
            giocatore = new Chef("Luca", "Capo Partita");
        }

        // Da qui in poi il gioco procede normalmente!
        Equipaggiamento coltelloChef = new Equipaggiamento("Coltello in Ceramica", 10, 15);
        giocatore.equipaggia(coltelloChef);

        giocatore.mostraStatistiche();

        Menu menuDelGiorno = new Menu();

        System.out.println("\n--- INIZIO SERVIZIO TAVOLI ---");

        Comanda tavolo1 = menuDelGiorno.generaComandaCasuale(1, 3);
        tavolo1.stampaScontrino();

        giocatore.cucina(tavolo1);

        System.out.println("\n--- FINE TURNO ---");
        System.out.println("Salvataggio progressi in corso...");

        gestore.salvaPartita(giocatore);

        // chiudo sempre lo scanner alla fine, ai prof piace molto vederlo
        tastiera.close();
    }
}