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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Preparazione: l'esito di un piatto cucinato con una mossa")
class PreparazioneTest {
    private Statistiche statistiche;

    @BeforeEach
    void preparaStatistiche() {
        statistiche = new Statistiche(25, 20);
    }

    @Nested
    @DisplayName("Costruttore")
    class Costruttore {

        @Test
        @DisplayName("le statistiche usate non possono essere null")
        void statisticheNullRifiutate() {
            assertThrows(IllegalArgumentException.class, () -> new Preparazione(true, null, "motivo"));
        }

        @ParameterizedTest(name = "motivo rifiutato: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("il motivo dell'esito non può essere vuoto")
        void motivoVuotoRifiutato(String motivo) {
            assertThrows(IllegalArgumentException.class, () -> new Preparazione(true, statistiche, motivo));
        }
    }

    @Nested
    @DisplayName("La regola di base: secondo la maestria")
    class SecondoMaestria {

        @ParameterizedTest(name = "{0} stelle con maestria {1}: riuscito = {2}")
        @CsvSource({
                "2, 20, true",
                "2, 19, false",
                "2, 21, true",
                "5, 50, true",
                "5, 49, false",
                "1, 0, false"
        })
        @DisplayName("il piatto riesce se la maestria arriva esattamente a stelle × 10 o la supera")
        void riesceDalLimiteInSu(int stelle, int maestria, boolean riuscitoAtteso) {
            Piatto piatto = new Piatto("Piatto di prova", stelle);
            Preparazione preparazione = Preparazione.secondoMaestria(piatto, new Statistiche(maestria, 10));
            assertEquals(riuscitoAtteso, preparazione.riuscito());
        }

        @Test
        @DisplayName("tiene le statistiche ricevute e spiega il motivo con i due numeri")
        void statisticheEMotivo() {
            Preparazione preparazione = Preparazione.secondoMaestria(new Piatto("Risotto ai Funghi", 3), statistiche);
            assertAll(
                    () -> assertSame(statistiche, preparazione.statisticheUsate()),
                    () -> assertEquals("serve 30 maestria, hai 25", preparazione.motivo()));
        }
    }
}
