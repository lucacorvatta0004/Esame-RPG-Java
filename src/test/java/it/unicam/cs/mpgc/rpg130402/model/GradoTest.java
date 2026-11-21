package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Grado: soglie, righe dei gradi e azioni sbloccate")
class GradoTest {

    @Nested
    @DisplayName("Grado dal livello")
    class DalLivello {

        @ParameterizedTest(name = "livello {0}: {1}")
        @CsvSource({
                "1, COMMIS", "2, COMMIS",
                "3, CAPO_PARTITA", "4, CAPO_PARTITA",
                "5, SOUS_CHEF", "6, SOUS_CHEF",
                "7, CHEF", "8, CHEF", "100, CHEF"
        })
        @DisplayName("Commis dal livello 1, Capo Partita dal 3, Sous-Chef dal 5, Chef dal 7")
        void sogliaDeiGradi(int livello, Grado gradoAtteso) {
            assertEquals(gradoAtteso, Grado.perLivello(livello));
        }

        @ParameterizedTest(name = "livello {0} rifiutato")
        @ValueSource(ints = {0, -1})
        @DisplayName("un livello sotto 1 viene rifiutato")
        void livelloSottoUnoRifiutato(int livello) {
            assertThrows(IllegalArgumentException.class, () -> Grado.perLivello(livello));
        }

        @Test
        @DisplayName("i gradi sono in ordine di carriera, dal più basso al più alto")
        void ordineDiCarriera() {
            assertEquals(List.of(Grado.COMMIS, Grado.CAPO_PARTITA, Grado.SOUS_CHEF, Grado.CHEF),
                    Arrays.asList(Grado.values()));
        }
    }

    @ParameterizedTest(name = "{0}: \"{1}\", {2} stelle, {3} tavoli, {4}% piatti sfida, {5}% ispettore")
    @CsvSource({
            "COMMIS, Commis, 2, 3, 0, 0",
            "CAPO_PARTITA, Capo Partita, 3, 4, 10, 0",
            "SOUS_CHEF, Sous-Chef, 4, 5, 10, 0",
            "CHEF, Chef, 5, 6, 0, 25"
    })
    @DisplayName("ogni grado ha nome, stelle massime, tavoli per servizio e percentuali della difficoltà")
    void rigaDelGrado(Grado grado, String nome, int stelle, int tavoli, int piattiSfida, int ispettore) {
        assertAll(
                () -> assertEquals(nome, grado.getNome()),
                () -> assertEquals(stelle, grado.getStelleMassime()),
                () -> assertEquals(tavoli, grado.getTavoliPerServizio()),
                () -> assertEquals(piattiSfida, grado.getPercentualePiattiSfida()),
                () -> assertEquals(ispettore, grado.getPercentualeIspettore()));
    }

    @Nested
    @DisplayName("Azioni sbloccate")
    class Azioni {
        private List<Azione> azioniDelloChef;

        @BeforeEach
        void preparaAzioni() {
            azioniDelloChef = Grado.CHEF.creaAzioniPerServizio();
        }

        @ParameterizedTest(name = "{0}: {1}")
        @CsvSource({
                "COMMIS, Cottura normale;Improvvisazione",
                "CAPO_PARTITA, Cottura normale;Improvvisazione;Fuoco alto",
                "SOUS_CHEF, Cottura normale;Improvvisazione;Fuoco alto;Ricetta collaudata",
                "CHEF, Cottura normale;Improvvisazione;Fuoco alto;Ricetta collaudata;Piatto firma"
        })
        @DisplayName("ogni grado ha le sue azioni e quelle dei gradi precedenti, in ordine di carriera")
        void azioniDiOgniGrado(Grado grado, String nomiAttesi) {
            List<String> nomi = grado.creaAzioniPerServizio().stream()
                    .map(Azione::getNome)
                    .toList();
            assertEquals(List.of(nomiAttesi.split(";")), nomi);
        }

        @Test
        @DisplayName("le azioni speciali partono con tutti gli usi, quelle base non hanno limite")
        void usiPieni() {
            assertEquals(List.of(OptionalInt.empty(), OptionalInt.empty(), OptionalInt.of(3), OptionalInt.of(2), OptionalInt.of(1)),
                    azioniDelloChef.stream().map(Azione::getUsiRimasti).toList());
        }

        @Test
        @DisplayName("a ogni chiamata le azioni sono oggetti nuovi, con gli usi di nuovo pieni")
        void azioniNuoveAOgniServizio() {
            Azione fuocoAlto = azioniDelloChef.get(2);
            fuocoAlto.esegui(new Piatto("Carbonara", 2), new Statistiche(30, 20));

            List<Azione> azioniDelServizioDopo = Grado.CHEF.creaAzioniPerServizio();
            assertAll(
                    () -> assertNotSame(azioniDelloChef.get(0), azioniDelServizioDopo.get(0)),
                    () -> assertNotSame(fuocoAlto, azioniDelServizioDopo.get(2)),
                    () -> assertEquals(OptionalInt.of(2), fuocoAlto.getUsiRimasti()),
                    () -> assertEquals(OptionalInt.of(3), azioniDelServizioDopo.get(2).getUsiRimasti()));
        }
    }

    @ParameterizedTest(name = "{0} comprende {1}: {2}")
    @CsvSource({
            "COMMIS, COMMIS, true",
            "CAPO_PARTITA, COMMIS, true",
            "CHEF, COMMIS, true",
            "CHEF, SOUS_CHEF, true",
            "COMMIS, CAPO_PARTITA, false",
            "SOUS_CHEF, CHEF, false"
    })
    @DisplayName("un grado comprende sé stesso e i gradi più bassi")
    void comprende(Grado grado, Grado altro, boolean atteso) {
        assertEquals(atteso, grado.comprende(altro));
    }

    @Nested
    @DisplayName("Piatti sfida")
    class PiattiSfida {

        @ParameterizedTest(name = "{0} con un piatto da {1} stelle: sfida = {2}")
        @CsvSource({
                "COMMIS, 2, false",
                "COMMIS, 3, true",
                "CAPO_PARTITA, 3, false",
                "CAPO_PARTITA, 4, true",
                "SOUS_CHEF, 4, false",
                "SOUS_CHEF, 5, true",
                "CHEF, 5, false",
                "CHEF, 1, false"
        })
        @DisplayName("un piatto è una sfida se ha più stelle delle stelle massime del grado")
        void piattoSfidaAlLimite(Grado grado, int stelle, boolean atteso) {
            assertEquals(atteso, grado.isPiattoSfida(new Piatto("Piatto di prova", stelle)));
        }

        @Test
        @DisplayName("un piatto null viene rifiutato")
        void piattoNullRifiutato() {
            assertThrows(IllegalArgumentException.class, () -> Grado.COMMIS.isPiattoSfida(null));
        }
    }
}
