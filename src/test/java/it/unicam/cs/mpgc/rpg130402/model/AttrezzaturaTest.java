package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Attrezzatura: tutti gli attrezzi della cucina, sbloccati dal grado")
class AttrezzaturaTest {
    private Attrezzatura attrezzatura;
    private Attrezzo coltello;
    private Attrezzo antiaderente;
    private Attrezzo rame;
    private Attrezzo ventilato;
    private Attrezzo vapore;
    private Attrezzo santoku;

    @BeforeEach
    void preparaAttrezzatura() {
        attrezzatura = new Attrezzatura(AttrezziDelGioco.tutti());
        coltello = AttrezziDelGioco.coltelloInCeramica();
        antiaderente = AttrezziDelGioco.padellaAntiaderente();
        rame = AttrezziDelGioco.padellaInRame();
        ventilato = AttrezziDelGioco.fornoVentilato();
        vapore = AttrezziDelGioco.fornoAVapore();
        santoku = AttrezziDelGioco.coltelloSantoku();
    }

    @Test
    @DisplayName("la lista non può essere null, vuota o con elementi null")
    void listaNonValidaRifiutata() {
        List<Attrezzo> conUnNull = Arrays.asList(coltello, null);
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Attrezzatura(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Attrezzatura(List.of())),
                () -> assertThrows(IllegalArgumentException.class, () -> new Attrezzatura(conUnNull)));
    }

    @Test
    @DisplayName("il Commis ha solo il Coltello in Ceramica")
    void commis() {
        assertEquals(List.of(List.of(coltello)), attrezzatura.sbloccatiPerTipo(Grado.COMMIS));
    }

    @Test
    @DisplayName("il Capo Partita aggiunge le due padelle, da scegliere")
    void capoPartita() {
        assertEquals(List.of(List.of(coltello), List.of(antiaderente, rame)),
                attrezzatura.sbloccatiPerTipo(Grado.CAPO_PARTITA));
    }

    @Test
    @DisplayName("il Sous-Chef aggiunge i due forni")
    void sousChef() {
        assertEquals(List.of(List.of(coltello), List.of(antiaderente, rame), List.of(ventilato, vapore)),
                attrezzatura.sbloccatiPerTipo(Grado.SOUS_CHEF));
    }

    @Test
    @DisplayName("lo Chef può scegliere anche tra i due coltelli")
    void chef() {
        assertEquals(List.of(List.of(coltello, santoku), List.of(antiaderente, rame), List.of(ventilato, vapore)),
                attrezzatura.sbloccatiPerTipo(Grado.CHEF));
    }

    @Test
    @DisplayName("un grado che non sblocca nessun attrezzo riceve una lista vuota")
    void nessunAttrezzoSbloccato() {
        Attrezzatura soloDelloChef = new Attrezzatura(List.of(santoku));
        assertTrue(soloDelloChef.sbloccatiPerTipo(Grado.COMMIS).isEmpty());
    }

    @Test
    @DisplayName("la lista viene copiata: cambiarla dopo non cambia l'attrezzatura")
    void listaCopiata() {
        List<Attrezzo> attrezzi = new ArrayList<>(List.of(coltello));
        Attrezzatura soloIlColtello = new Attrezzatura(attrezzi);
        attrezzi.add(rame);
        assertEquals(List.of(List.of(coltello)), soloIlColtello.sbloccatiPerTipo(Grado.CHEF));
    }
}
