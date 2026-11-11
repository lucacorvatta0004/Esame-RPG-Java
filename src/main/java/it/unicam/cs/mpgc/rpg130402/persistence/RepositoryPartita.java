package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Chef;

import java.util.Optional;

/**
 * Conserva la partita del giocatore tra un avvio e l'altro.
 * Il gioco usa solo questa interfaccia: dove e in che formato si salva lo decide l'implementazione.
 * C'è un solo salvataggio: salvare sostituisce quello di prima.
 */
public interface RepositoryPartita {

    /**
     * Salva lo chef al posto del salvataggio precedente.
     *
     * @throws SalvataggioException se non si riesce a scrivere il salvataggio
     */
    void salva(Chef chef) throws SalvataggioException;

    /**
     * Restituisce lo chef salvato, ricreato passando dai costruttori del model (quindi sempre valido),
     * oppure un Optional vuoto se non c'è nessun salvataggio.
     *
     * @throws SalvataggioException se il salvataggio c'è ma non si riesce a leggere, è rovinato o ha valori non validi
     */
    Optional<Chef> carica() throws SalvataggioException;

    /**
     * Cancella il salvataggio, per esempio quando la carriera finisce. Se non c'è, non fa nulla.
     *
     * @throws SalvataggioException se il salvataggio c'è ma non si riesce a cancellare
     */
    void elimina() throws SalvataggioException;
}
