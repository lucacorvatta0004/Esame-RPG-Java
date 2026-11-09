package it.unicam.cs.mpgc.rpg130402.model;

import java.util.OptionalInt;

/**
 * Una mossa con cui lo chef prepara un piatto.
 * Per aggiungere una mossa basta una nuova classe che implementa questa interfaccia
 * e una riga nel {@link Grado} che la sblocca: chi usa le azioni non cambia.
 */
public interface Azione {

    /** Il nome della mossa, mostrato al giocatore. Mai vuoto. */
    String getNome();

    /** Cosa fa la mossa, in una riga, mostrato al giocatore accanto al nome. Mai vuoto. */
    String getDescrizione();

    /**
     * Dice se la mossa si può usare adesso.
     * Le azioni base sono sempre disponibili; le speciali finché hanno usi in questo servizio.
     */
    default boolean isDisponibile() {
        return true;
    }

    /** Gli usi rimasti in questo servizio; vuoto se la mossa ha usi illimitati. */
    default OptionalInt getUsiRimasti() {
        return OptionalInt.empty();
    }

    /**
     * Prepara il piatto partendo dalle statistiche effettive dello chef (base più attrezzo).
     * Restituisce, mai null, se il piatto è riuscito e le statistiche usate davvero
     * (con i bonus della mossa), da cui lo chef calcola il tempo di preparazione.
     * Non modifica né il piatto né lo chef: le conseguenze le decide lo chef.
     *
     * @throws IllegalStateException se la mossa non è disponibile
     */
    Preparazione esegui(Piatto piatto, Statistiche statistiche);
}
