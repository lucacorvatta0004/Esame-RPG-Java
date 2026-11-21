package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Statistiche: maestria e velocità con cui si cucina")
class StatisticheTest {

    @Nested
    @DisplayName("Costruttore")
    class Costruttore {

        @ParameterizedTest(name = "maestria {0} e velocità {1} sono rifiutate")
        @CsvSource({"-1, 10", "10, 0", "0, 0", "-5, -5"})
        @DisplayName("maestria sotto 0 o velocità sotto 1 vengono rifiutate")
        void valoriSottoIMinimiRifiutati(int maestria, int velocita) {
            assertThrows(IllegalArgumentException.class, () -> new Statistiche(maestria, velocita));
        }

        @Test
        @DisplayName("i valori minimi, maestria 0 e velocità 1, vengono accettati")
        void valoriMinimiAccettati() {
            Statistiche minime = new Statistiche(0, 1);
            assertAll(
                    () -> assertEquals(0, minime.maestria()),
                    () -> assertEquals(1, minime.velocita()));
        }
    }

    @Nested
    @DisplayName("Bonus")
    class Bonus {
        private Statistiche statistiche;

        @BeforeEach
        void preparaStatistiche() {
            statistiche = new Statistiche(25, 20);
        }

        @ParameterizedTest(name = "25/20 con bonus {0}/{1} diventa {2}/{3}")
        @CsvSource({
                "20, 0, 45, 20",
                "0, 10, 25, 30",
                "-10, 10, 15, 30",
                "-25, -19, 0, 1",
                "-30, 0, 0, 20",
                "0, -50, 25, 1",
                "0, 0, 25, 20"
        })
        @DisplayName("i bonus si sommano e il risultato non scende sotto maestria 0 e velocità 1")
        void bonusSommatiConIMinimi(int bonusMaestria, int bonusVelocita, int maestriaAttesa, int velocitaAttesa) {
            assertEquals(new Statistiche(maestriaAttesa, velocitaAttesa),
                    statistiche.conBonus(bonusMaestria, bonusVelocita));
        }

        @Test
        @DisplayName("i bonus creano una copia: le statistiche di partenza non cambiano")
        void bonusNonCambianoLOriginale() {
            statistiche.conBonus(20, 10);
            assertEquals(new Statistiche(25, 20), statistiche);
        }
    }
}
