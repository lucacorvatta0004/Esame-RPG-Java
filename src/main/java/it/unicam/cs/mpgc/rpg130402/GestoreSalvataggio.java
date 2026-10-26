package it.unicam.cs.mpgc.rpg130402;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GestoreSalvataggio {
    private String nomeFile = "salvataggio.txt";

    public void salvaPartita(Chef giocatore) {
        try {
            FileWriter writer = new FileWriter(nomeFile);
            String datiDaSalvare = giocatore.getNome() + "," +
                    giocatore.getRuolo() + "," +
                    giocatore.getLivello() + "," +
                    giocatore.getVelocita() + "," +
                    giocatore.getMaestriaBase() + "," +
                    giocatore.getReputazione();
            writer.write(datiDaSalvare);
            writer.close();
            System.out.println("Salvataggio completato con successo!");
        } catch (IOException e) {
            System.out.println("ERRORE: impossibile salvare la partita.");
            e.printStackTrace();
        }
    }

    // metodo che legge il txt e mi restituisce lo Chef ricreato
    public Chef caricaPartita() {
        try {
            File fileDaLeggere = new File(nomeFile);
            Scanner lettore = new Scanner(fileDaLeggere);

            // leggo la prima e unica riga del file
            String rigaDati = lettore.nextLine();
            lettore.close();

            // splitto la stringa in un array di pezzi separati dalla virgola
            String[] pezzi = rigaDati.split(",");

            String nomeSalvato = pezzi[0];
            String ruoloSalvato = pezzi[1];
            // uso Integer.parseInt per trasformare il testo in numeri veri
            int livelloSalvato = Integer.parseInt(pezzi[2]);
            int velocitaSalvata = Integer.parseInt(pezzi[3]);
            int maestriaSalvata = Integer.parseInt(pezzi[4]);
            int reputazioneSalvata = Integer.parseInt(pezzi[5]);

            // ricreo lo chef e gli butto dentro le stats caricate
            Chef chefRecuperato = new Chef(nomeSalvato, ruoloSalvato);
            chefRecuperato.setStatisticheSalvate(livelloSalvato, velocitaSalvata, maestriaSalvata, reputazioneSalvata);

            System.out.println("Salvataggio trovato! Bentornato, " + nomeSalvato + ".");
            return chefRecuperato;

        } catch (FileNotFoundException e) {
            // se il file non esiste (es. prima partita) catturo l'errore senza far crashare tutto
            System.out.println("Nessun salvataggio trovato.");
            return null;
        }
    }
}