package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Attrezzo: un attrezzo da cucina con i suoi bonus")
class AttrezzoTest {
    private Attrezzo coltello;

    @BeforeEach
    void preparaAttrezzo() {
        coltello = new Attrezzo("Coltello in Ceramica", TipoAttrezzo.COLTELLO, Grado.COMMIS, 10, 15);
    }

    @Nested
    @DisplayName("Costruttore")
    class Costruttore {

        @ParameterizedTest(name = "nome rifiutato: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("il nome non può essere vuoto")
        void nomeVuotoRifiutato(String nome) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Attrezzo(nome, TipoAttrezzo.COLTELLO, Grado.COMMIS, 10, 15));
        }

        @Test
        @DisplayName("tipo e grado che lo sblocca non possono essere null")
        void tipoEGradoNullRifiutati() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new Attrezzo("Coltello", null, Grado.COMMIS, 10, 15)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new Attrezzo("Coltello", TipoAttrezzo.COLTELLO, null, 10, 15)));
        }

        @ParameterizedTest(name = "bonus velocità {0} e maestria {1} rifiutati")
        @CsvSource({"-1, 0", "0, -1", "-5, -5"})
        @DisplayName("i bonus non possono essere negativi")
        void bonusNegativiRifiutati(int bonusVelocita, int bonusMaestria) {
            assertThrows(IllegalArgumentException.class,
                    () -> new Attrezzo("Padella", TipoAttrezzo.PADELLA, Grado.CAPO_PARTITA, bonusVelocita, bonusMaestria));
        }

        @Test
        @DisplayName("bonus 0, il valore limite, è accettato")
        void bonusZeroAccettato() {
            assertDoesNotThrow(() -> new Attrezzo("Padella", TipoAttrezzo.PADELLA, Grado.CAPO_PARTITA, 0, 0));
        }

        @Test
        @DisplayName("l'attrezzo tiene i dati ricevuti")
        void tieneIDati() {
            assertAll(
                    () -> assertEquals("Coltello in Ceramica", coltello.getNome()),
                    () -> assertEquals(TipoAttrezzo.COLTELLO, coltello.getTipo()),
                    () -> assertEquals(Grado.COMMIS, coltello.getGradoMinimo()),
                    () -> assertEquals(10, coltello.getBonusVelocita()),
                    () -> assertEquals(15, coltello.getBonusMaestria()));
        }
    }

    @Nested
    @DisplayName("Uguaglianza: il nome identifica l'attrezzo")
    class Uguaglianza {

        @Test
        @DisplayName("due attrezzi con lo stesso nome sono uguali e hanno lo stesso hashCode (un HashSet li conta una volta), anche con dati diversi")
        void stessoNomeUguali() {
            Attrezzo copia = new Attrezzo("Coltello in Ceramica", TipoAttrezzo.COLTELLO, Grado.CHEF, 1, 1);
            assertAll(
                    () -> assertEquals(coltello, copia),
                    () -> assertEquals(coltello.hashCode(), copia.hashCode()),
                    () -> assertEquals(1, new HashSet<>(List.of(coltello, copia)).size()));
        }

        @Test
        @DisplayName("due attrezzi con nomi diversi sono diversi, anche con gli stessi dati")
        void nomiDiversiDiversi() {
            Attrezzo altro = new Attrezzo("Coltello Santoku", TipoAttrezzo.COLTELLO, Grado.COMMIS, 10, 15);
            assertNotEquals(coltello, altro);
        }

        @Test
        @DisplayName("un attrezzo è uguale a sé stesso, diverso da null e da oggetti di un altro tipo")
        void casiAlContorno() {
            assertAll(
                    () -> assertEquals(coltello, coltello),
                    () -> assertNotEquals(null, coltello),
                    () -> assertNotEquals("Coltello in Ceramica", coltello));
        }
    }

    @Nested
    @DisplayName("Sblocco con il grado")
    class Sblocco {

        @ParameterizedTest(name = "attrezzo del {0}, chef {1}: sbloccato = {2}")
        @CsvSource({
                "COMMIS, COMMIS, true",
                "COMMIS, CHEF, true",
                "CAPO_PARTITA, COMMIS, false",
                "CAPO_PARTITA, CAPO_PARTITA, true",
                "CAPO_PARTITA, SOUS_CHEF, true",
                "SOUS_CHEF, CAPO_PARTITA, false",
                "CHEF, SOUS_CHEF, false",
                "CHEF, CHEF, true"
        })
        @DisplayName("lo sblocca il suo grado e tutti i gradi più alti")
        void sbloccatoDalGradoEDaiSuccessivi(Grado gradoMinimo, Grado gradoDelloChef, boolean atteso) {
            Attrezzo attrezzo = new Attrezzo("Attrezzo di prova", TipoAttrezzo.FORNO, gradoMinimo, 1, 0);
            assertEquals(atteso, attrezzo.isSbloccatoDa(gradoDelloChef));
        }

        @Test
        @DisplayName("il grado dello chef non può essere null")
        void gradoNullRifiutato() {
            assertThrows(IllegalArgumentException.class, () -> coltello.isSbloccatoDa(null));
        }
    }
}
