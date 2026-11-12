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
import it.unicam.cs.mpgc.rpg130402.persistence.Catalogo;
import it.unicam.cs.mpgc.rpg130402.persistence.CatalogoException;
import it.unicam.cs.mpgc.rpg130402.persistence.CatalogoJson;
import it.unicam.cs.mpgc.rpg130402.persistence.RepositoryPartita;
import it.unicam.cs.mpgc.rpg130402.persistence.RepositoryPartitaJson;
import it.unicam.cs.mpgc.rpg130402.persistence.SalvataggioException;
import it.unicam.cs.mpgc.rpg130402.ui.SceltaAzioneDaConsole;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    // l'attrezzo con cui parte ogni chef: qui c'è solo il nome, i bonus sono nel catalogo (attrezzi.json)
    private static final String ATTREZZO_INIZIALE = "Coltello in Ceramica";

    /**
     * Prima di tutto legge i dati fissi del gioco. Se mancano o sono rovinati è un errore di chi ha preparato
     * il gioco, non del giocatore: si mostra il problema e si esce con codice 1, senza iniziare la partita.
     */
    public static void main(String[] args) {
        System.out.println("=== BATTLE OF THE BRIGATA ===\n");

        Catalogo catalogo;
        Equipaggiamento attrezzoIniziale;
        try {
            // l'unico punto che sceglie di leggere i dati fissi dai file JSON tra le risorse
            catalogo = new CatalogoJson();
            attrezzoIniziale = catalogo.getAttrezzo(ATTREZZO_INIZIALE);
        } catch (CatalogoException e) {
            System.err.println("ERRORE: il gioco non può partire, i suoi dati sono rovinati (" + e.getMessage() + ").");
            System.exit(1);
            return; // il compilatore non sa che System.exit non torna: senza return, sotto, catalogo risulterebbe non assegnato
        }

        giocaPartita(new Menu(catalogo.getPiatti()), attrezzoIniziale);
    }

    private static void giocaPartita(Menu menuDelGiorno, Equipaggiamento attrezzoIniziale) {
        Scanner tastiera = new Scanner(System.in);
        // l'unico punto che sceglie il salvataggio in JSON: il file sta nella cartella da cui si avvia il gioco
        RepositoryPartita repository = new RepositoryPartitaJson(Path.of("salvataggio.json"));
        Chef giocatore = preparaChef(tastiera, repository);

        giocatore.equipaggia(attrezzoIniziale);

        giocatore.mostraStatistiche();

        Servizio servizio = new Servizio(giocatore, new SceltaAzioneDaConsole(tastiera));

        System.out.println("\n--- INIZIO SERVIZIO TAVOLI ---");

        Comanda tavolo1 = menuDelGiorno.generaComandaCasuale(1, 3, giocatore.getGrado().getStelleMassime());
        tavolo1.stampaScontrino();

        System.out.println("\n" + giocatore.getNome() + " inizia a preparare il Tavolo " + tavolo1.getNumeroTavolo() + "...");
        EsitoServizio esito = servizio.serviTavolo(tavolo1, Main::mostraEsitoPiatto);

        if (esito.chefLicenziato()) {
            System.out.println("\nGAME OVER! Hai bruciato troppi piatti, sei stato licenziato.");
            chiudiCarriera(repository);
        } else {
            mostraServizioCompletato(tavolo1, esito);

            System.out.println("\n--- FINE TURNO ---");
            System.out.println("Salvataggio progressi in corso...");
            salvaChef(repository, giocatore);
        }

        tastiera.close();
    }

    /** Carica lo chef salvato oppure ne crea uno nuovo: restituisce sempre uno chef. */
    private static Chef preparaChef(Scanner tastiera, RepositoryPartita repository) {
        System.out.println("Vuoi caricare l'ultimo salvataggio? (scrivi 'si' o 'no')");

        if (tastiera.nextLine().equals("si")) {
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
        try {
            repository.salva(giocatore);
            System.out.println("Salvataggio completato con successo!");
        } catch (SalvataggioException e) {
            System.out.println("ERRORE: impossibile salvare la partita (" + e.getMessage() + ").");
        }
    }
}
