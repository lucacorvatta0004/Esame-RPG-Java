package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;

/**
 * Chi decide con quale azione cucinare ogni piatto: il giocatore, dalla console o dalla GUI.
 * Il model usa solo questa interfaccia, le implementazioni stanno nell'interfaccia utente.
 */
@FunctionalInterface
public interface SceltaAzione {

    /**
     * Riceve il piatto da preparare e le azioni disponibili adesso (mai vuota:
     * le azioni base sono sempre disponibili). Restituisce una di quelle azioni, mai null.
     */
    Azione scegli(Piatto piatto, List<Azione> azioniDisponibili);
}
