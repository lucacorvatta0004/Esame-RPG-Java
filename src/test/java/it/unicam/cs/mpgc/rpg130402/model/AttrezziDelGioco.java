package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;

/**
 * I 6 attrezzi di attrezzi.json, scritti qui per i test del model: così i test non dipendono dal file
 * (il catalogo letto dal file si prova nel package persistence). Crea oggetti nuovi a ogni chiamata.
 */
final class AttrezziDelGioco {

    private AttrezziDelGioco() {
    }

    static Attrezzo coltelloInCeramica() {
        return new Attrezzo("Coltello in Ceramica", TipoAttrezzo.COLTELLO, Grado.COMMIS, 10, 15);
    }

    static Attrezzo padellaAntiaderente() {
        return new Attrezzo("Padella Antiaderente", TipoAttrezzo.PADELLA, Grado.CAPO_PARTITA, 1, 0);
    }

    static Attrezzo padellaInRame() {
        return new Attrezzo("Padella in Rame", TipoAttrezzo.PADELLA, Grado.CAPO_PARTITA, 0, 5);
    }

    static Attrezzo fornoVentilato() {
        return new Attrezzo("Forno Ventilato", TipoAttrezzo.FORNO, Grado.SOUS_CHEF, 1, 0);
    }

    static Attrezzo fornoAVapore() {
        return new Attrezzo("Forno a Vapore", TipoAttrezzo.FORNO, Grado.SOUS_CHEF, 0, 5);
    }

    static Attrezzo coltelloSantoku() {
        return new Attrezzo("Coltello Santoku", TipoAttrezzo.COLTELLO, Grado.CHEF, 12, 5);
    }

    /** Tutti e 6, nell'ordine del file. */
    static List<Attrezzo> tutti() {
        return List.of(coltelloInCeramica(), padellaAntiaderente(), padellaInRame(),
                fornoVentilato(), fornoAVapore(), coltelloSantoku());
    }
}
