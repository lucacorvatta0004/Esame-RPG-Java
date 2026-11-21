package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Validazione: i controlli comuni sui parametri del model")
class ValidazioneTest {

    @Nested
    @DisplayName("Oggetti e testi")
    class OggettiETesti {

        @Test
        @DisplayName("un oggetto null viene rifiutato con un messaggio in italiano")
        void oggettoNullRifiutato() {
            IllegalArgumentException errore = assertThrows(IllegalArgumentException.class,
                    () -> Validazione.verificaNonNullo(null, "Il piatto"));
            assertEquals("Il piatto non può essere null", errore.getMessage());
        }

        @Test
        @DisplayName("un oggetto presente viene accettato")
        void oggettoPresenteAccettato() {
            assertDoesNotThrow(() -> Validazione.verificaNonNullo("qualcosa", "Il testo"));
        }

        @ParameterizedTest(name = "testo rifiutato: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("un testo null, vuoto o di soli spazi viene rifiutato")
        void testoVuotoRifiutato(String testo) {
            assertThrows(IllegalArgumentException.class,
                    () -> Validazione.verificaTestoNonVuoto(testo, "Il nome"));
        }

        @Test
        @DisplayName("un testo con almeno una lettera viene accettato")
        void testoAccettato() {
            assertDoesNotThrow(() -> Validazione.verificaTestoNonVuoto(" a ", "Il nome"));
        }
    }

    @Nested
    @DisplayName("Liste")
    class Liste {

        @Test
        @DisplayName("una lista che non può essere vuota rifiuta null, la lista vuota e gli elementi null")
        void listaNonVuotaRifiutaICasiSbagliati() {
            List<String> conUnNull = Arrays.asList("a", null);
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> Validazione.verificaListaNonVuota(null, "La lista")),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> Validazione.verificaListaNonVuota(List.of(), "La lista")),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> Validazione.verificaListaNonVuota(conUnNull, "La lista")));
        }

        @Test
        @DisplayName("una lista con almeno un elemento viene accettata")
        void listaConUnElementoAccettata() {
            assertDoesNotThrow(() -> Validazione.verificaListaNonVuota(List.of("a"), "La lista"));
        }

        @Test
        @DisplayName("una lista che può essere vuota accetta la lista vuota ma non null e non gli elementi null")
        void listaSenzaNull() {
            List<String> conUnNull = Arrays.asList(null, "b");
            assertAll(
                    () -> assertDoesNotThrow(() -> Validazione.verificaSenzaElementiNull(List.of(), "La lista")),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> Validazione.verificaSenzaElementiNull(null, "La lista")),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> Validazione.verificaSenzaElementiNull(conUnNull, "La lista")));
        }
    }

    @Nested
    @DisplayName("Numeri: valori limite")
    class Numeri {

        @ParameterizedTest(name = "{0} con minimo {1} è accettato")
        @CsvSource({"0, 0", "1, 0", "1, 1", "100, 1"})
        @DisplayName("un valore uguale o sopra il minimo viene accettato")
        void almenoAccettato(int valore, int minimo) {
            assertDoesNotThrow(() -> Validazione.verificaAlmeno(valore, minimo, "Il valore"));
        }

        @ParameterizedTest(name = "{0} con minimo {1} è rifiutato")
        @CsvSource({"-1, 0", "0, 1", "-100, 1"})
        @DisplayName("un valore sotto il minimo viene rifiutato e il messaggio dice il valore ricevuto")
        void almenoRifiutato(int valore, int minimo) {
            IllegalArgumentException errore = assertThrows(IllegalArgumentException.class,
                    () -> Validazione.verificaAlmeno(valore, minimo, "Il valore"));
            assertEquals("Il valore deve essere almeno " + minimo + " (valore ricevuto: " + valore + ")",
                    errore.getMessage());
        }

        @ParameterizedTest(name = "{0} è dentro l''intervallo da 1 a 5")
        @ValueSource(ints = {1, 3, 5})
        @DisplayName("un valore dentro l'intervallo, estremi compresi, viene accettato")
        void intervalloAccettato(int valore) {
            assertDoesNotThrow(() -> Validazione.verificaIntervallo(valore, 1, 5, "Le stelle"));
        }

        @ParameterizedTest(name = "{0} è fuori dall''intervallo da 1 a 5")
        @ValueSource(ints = {0, 6, -1})
        @DisplayName("un valore appena fuori dall'intervallo viene rifiutato")
        void intervalloRifiutato(int valore) {
            assertThrows(IllegalArgumentException.class,
                    () -> Validazione.verificaIntervallo(valore, 1, 5, "Le stelle"));
        }
    }
}
