package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Azione speciale: gli usi per servizio delle mosse sbloccate dal grado")
class AzioneSpecialeTest {
    private Piatto carbonara;
    private Statistiche statistiche;

    @BeforeEach
    void preparaPiatto() {
        carbonara = new Piatto("Carbonara", 2);
        statistiche = new Statistiche(30, 20);
    }

    /** La mossa con quel nome, presa dalle azioni dello Chef, che le ha tutte. */
    private static Azione mossa(String nome) {
        return Grado.CHEF.creaAzioniPerServizio().stream()
                .filter(azione -> azione.getNome().equals(nome))
                .findFirst()
                .orElseThrow();
    }

    @Nested
    @DisplayName("Le tre mosse speciali del gioco")
    class MosseDelGioco {

        @ParameterizedTest(name = "{0}: {1} usi")
        @CsvSource({"Fuoco alto, 3", "Ricetta collaudata, 2", "Piatto firma, 1"})
        @DisplayName("una mossa nuova ha tutti i suoi usi ed è disponibile")
        void usiIniziali(String nome, int usi) {
            Azione azione = mossa(nome);
            assertAll(
                    () -> assertEquals(OptionalInt.of(usi), azione.getUsiRimasti()),
                    () -> assertTrue(azione.isDisponibile()));
        }

        @ParameterizedTest(name = "{0}: {1} usi")
        @CsvSource({"Fuoco alto, 3", "Ricetta collaudata, 2", "Piatto firma, 1"})
        @DisplayName("ogni piatto cucinato toglie un uso")
        void ogniPiattoTogliUnUso(String nome, int usi) {
            Azione azione = mossa(nome);
            for (int usati = 1; usati <= usi; usati++) {
                azione.esegui(carbonara, statistiche);
                assertEquals(OptionalInt.of(usi - usati), azione.getUsiRimasti());
            }
        }

        @ParameterizedTest(name = "{0}: {1} usi")
        @CsvSource({"Fuoco alto, 3", "Ricetta collaudata, 2", "Piatto firma, 1"})
        @DisplayName("con gli usi finiti non è disponibile e un altro uso viene rifiutato, senza scendere sotto 0")
        void usiFiniti(String nome, int usi) {
            Azione azione = mossa(nome);
            for (int i = 0; i < usi; i++) {
                azione.esegui(carbonara, statistiche);
            }
            IllegalStateException errore = assertThrows(IllegalStateException.class,
                    () -> azione.esegui(carbonara, statistiche));
            assertAll(
                    () -> assertFalse(azione.isDisponibile()),
                    () -> assertEquals(OptionalInt.of(0), azione.getUsiRimasti()),
                    () -> assertEquals(nome + ": usi finiti per questo servizio", errore.getMessage()));
        }
    }

    @Nested
    @DisplayName("La regola comune a tutte le mosse speciali")
    class RegolaComune {

        /** Una mossa di prova che conta quante volte la sua regola viene eseguita davvero. */
        private static final class MossaDiProva extends AzioneSpeciale {
            private int preparazioni;

            MossaDiProva(int usiPerServizio) {
                super(usiPerServizio);
            }

            @Override
            public String getNome() {
                return "Mossa di prova";
            }

            @Override
            public String getDescrizione() {
                return "serve solo ai test";
            }

            @Override
            protected Preparazione prepara(Piatto piatto, Statistiche statistiche) {
                preparazioni++;
                return new Preparazione(true, statistiche, "mossa di prova");
            }
        }

        @ParameterizedTest(name = "{0} usi per servizio rifiutati")
        @ValueSource(ints = {0, -1})
        @DisplayName("una mossa speciale deve avere almeno un uso per servizio")
        void usiSottoUnoRifiutati(int usi) {
            assertThrows(IllegalArgumentException.class, () -> new MossaDiProva(usi));
        }

        @Test
        @DisplayName("con gli usi finiti la regola della mossa non viene eseguita")
        void regolaNonEseguitaSenzaUsi() {
            MossaDiProva mossa = new MossaDiProva(1);
            mossa.esegui(carbonara, statistiche);
            assertThrows(IllegalStateException.class, () -> mossa.esegui(carbonara, statistiche));
            assertEquals(1, mossa.preparazioni);
        }
    }
}
