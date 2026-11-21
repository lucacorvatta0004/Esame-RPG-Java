package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Piatto: difficoltà, maestria richiesta e minuti di preparazione")
class PiattoTest {
    private Piatto carbonara;

    @BeforeEach
    void preparaPiatto() {
        carbonara = new Piatto("Carbonara", 2);
    }

    @Nested
    @DisplayName("Costruttore")
    class Costruttore {

        @ParameterizedTest(name = "nome rifiutato: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("il nome non può essere vuoto")
        void nomeVuotoRifiutato(String nome) {
            assertThrows(IllegalArgumentException.class, () -> new Piatto(nome, 2));
        }

        @ParameterizedTest(name = "{0} stelle sono rifiutate")
        @ValueSource(ints = {0, 6, -1})
        @DisplayName("le stelle devono essere da 1 a 5")
        void stelleFuoriIntervalloRifiutate(int stelle) {
            assertThrows(IllegalArgumentException.class, () -> new Piatto("Carbonara", stelle));
        }

        @ParameterizedTest(name = "{0} stelle sono accettate")
        @ValueSource(ints = {1, 5})
        @DisplayName("1 e 5 stelle, gli estremi, sono accettate")
        void stelleAgliEstremiAccettate(int stelle) {
            assertEquals(stelle, new Piatto("Piatto di prova", stelle).getDifficoltaStelle());
        }

        @Test
        @DisplayName("il piatto tiene nome e stelle ricevuti")
        void tieneNomeEStelle() {
            assertAll(
                    () -> assertEquals("Carbonara", carbonara.getNomePiatto()),
                    () -> assertEquals(2, carbonara.getDifficoltaStelle()));
        }
    }

    @ParameterizedTest(name = "{0} stelle richiedono {1} maestria")
    @CsvSource({"1, 10", "2, 20", "3, 30", "4, 40", "5, 50"})
    @DisplayName("la maestria richiesta è 10 punti per stella")
    void maestriaRichiesta(int stelle, int maestriaAttesa) {
        assertEquals(maestriaAttesa, new Piatto("Piatto di prova", stelle).getMaestriaRichiesta());
    }

    @Nested
    @DisplayName("Minuti di preparazione")
    class Minuti {

        @ParameterizedTest(name = "{0} stelle a velocità {1}: {2} minuti")
        @CsvSource({
                "1, 10, 10",
                "2, 20, 10",
                "3, 22, 14",
                "5, 22, 23",
                "3, 7, 43",
                "5, 1, 500",
                "1, 100, 1",
                "1, 1000, 1"
        })
        @DisplayName("sono stelle × 100 diviso la velocità, arrotondati per eccesso e mai meno di 1")
        void minutiArrotondatiPerEccesso(int stelle, int velocita, int minutiAttesi) {
            assertEquals(minutiAttesi, new Piatto("Piatto di prova", stelle).minutiDiPreparazione(velocita));
        }

        @ParameterizedTest(name = "velocità {0} rifiutata")
        @ValueSource(ints = {0, -5})
        @DisplayName("una velocità sotto 1 viene rifiutata")
        void velocitaSottoUnoRifiutata(int velocita) {
            assertThrows(IllegalArgumentException.class, () -> carbonara.minutiDiPreparazione(velocita));
        }
    }
}
