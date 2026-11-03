package it.unicam.cs.mpgc.rpg130402;

import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Comanda;
import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;
import it.unicam.cs.mpgc.rpg130402.model.EsitoPiatto;
import it.unicam.cs.mpgc.rpg130402.model.EsitoServizio;
import it.unicam.cs.mpgc.rpg130402.model.Menu;
import it.unicam.cs.mpgc.rpg130402.model.Piatto;

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

        System.out.println("\n" + giocatore.getNome() + " inizia a preparare il Tavolo " + tavolo1.getNumeroTavolo() + "...");
        EsitoServizio esito = giocatore.cucina(tavolo1);
        mostraEsitiPiatti(esito, giocatore.getMaestriaTotale());

        if (esito.chefLicenziato()) {
            System.out.println("\nGAME OVER! Hai bruciato troppi piatti, sei stato licenziato.");
        } else {
            mostraServizioCompletato(tavolo1, esito);

            System.out.println("\n--- FINE TURNO ---");
            System.out.println("Salvataggio progressi in corso...");

            gestore.salvaPartita(giocatore);
        }

        // chiudo sempre lo scanner alla fine, ai prof piace molto vederlo
        tastiera.close();
    }

    private static void mostraEsitiPiatti(EsitoServizio esito, int maestriaEffettiva) {
        for (EsitoPiatto esitoPiatto : esito.esitiPiatti()) {
            mostraEsitoPiatto(esitoPiatto, maestriaEffettiva);
        }
    }

    private static void mostraEsitoPiatto(EsitoPiatto esitoPiatto, int maestriaEffettiva) {
        Piatto piatto = esitoPiatto.piatto();
        System.out.println("\n-> Preparo: " + piatto.getNomePiatto());

        if (esitoPiatto.riuscito()) {
            System.out.println("SUCCESSO! Piatto riuscito.");
        } else {
            System.out.println("DISASTRO! Piatto bruciato. (Serve " + piatto.getMaestriaRichiesta() + " maestria, hai " + maestriaEffettiva + ")");
            System.out.println("Attenzione! Reputazione scesa a: " + esitoPiatto.reputazioneDopo());
        }
    }

    private static void mostraServizioCompletato(Comanda tavolo, EsitoServizio esito) {
        System.out.println("\nServizio per il Tavolo " + tavolo.getNumeroTavolo() + " completato!");

        if (esito.piattiBruciati() == 0) {
            System.out.println("I clienti sono entusiasti! Hanno ricevuto tutti i piatti in modo perfetto.");
        } else {
            System.out.println("I clienti sono FURIOSI! Hanno ricevuto solo " + esito.piattiRiusciti() + " piatti su " + esito.esitiPiatti().size() + " e lasciano una recensione pessima!");
        }
    }
}
