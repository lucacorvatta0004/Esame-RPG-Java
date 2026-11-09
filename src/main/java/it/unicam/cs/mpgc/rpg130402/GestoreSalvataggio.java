package it.unicam.cs.mpgc.rpg130402;

import it.unicam.cs.mpgc.rpg130402.model.Chef;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class GestoreSalvataggio {
    private static final int NUMERO_CAMPI = 6;
    private final String nomeFile = "salvataggio.txt";

    public void salvaPartita(Chef giocatore) {
        String datiDaSalvare = giocatore.getNome() + "," +
                giocatore.getGrado().getNome() + "," +
                giocatore.getLivello() + "," +
                giocatore.getVelocita() + "," +
                giocatore.getMaestriaBase() + "," +
                giocatore.getReputazione();
        try (FileWriter writer = new FileWriter(nomeFile)) {
            writer.write(datiDaSalvare);
            System.out.println("Salvataggio completato con successo!");
        } catch (IOException e) {
            System.out.println("ERRORE: impossibile salvare la partita (" + e.getMessage() + ").");
        }
    }

    /**
     * Ricrea lo chef dalla riga salvata nel file.
     * Restituisce null se il file non esiste oppure se è rovinato o non valido
     * (riga mancante, numero di campi sbagliato, testo al posto di un numero,
     * valori rifiutati dalla validazione dello chef).
     */
    public Chef caricaPartita() {
        try (Scanner lettore = new Scanner(new File(nomeFile))) {
            String[] pezzi = lettore.nextLine().split(",");
            if (pezzi.length != NUMERO_CAMPI) {
                throw new IllegalArgumentException("servono " + NUMERO_CAMPI + " campi, trovati " + pezzi.length);
            }

            String nomeSalvato = pezzi[0];
            // pezzi[1] è il grado, scritto solo come informazione: non si legge perché si ricava dal livello
            int livelloSalvato = Integer.parseInt(pezzi[2]);
            int velocitaSalvata = Integer.parseInt(pezzi[3]);
            int maestriaSalvata = Integer.parseInt(pezzi[4]);
            int reputazioneSalvata = Integer.parseInt(pezzi[5]);

            Chef chefRecuperato = new Chef(nomeSalvato);
            chefRecuperato.setStatisticheSalvate(livelloSalvato, velocitaSalvata, maestriaSalvata, reputazioneSalvata);

            System.out.println("Salvataggio trovato! Bentornato, " + nomeSalvato + ".");
            return chefRecuperato;

        } catch (FileNotFoundException e) {
            System.out.println("Nessun salvataggio trovato.");
            return null;
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.out.println("Salvataggio non valido (" + e.getMessage() + ").");
            return null;
        }
    }
}
