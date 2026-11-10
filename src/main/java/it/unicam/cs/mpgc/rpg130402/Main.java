package it.unicam.cs.mpgc.rpg130402;

import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Comanda;
import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;
import it.unicam.cs.mpgc.rpg130402.model.EsitoPiatto;
import it.unicam.cs.mpgc.rpg130402.model.EsitoServizio;
import it.unicam.cs.mpgc.rpg130402.model.Menu;
import it.unicam.cs.mpgc.rpg130402.model.Piatto;
import it.unicam.cs.mpgc.rpg130402.model.Servizio;
import it.unicam.cs.mpgc.rpg130402.model.Testo;
import it.unicam.cs.mpgc.rpg130402.ui.SceltaAzioneDaConsole;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        Scanner tastiera = new Scanner(System.in);
        GestoreSalvataggio gestore = new GestoreSalvataggio();
        Chef giocatore = null;

        System.out.println("Vuoi caricare l'ultimo salvataggio? (scrivi 'si' o 'no')");
        String risposta = tastiera.nextLine();

        if (risposta.equals("si")) {
            giocatore = gestore.caricaPartita();
            if (giocatore == null) {
                System.out.println("Creo un nuovo Chef...");
                giocatore = creaNuovoChef(tastiera);
            }
        } else {
            System.out.println("Inizio una nuova carriera...");
            giocatore = creaNuovoChef(tastiera);
        }

        Equipaggiamento coltelloChef = new Equipaggiamento("Coltello in Ceramica", 10, 15);
        giocatore.equipaggia(coltelloChef);

        giocatore.mostraStatistiche();

        Menu menuDelGiorno = new Menu();
        Servizio servizio = new Servizio(giocatore, new SceltaAzioneDaConsole(tastiera));

        System.out.println("\n--- INIZIO SERVIZIO TAVOLI ---");

        Comanda tavolo1 = menuDelGiorno.generaComandaCasuale(1, 3, giocatore.getGrado().getStelleMassime());
        tavolo1.stampaScontrino();

        System.out.println("\n" + giocatore.getNome() + " inizia a preparare il Tavolo " + tavolo1.getNumeroTavolo() + "...");
        EsitoServizio esito = servizio.serviTavolo(tavolo1, Main::mostraEsitoPiatto);

        if (esito.chefLicenziato()) {
            System.out.println("\nGAME OVER! Hai bruciato troppi piatti, sei stato licenziato.");
        } else {
            mostraServizioCompletato(tavolo1, esito);

            System.out.println("\n--- FINE TURNO ---");
            System.out.println("Salvataggio progressi in corso...");

            gestore.salvaPartita(giocatore);
        }

        tastiera.close();
    }

    /** Chiede il nome finché il model non lo accetta: la regola sul nome resta nel costruttore di Chef. */
    private static Chef creaNuovoChef(Scanner tastiera) {
        while (true) {
            System.out.println("Come si chiama il tuo chef?");
            try {
                return new Chef(tastiera.nextLine().strip());
            } catch (IllegalArgumentException e) {
                System.out.println("Nome non valido: " + e.getMessage() + ".");
            }
        }
    }

    private static void mostraEsitoPiatto(EsitoPiatto esitoPiatto) {
        Piatto piatto = esitoPiatto.piatto();
        System.out.println("Tempo di preparazione: " + Testo.conNumero(esitoPiatto.minutiPreparazione(), "minuto", "minuti"));

        if (esitoPiatto.riuscito()) {
            System.out.println("SUCCESSO! Piatto riuscito.");
        } else {
            System.out.println("DISASTRO! Piatto bruciato. (Serve " + piatto.getMaestriaRichiesta() + " maestria, hai " + esitoPiatto.maestriaUsata() + ")");
            System.out.println("Attenzione! Reputazione scesa a: " + esitoPiatto.reputazioneDopo());
        }
    }

    private static void mostraServizioCompletato(Comanda tavolo, EsitoServizio esito) {
        System.out.println("\nServizio per il Tavolo " + tavolo.getNumeroTavolo() + " completato!");

        if (esito.piattiBruciati() == 0) {
            System.out.println("I clienti sono entusiasti! Hanno ricevuto tutti i piatti in modo perfetto.");
        } else {
            System.out.println("I clienti sono FURIOSI! Hanno ricevuto solo " + Testo.conNumero(esito.piattiRiusciti(), "piatto", "piatti") + " su " + esito.esitiPiatti().size() + " e lasciano una recensione pessima!");
        }
    }
}
