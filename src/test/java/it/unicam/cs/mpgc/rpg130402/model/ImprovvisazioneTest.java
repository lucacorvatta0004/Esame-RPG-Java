package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Improvvisazione: riesce una volta su due, la maestria non conta")
class ImprovvisazioneTest {
    private Piatto soufle;
    private Piatto bruschetta;

    /** Un generatore che risponde sempre allo stesso modo: così l'esito non dipende dalla fortuna. */
    private static final class RandomSempreUguale extends Random {
        private final boolean risposta;

        RandomSempreUguale(boolean risposta) {
            this.risposta = risposta;
        }

        @Override
        public boolean nextBoolean() {
            return risposta;
        }
    }

    @BeforeEach
    void preparaPiatti() {
        soufle = new Piatto("Soufflé al Cioccolato", 5);
        bruschetta = new Piatto("Bruschetta al Pomodoro", 1);
    }

    @Test
    @DisplayName("il generatore casuale non può essere null")
    void generatoreNullRifiutato() {
        assertThrows(IllegalArgumentException.class, () -> new Improvvisazione(null));
    }

    @ParameterizedTest(name = "seme {0}")
    @ValueSource(longs = {1, 42, 2026})
    @DisplayName("con un seme fisso gli esiti seguono la stessa sequenza di un generatore con lo stesso seme")
    void ripetibileConUnSemeFisso(long seme) {
        Improvvisazione improvvisazione = new Improvvisazione(new Random(seme));
        Random riferimento = new Random(seme);
        for (int i = 0; i < 20; i++) {
            assertEquals(riferimento.nextBoolean(), improvvisazione.esegui(bruschetta, new Statistiche(25, 20)).riuscito());
        }
    }

    @Test
    @DisplayName("quando va bene riesce anche un 5 stelle con maestria 0")
    void riesceSenzaMaestria() {
        Statistiche senzaMaestria = new Statistiche(0, 10);
        Preparazione preparazione = new Improvvisazione(new RandomSempreUguale(true)).esegui(soufle, senzaMaestria);
        assertAll(
                () -> assertTrue(preparazione.riuscito()),
                () -> assertSame(senzaMaestria, preparazione.statisticheUsate()),
                () -> assertEquals("l'improvvisazione è andata bene", preparazione.motivo()));
    }

    @Test
    @DisplayName("quando va male brucia anche un 1 stella con maestria 100")
    void bruciaConTantaMaestria() {
        Preparazione preparazione = new Improvvisazione(new RandomSempreUguale(false))
                .esegui(bruschetta, new Statistiche(100, 10));
        assertAll(
                () -> assertFalse(preparazione.riuscito()),
                () -> assertEquals("l'improvvisazione è andata male, la maestria non contava", preparazione.motivo()));
    }

    @Test
    @DisplayName("è un'azione base: sempre disponibile, senza limite di usi")
    void sempreDisponibile() {
        Improvvisazione improvvisazione = new Improvvisazione(new Random(7));
        for (int i = 0; i < 50; i++) {
            improvvisazione.esegui(bruschetta, new Statistiche(25, 20));
        }
        assertAll(
                () -> assertTrue(improvvisazione.isDisponibile()),
                () -> assertTrue(improvvisazione.getUsiRimasti().isEmpty()));
    }
}
