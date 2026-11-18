package it.unicam.cs.mpgc.rpg130402;

import it.unicam.cs.mpgc.rpg130402.model.Attrezzatura;
import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Cucina;
import it.unicam.cs.mpgc.rpg130402.model.EsitoServizio;
import it.unicam.cs.mpgc.rpg130402.model.Menu;
import it.unicam.cs.mpgc.rpg130402.model.Partita;
import it.unicam.cs.mpgc.rpg130402.model.SceltaAttrezzi;
import it.unicam.cs.mpgc.rpg130402.model.SceltaAzione;
import it.unicam.cs.mpgc.rpg130402.model.Tavolo;
import it.unicam.cs.mpgc.rpg130402.model.Testo;
import it.unicam.cs.mpgc.rpg130402.persistence.Catalogo;
import it.unicam.cs.mpgc.rpg130402.persistence.CatalogoException;
import it.unicam.cs.mpgc.rpg130402.persistence.CatalogoJson;
import it.unicam.cs.mpgc.rpg130402.persistence.RepositoryPartita;
import it.unicam.cs.mpgc.rpg130402.persistence.RepositoryPartitaJson;
import it.unicam.cs.mpgc.rpg130402.persistence.SalvataggioException;
import it.unicam.cs.mpgc.rpg130402.ui.OsservatoreServizioDaConsole;
import it.unicam.cs.mpgc.rpg130402.ui.SceltaAttrezziDaConsole;
import it.unicam.cs.mpgc.rpg130402.ui.SceltaAzioneDaConsole;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    /**
     * Prima di tutto legge i dati fissi del gioco. Se mancano o sono rovinati è un errore di chi ha preparato
     * il gioco, non del giocatore: si mostra il problema e si esce con codice 1, senza iniziare la partita.
     */
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        Catalogo catalogo;
        try {
            // l'unico punto che sceglie di leggere i dati fissi dai file JSON tra le risorse
            catalogo = new CatalogoJson();
        } catch (CatalogoException e) {
            System.err.println("ERRORE: il gioco non può partire, i suoi dati sono rovinati (" + e.getMessage() + ").");
            System.exit(1);
            return; // il compilatore non sa che System.exit non torna: senza return, sotto, catalogo risulterebbe non assegnato
        }

        giocaPartita(catalogo);
    }

    /**
     * Gioca un servizio dopo l'altro: prima di ogni servizio si scelgono gli attrezzi, poi si mostrano
     * le statistiche con quegli attrezzi. Dopo ogni servizio salva e chiede se continuare;
     * al game over cancella il salvataggio e la partita finisce.
     */
    private static void giocaPartita(Catalogo catalogo) {
        Scanner tastiera = new Scanner(System.in);
        // l'unico punto che sceglie il salvataggio in JSON: il file sta nella cartella da cui si avvia il gioco
        RepositoryPartita repository = new RepositoryPartitaJson(Path.of("salvataggio.json"), catalogo);
        Chef giocatore = preparaChef(tastiera, repository);
        Partita partita = new Partita(giocatore, creaCucina(catalogo));
        SceltaAttrezzi sceltaAttrezzi = new SceltaAttrezziDaConsole(tastiera);
        SceltaAzione sceltaAzione = new SceltaAzioneDaConsole(tastiera);

        boolean unAltroServizio = true;
        while (unAltroServizio) {
            partita.scegliAttrezzi(sceltaAttrezzi);
            annunciaServizio(giocatore);
            EsitoServizio esito = partita.giocaServizio(sceltaAzione, new OsservatoreServizioDaConsole());
            if (esito.chefLicenziato()) {
                System.out.println("\nGAME OVER! Hai perso tutta la reputazione, sei stato licenziato.");
                chiudiCarriera(repository);
                unAltroServizio = false;
            } else {
                mostraFineServizio(esito);
                salvaChef(repository, giocatore);
                unAltroServizio = vuoleUnAltroServizio(tastiera, giocatore);
            }
        }

        tastiera.close();
    }

    /** Il menu e l'attrezzatura della cucina, presi dal catalogo. */
    private static Cucina creaCucina(Catalogo catalogo) {
        return new Cucina(new Menu(catalogo.getPiatti()), new Attrezzatura(catalogo.getAttrezzi()));
    }

    private static void annunciaServizio(Chef giocatore) {
        giocatore.mostraStatistiche();
        System.out.println("\n=== INIZIO SERVIZIO ===");
        System.out.println("Oggi arrivano " + Testo.conNumero(giocatore.getGrado().getTavoliPerServizio(), "tavolo", "tavoli")
                + ", uno ogni " + Tavolo.MINUTI_TRA_DUE_ARRIVI + " minuti. Ogni tavolo aspetta al massimo "
                + Tavolo.PAZIENZA_IN_MINUTI + " minuti da quando si siede.");
    }

    /** Carica lo chef salvato oppure ne crea uno nuovo: restituisce sempre uno chef. */
    private static Chef preparaChef(Scanner tastiera, RepositoryPartita repository) {
        System.out.println("Vuoi caricare l'ultimo salvataggio? (scrivi 'si' o 'no')");

        if (rispondeSi(tastiera)) {
            Optional<Chef> chefSalvato = caricaChef(repository);
            if (chefSalvato.isPresent()) {
                return chefSalvato.get();
            }
            System.out.println("Creo un nuovo Chef...");
        } else {
            avvisaSeSostituisciCarriera(repository);
            System.out.println("Inizio una nuova carriera...");
        }
        return creaNuovoChef(tastiera);
    }

    private static Optional<Chef> caricaChef(RepositoryPartita repository) {
        try {
            Optional<Chef> chefSalvato = repository.carica();
            chefSalvato.ifPresentOrElse(
                    chef -> System.out.println("Salvataggio trovato! Bentornato, " + chef.getNome() + "."),
                    () -> System.out.println("Nessun salvataggio trovato."));
            return chefSalvato;
        } catch (SalvataggioException e) {
            System.out.println("Salvataggio non valido (" + e.getMessage() + ").");
            return Optional.empty();
        }
    }

    /** C'è un solo salvataggio: una nuova carriera prende il posto di quella salvata. */
    private static void avvisaSeSostituisciCarriera(RepositoryPartita repository) {
        try {
            repository.carica().ifPresent(chefSalvato -> System.out.println("Attenzione: la carriera salvata di "
                    + chefSalvato.getNome() + " verrà sostituita da quella nuova."));
        } catch (SalvataggioException _) {
            // un salvataggio rovinato non è una carriera da perdere: non c'è niente da avvisare
        }
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

    private static void mostraFineServizio(EsitoServizio esito) {
        System.out.println("\n=== FINE SERVIZIO ===");
        System.out.println("Tavoli serviti: " + esito.tavoliServiti() + " su " + esito.tavoli().size()
                + ", tavoli andati via: " + esito.tavoliAndatiVia() + ".");
        System.out.println("Esperienza del servizio: " + Testo.conSegno(esito.esperienzaGuadagnata()) + ".");
    }

    private static boolean vuoleUnAltroServizio(Scanner tastiera, Chef giocatore) {
        System.out.println("\nVuoi fare un altro servizio? (scrivi 'si' o 'no')");
        if (rispondeSi(tastiera)) {
            return true;
        }
        System.out.println("A presto, " + giocatore.getNome() + "! Per riprendere la carriera, la prossima volta rispondi 'si' alla prima domanda.");
        return false;
    }

    /** Solo "si" vale come sì (anche con spazi prima o dopo); qualunque altra risposta vale come no. */
    private static boolean rispondeSi(Scanner tastiera) {
        return tastiera.nextLine().strip().equals("si");
    }

    /** Reputazione 0 = carriera finita: il salvataggio si cancella e la prossima partita riparte da capo. */
    private static void chiudiCarriera(RepositoryPartita repository) {
        try {
            repository.elimina();
            System.out.println("Carriera finita: il salvataggio non c'è più, la prossima volta si ricomincia da capo.");
        } catch (SalvataggioException e) {
            System.out.println("ERRORE: impossibile cancellare il salvataggio (" + e.getMessage() + ").");
        }
    }

    private static void salvaChef(RepositoryPartita repository, Chef giocatore) {
        System.out.println("Salvataggio progressi in corso...");
        try {
            repository.salva(giocatore);
            System.out.println("Salvataggio completato con successo!");
        } catch (SalvataggioException e) {
            System.out.println("ERRORE: impossibile salvare la partita (" + e.getMessage() + ").");
        }
    }
}
