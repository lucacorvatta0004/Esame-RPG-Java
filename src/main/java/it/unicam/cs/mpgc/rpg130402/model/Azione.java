package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Una mossa con cui lo chef prepara un piatto.
 * Per aggiungere una mossa basta una nuova classe che implementa questa interfaccia
 * e una riga nel {@link Grado} che la sblocca: chi usa le azioni non cambia.
 */
public interface Azione {

    /** Il nome della mossa, mostrato al giocatore. Mai vuoto. */
    String getNome();

    /**
     * Dice se la mossa si può usare adesso.
     * Le azioni base sono sempre disponibili; le speciali finché hanno usi in questo servizio.
     */
    default boolean isDisponibile() {
        return true;
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
