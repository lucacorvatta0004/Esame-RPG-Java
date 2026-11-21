package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Carriera: esperienza, livelli e statistiche dello chef")
class CarrieraTest {
    private Carriera iniziale;

    @BeforeEach
    void preparaCarriera() {
        iniziale = Carriera.INIZIALE;
    }

    @Nested
    @DisplayName("Costruttore")
    class Costruttore {

        @ParameterizedTest(name = "livello {0}, esperienza {1}, grado più alto {2}: rifiutata")
        @CsvSource({
                "0, 0, COMMIS",
                "-1, 0, COMMIS",
                "1, -1, COMMIS",
                "1, 100, COMMIS",
                "3, 300, CAPO_PARTITA",
                "3, 0, COMMIS",
                "7, 0, SOUS_CHEF"
        })
        @DisplayName("livello sotto 1, esperienza fuori dal livello o grado più alto sotto quello del livello vengono rifiutati")
        void valoriNonValidiRifiutati(int livello, int esperienza, Grado gradoMassimo) {
            assertThrows(IllegalArgumentException.class, () -> new Carriera(livello, esperienza, gradoMassimo));
        }

        @ParameterizedTest(name = "livello {0}, esperienza {1}, grado più alto {2}: accettata")
        @CsvSource({
                "1, 0, COMMIS",
                "1, 99, COMMIS",
                "3, 299, CAPO_PARTITA",
                "2, 0, CAPO_PARTITA",
                "1, 0, CHEF"
        })
        @DisplayName("ai limiti, e con un grado più alto sopra quello del livello (dopo una retrocessione), viene accettata")
        void valoriAiLimitiAccettati(int livello, int esperienza, Grado gradoMassimo) {
            assertDoesNotThrow(() -> new Carriera(livello, esperienza, gradoMassimo));
        }

        @Test
        @DisplayName("il grado più alto raggiunto non può essere null")
        void gradoNullRifiutato() {
            assertThrows(IllegalArgumentException.class, () -> new Carriera(1, 0, null));
        }

        @Test
        @DisplayName("la carriera iniziale è livello 1, nessuna esperienza, Commis")
        void carrieraIniziale() {
            assertAll(
                    () -> assertEquals(1, iniziale.livello()),
                    () -> assertEquals(0, iniziale.esperienza()),
                    () -> assertEquals(Grado.COMMIS, iniziale.grado()),
                    () -> assertEquals(Grado.COMMIS, iniziale.gradoMassimoRaggiunto()),
                    () -> assertEquals(100, iniziale.esperienzaPerSalire()));
        }
    }

    @ParameterizedTest(name = "{0} stelle, riuscito = {1}: {2} di esperienza")
    @CsvSource({
            "1, true, 10",
            "3, true, 30",
            "5, true, 50",
            "1, false, -5",
            "3, false, -15",
            "5, false, -25"
    })
    @DisplayName("un piatto riuscito vale stelle × 10, uno bruciato toglie stelle × 5")
    void esperienzaDiUnPiatto(int stelle, boolean riuscito, int esperienzaAttesa) {
        assertEquals(esperienzaAttesa, Carriera.esperienzaPer(new Piatto("Piatto di prova", stelle), riuscito));
    }

    @Nested
    @DisplayName("Salire di livello")
    class Salire {

        @Test
        @DisplayName("sotto la soglia si resta al livello e l'esperienza si somma")
        void sottoLaSoglia() {
            assertEquals(new Carriera(1, 99, Grado.COMMIS), iniziale.conEsperienza(99));
        }

        @Test
        @DisplayName("arrivando esattamente alla soglia si sale di livello con 0 di esperienza")
        void sogliaEsatta() {
            assertEquals(new Carriera(2, 0, Grado.COMMIS), iniziale.conEsperienza(100));
        }

        @Test
        @DisplayName("l'esperienza oltre la soglia resta nel livello nuovo")
        void avanzoCheResta() {
            assertEquals(new Carriera(2, 20, Grado.COMMIS), new Carriera(1, 90, Grado.COMMIS).conEsperienza(30));
        }

        @Test
        @DisplayName("con tanta esperienza si salgono più livelli in un colpo (100 + 200, avanzo 50)")
        void piuLivelliInUnColpo() {
            assertEquals(new Carriera(3, 50, Grado.CAPO_PARTITA), iniziale.conEsperienza(350));
        }

