package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Attrezzo;
import it.unicam.cs.mpgc.rpg130402.model.Piatto;

import java.util.List;
import java.util.Optional;

/**
 * I dati fissi del gioco: i piatti che il ristorante sa cucinare e gli attrezzi da cucina.
 * Il gioco usa solo questa interfaccia: da dove arrivano i dati lo decide l'implementazione.
 * Gli oggetti restituiti sono creati dai costruttori del model, quindi sono sempre validi.
 */
public interface Catalogo {

    /** Tutti i piatti, nell'ordine del catalogo: una lista non modificabile e mai vuota. */
    List<Piatto> getPiatti();

    /** Tutti gli attrezzi, nell'ordine del catalogo: una lista non modificabile e mai vuota, con nomi tutti diversi. */
    List<Attrezzo> getAttrezzi();

    /**
     * L'attrezzo con quel nome, oppure un Optional vuoto se nel catalogo non c'è
     * (per esempio un nome scritto in un salvataggio rovinato).
     */
    Optional<Attrezzo> cercaAttrezzo(String nome);
}
