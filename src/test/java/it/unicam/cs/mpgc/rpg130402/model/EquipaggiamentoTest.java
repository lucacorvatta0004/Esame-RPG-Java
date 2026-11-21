package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Equipaggiamento: gli attrezzi in uso, uno per tipo, con i bonus sommati")
class EquipaggiamentoTest {
    private Attrezzo coltello;
    private Attrezzo padellaInRame;
    private Attrezzo fornoVentilato;
    private Attrezzo santoku;
    private Statistiche base;

    @BeforeEach
    void preparaAttrezzi() {
        coltello = AttrezziDelGioco.coltelloInCeramica();
        padellaInRame = AttrezziDelGioco.padellaInRame();
        fornoVentilato = AttrezziDelGioco.fornoVentilato();
        santoku = AttrezziDelGioco.coltelloSantoku();
        base = new Statistiche(10, 10);
    }

    @Nested
    @DisplayName("Costruttore")
    class Costruttore {

        @Test
        @DisplayName("la lista non può essere null né contenere null")
        void listaConNullRifiutata() {
            List<Attrezzo> conUnNull = Arrays.asList(coltello, null);
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> new Equipaggiamento(null)),
                    () -> assertThrows(IllegalArgumentException.class, () -> new Equipaggiamento(conUnNull)));
        }

        @Test
        @DisplayName("due attrezzi dello stesso tipo vengono rifiutati: uno per tipo")
        void dueDelloStessoTipoRifiutati() {
            IllegalArgumentException errore = assertThrows(IllegalArgumentException.class,
                    () -> new Equipaggiamento(List.of(coltello, santoku)));
            assertEquals("Lo chef può usare un solo attrezzo di tipo Coltello", errore.getMessage());
        }

        @Test
        @DisplayName("la lista viene copiata: cambiarla dopo non cambia gli attrezzi in uso")
        void listaCopiata() {
            List<Attrezzo> attrezzi = new ArrayList<>(List.of(coltello));
            Equipaggiamento equipaggiamento = new Equipaggiamento(attrezzi);
            attrezzi.add(padellaInRame);
            assertEquals(List.of(coltello), equipaggiamento.getAttrezzi());
        }
    }

    @Test
    @DisplayName("senza attrezzi: lista vuota e statistiche uguali")
    void vuoto() {
        assertAll(
                () -> assertTrue(Equipaggiamento.VUOTO.getAttrezzi().isEmpty()),
                () -> assertEquals(base, Equipaggiamento.VUOTO.applicaA(base)),
                () -> assertTrue(Equipaggiamento.VUOTO.isSbloccatoDa(Grado.COMMIS)));
    }

    @Test
    @DisplayName("i bonus di coltello, padella e forno si sommano")
    void bonusSommati() {
        Equipaggiamento treAttrezzi = new Equipaggiamento(List.of(coltello, padellaInRame, fornoVentilato));
        // maestria 10 + 15 + 5 + 0, velocità 10 + 10 + 0 + 1
        assertEquals(new Statistiche(30, 21), treAttrezzi.applicaA(base));
    }

    @Test
    @DisplayName("gli attrezzi sono nell'ordine dei tipi (coltello, padella, forno), qualunque sia l'ordine ricevuto")
    void ordineDeiTipi() {
        Equipaggiamento equipaggiamento = new Equipaggiamento(List.of(fornoVentilato, coltello, padellaInRame));
        assertEquals(List.of(coltello, padellaInRame, fornoVentilato), equipaggiamento.getAttrezzi());
    }

    @Test
    @DisplayName("la lista degli attrezzi non si può modificare da fuori")
    void listaNonModificabile() {
        List<Attrezzo> attrezzi = new Equipaggiamento(List.of(coltello)).getAttrezzi();
        assertThrows(UnsupportedOperationException.class, () -> attrezzi.add(padellaInRame));
    }

    @Test
    @DisplayName("contiene solo l'attrezzo in uso per il suo tipo")
    void contiene() {
        Equipaggiamento equipaggiamento = new Equipaggiamento(List.of(coltello));
        assertAll(
                () -> assertTrue(equipaggiamento.contiene(AttrezziDelGioco.coltelloInCeramica())),
                () -> assertFalse(equipaggiamento.contiene(santoku)),
                () -> assertFalse(equipaggiamento.contiene(padellaInRame)),
                () -> assertThrows(IllegalArgumentException.class, () -> equipaggiamento.contiene(null)));
    }

    @ParameterizedTest(name = "coltello + padella in rame con grado {0}: sbloccato = {1}")
    @CsvSource({"COMMIS, false", "CAPO_PARTITA, true", "CHEF, true"})
    @DisplayName("gli attrezzi sono sbloccati da un grado solo se lo sono tutti")
    void sbloccatoSeLoSonoTutti(Grado grado, boolean atteso) {
        assertEquals(atteso, new Equipaggiamento(List.of(coltello, padellaInRame)).isSbloccatoDa(grado));
    }
}
