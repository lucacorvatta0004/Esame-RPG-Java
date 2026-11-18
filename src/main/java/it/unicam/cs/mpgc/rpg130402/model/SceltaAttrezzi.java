package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;

/**
 * Chi decide quale attrezzo usare quando, prima di un servizio, per un tipo c'è più di un attrezzo sbloccato:
 * il giocatore, dalla console o dalla GUI. Il model usa solo questa interfaccia, le implementazioni stanno
 * nell'interfaccia utente.
 */
@FunctionalInterface
public interface SceltaAttrezzi {

    /**
     * Riceve gli attrezzi sbloccati di uno stesso tipo (almeno due: c'è una scelta) e gli attrezzi
     * che lo chef usa adesso, solo per mostrarli. Restituisce uno degli attrezzi ricevuti, mai null.
     */
    Attrezzo scegli(List<Attrezzo> attrezziDelTipo, Equipaggiamento equipaggiamentoAttuale);
}