        @Test
        @DisplayName("salendo al livello 3 si diventa Capo Partita e il grado più alto sale")
        void promozione() {
            Carriera dopo = new Carriera(2, 190, Grado.COMMIS).conEsperienza(10);
            assertAll(
                    () -> assertEquals(3, dopo.livello()),
                    () -> assertEquals(Grado.CAPO_PARTITA, dopo.grado()),
                    () -> assertEquals(Grado.CAPO_PARTITA, dopo.gradoMassimoRaggiunto()));
        }

        @Test
        @DisplayName("la carriera è immutabile: quella di partenza non cambia")
        void immutabile() {
            iniziale.conEsperienza(150);
            assertEquals(new Carriera(1, 0, Grado.COMMIS), iniziale);
        }

        @Test
        @DisplayName("zero di esperienza non cambia niente")
        void zeroNonCambiaNiente() {
            Carriera carriera = new Carriera(4, 120, Grado.CAPO_PARTITA);
            assertEquals(carriera, carriera.conEsperienza(0));
        }
    }

    @Nested
    @DisplayName("Scendere di livello")
    class Scendere {

        @Test
        @DisplayName("sotto zero si scende di livello e si riparte dalla soglia del livello sotto")
        void discesaDiUnLivello() {
            assertEquals(new Carriera(1, 85, Grado.COMMIS), new Carriera(2, 10, Grado.COMMIS).conEsperienza(-25));
        }

        @Test
        @DisplayName("scendendo dal livello 3 al 2 si torna Commis, ma il grado più alto resta Capo Partita")
        void retrocessione() {
            Carriera dopo = new Carriera(3, 5, Grado.CAPO_PARTITA).conEsperienza(-25);
            assertAll(
                    () -> assertEquals(2, dopo.livello()),
                    () -> assertEquals(180, dopo.esperienza()),
                    () -> assertEquals(Grado.COMMIS, dopo.grado()),
                    () -> assertEquals(Grado.CAPO_PARTITA, dopo.gradoMassimoRaggiunto()));
        }

        @Test
        @DisplayName("si possono scendere più livelli in un colpo")
        void piuLivelliInUnColpo() {
            assertEquals(new Carriera(1, 50, Grado.CAPO_PARTITA),
                    new Carriera(3, 0, Grado.CAPO_PARTITA).conEsperienza(-250));
        }

        @ParameterizedTest(name = "livello 1 con {0} di esperienza, perdita {1}: resta a 0")
        @CsvSource({"0, -25", "10, -25", "0, -1000"})
        @DisplayName("al livello 1 l'esperienza non scende sotto 0")
        void livelloUnoFermoAZero(int esperienza, int perdita) {
            assertEquals(new Carriera(1, 0, Grado.COMMIS), new Carriera(1, esperienza, Grado.COMMIS).conEsperienza(perdita));
        }

        @Test
        @DisplayName("lo Chef che scende al livello 6 torna Sous-Chef ma il grado più alto resta Chef")
        void gradoPiuAltoNonScende() {
            Carriera dopo = new Carriera(7, 0, Grado.CHEF).conEsperienza(-10);
            assertAll(
                    () -> assertEquals(new Carriera(6, 590, Grado.CHEF), dopo),
                    () -> assertEquals(Grado.SOUS_CHEF, dopo.grado()));
        }
    }

    @ParameterizedTest(name = "livello {0}: {1}, maestria {2}, velocità {3}, {4} XP per salire")
    @CsvSource({
            "1, COMMIS, 10, 10, 100",
            "2, COMMIS, 15, 12, 200",
            "3, CAPO_PARTITA, 20, 14, 300",
            "5, SOUS_CHEF, 30, 18, 500",
            "7, CHEF, 40, 22, 700",
            "10, CHEF, 55, 28, 1000"
    })
    @DisplayName("grado, statistiche base (+5 maestria e +2 velocità a livello) ed esperienza per salire si ricavano dal livello")
    void tuttoDalLivello(int livello, Grado gradoAtteso, int maestriaAttesa, int velocitaAttesa, int esperienzaPerSalire) {
        Carriera carriera = new Carriera(livello, 0, Grado.perLivello(livello));
        assertAll(
                () -> assertEquals(gradoAtteso, carriera.grado()),
                () -> assertEquals(new Statistiche(maestriaAttesa, velocitaAttesa), carriera.statisticheBase()),
                () -> assertEquals(esperienzaPerSalire, carriera.esperienzaPerSalire()));
    }
}
