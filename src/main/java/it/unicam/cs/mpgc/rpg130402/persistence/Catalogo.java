package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;
import it.unicam.cs.mpgc.rpg130402.model.Piatto;

import java.util.List;

/**
 * I dati fissi del gioco: i piatti che il ristorante sa cucinare e gli attrezzi da cucina.
 * Il gioco usa solo questa interfaccia: da dove arrivano i dati lo decide l'implementazione.
 * Gli oggetti restituiti sono creati dai costruttori del model, quindi sono sempre validi.
 */
public interface Catalogo {

    /** Tutti i piatti, nell'ordine del catalogo: una lista non modificabile e mai vuota. */
    List<Piatto> getPiatti();

    /**
     * L'attrezzo con quel nome.
     *
     * @throws CatalogoException se nel catalogo non c'è nessun attrezzo con quel nome
     */
    Equipaggiamento getAttrezzo(String nome);
}
