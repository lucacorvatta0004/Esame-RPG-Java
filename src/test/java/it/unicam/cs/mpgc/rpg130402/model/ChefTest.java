package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Chef: creazione, statistiche, progressi salvati e vite")
class ChefTest {
    private Chef chef;
    private Attrezzo coltello;

    @BeforeEach
    void preparaChef() {
        chef = new Chef("Mario");
        coltello = AttrezziDelGioco.coltelloInCeramica();
    }

    @Nested
    @DisplayName("Chef nuovo")
    class ChefNuovo {

        @ParameterizedTest(name = "nome rifiutato: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("il nome non puÃ² essere vuoto")
        void nomeVuotoRifiutato(String nome) {
            assertThrows(IllegalArgumentException.class, () -> new Chef(nome));
        }

        @Test
        @DisplayName("parte da livello 1, Commis, 3 vite e nessun attrezzo")
        void partenza() {
            assertAll(
                    () -> assertEquals("Mario", chef.getNome()),
                    () -> assertEquals(Carriera.INIZIALE, chef.getCarriera()),
                    () -> assertEquals(Grado.COMMIS, chef.getGrado()),
                    () -> assertEquals(3, chef.getReputazione()),
                    () -> assertFalse(chef.isLicenziato()),
                    () -> assertSame(Equipaggiamento.VUOTO, chef.getEquipaggiamento()),
                    () -> assertEquals(new Statistiche(10, 10), chef.getStatisticheEffettive()));
        }

        @Test
        @DisplayName("col Coltello in Ceramica ha 25 maestria e 20 velocitÃ : basta per ogni piatto del Commis")
        void conIlColtello() {
            chef.usaAttrezzi(new Equipaggiamento(List.of(coltello)));
            Piatto piattoPiuDifficileDelCommis = new Piatto("Carbonara", Grado.COMMIS.getStelleMassime());
            assertAll(
                    () -> assertEquals(new Statistiche(25, 20), chef.getStatisticheEffettive()),
                    () -> assertTrue(chef.getStatisticheEffettive().maestria()
                            >= piattoPiuDifficileDelCommis.getMaestriaRichiesta()));
        }

        @Test
        @DisplayName("gli attrezzi da usare non possono essere null")
        void attrezziNullRifiutati() {
            assertThrows(IllegalArgumentException.class, () -> chef.usaAttrezzi(null));
        }
    }

    @Nested
    @DisplayName("Progressi salvati")
    class ProgressiSalvati {
        private Carriera capoPartita;

        @BeforeEach
        void preparaCarriera() {
            capoPartita = new Carriera(3, 40, Grado.CAPO_PARTITA);
        }

        @ParameterizedTest(name = "reputazione {0} rifiutata")
        @ValueSource(ints = {0, 4, -1})
        @DisplayName("la reputazione salvata deve essere da 1 a 3: con 0 la carriera era finita")
        void reputazioneFuoriIntervalloRifiutata(int reputazione) {
            assertThrows(IllegalArgumentException.class, () -> chef.setProgressiSalvati(capoPartita, reputazione));
        }

        @Test
        @DisplayName("una carriera null viene rifiutata e lo chef resta com'era")
        void carrieraNullRifiutata() {
            assertThrows(IllegalArgumentException.class, () -> chef.setProgressiSalvati(null, 2));
            assertAll(
                    () -> assertEquals(Carriera.INIZIALE, chef.getCarriera()),
                    () -> assertEquals(3, chef.getReputazione()));
        }

        @ParameterizedTest(name = "reputazione {0} accettata")
        @ValueSource(ints = {1, 3})
        @DisplayName("con i valori limite 1 e 3 i progressi vengono ripristinati, grado compreso")
        void progressiRipristinati(int reputazione) {
            chef.setProgressiSalvati(capoPartita, reputazione);
            assertAll(
                    () -> assertEquals(capoPartita, chef.getCarriera()),
                    () -> assertEquals(Grado.CAPO_PARTITA, chef.getGrado()),
                    () -> assertEquals(reputazione, chef.getReputazione()),
                    () -> assertEquals(new Statistiche(20, 14), chef.getStatisticheEffettive()));
        }
    }

    @Nested
    @DisplayName("Attrezzi salvati")
    class AttrezziSalvati {
        private Equipaggiamento conLaPadella;

        @BeforeEach
        void preparaAttrezzi() {
            conLaPadella = new Equipaggiamento(List.of(coltello, AttrezziDelGioco.padellaInRame()));
        }

        @Test
        @DisplayName("gli attrezzi salvati non possono essere null")
        void attrezziNullRifiutati() {
            assertThrows(IllegalArgumentException.class, () -> chef.setAttrezziSalvati(null));
        }

        @Test
        @DisplayName("un Commis che non Ã¨ mai stato Capo Partita non puÃ² avere la padella")
        void attrezzoDiUnGradoMaiRaggiuntoRifiutato() {
            assertThrows(IllegalArgumentException.class, () -> chef.setAttrezziSalvati(conLaPadella));
        }

        @Test
        @DisplayName("un Commis retrocesso da Capo Partita puÃ² avere ancora la padella (si toglie al servizio dopo)")
        void attrezzoDiUnGradoPersoAccettato() {
            chef.setProgressiSalvati(new Carriera(2, 150, Grado.CAPO_PARTITA), 2);
            chef.setAttrezziSalvati(conLaPadella);
            assertSame(conLaPadella, chef.getEquipaggiamento());
        }

        @Test
        @DisplayName("un Sous-Chef non puÃ² avere il Coltello Santoku dello Chef")
        void coltelloDelloChefRifiutato() {
            chef.setProgressiSalvati(new Carriera(5, 0, Grado.SOUS_CHEF), 3);
            Equipaggiamento conIlSantoku = new Equipaggiamento(List.of(AttrezziDelGioco.coltelloSantoku()));
            assertThrows(IllegalArgumentException.class, () -> chef.setAttrezziSalvati(conIlSantoku));
        }

        @Test
        @DisplayName("un salvataggio senza attrezzi viene accettato")
        void senzaAttrezziAccettato() {
            assertDoesNotThrow(() -> chef.setAttrezziSalvati(Equipaggiamento.VUOTO));
        }
    }

    @Nested
    @DisplayName("Cucinare e vite")
    class CucinareEVite {
        private Piatto bruschetta;
        private Azione cotturaNormale;

        @BeforeEach
        void preparaPiatto() {
            // 1 stella = 10 di maestria: lo chef senza attrezzi la riesce sempre con la Cottura normale (+10 XP)
            bruschetta = new Piatto("Bruschetta al Pomodoro", 1);
            cotturaNormale = new CotturaNormale();
        }

        @Test
        @DisplayName("un piatto bruciato toglie una vita")
        void piattoBruciatoTogliUnaVita() {
            // un Commis senza attrezzi ha 10 di maestria, la Carbonara (2 stelle) ne chiede 20
            EsitoPiatto esito = chef.cucina(new Piatto("Carbonara", 2), cotturaNormale);
            assertAll(
                    () -> assertFalse(esito.riuscito()),
                    () -> assertEquals(2, chef.getReputazione()),
                    () -> assertEquals(2, esito.reputazioneDopo()));
        }

        @ParameterizedTest(name = "dal livello {0} con {1} XP si arriva a {2}: vite di nuovo a 3")
        @CsvSource({"2, 190, CAPO_PARTITA", "4, 390, SOUS_CHEF", "6, 590, CHEF"})
        @DisplayName("la prima volta che si raggiunge un grado le vite tornano a 3")
        void primaPromozioneRidaLeVite(int livello, int esperienza, Grado gradoNuovo) {
            chef.setProgressiSalvati(new Carriera(livello, esperienza, Grado.perLivello(livello)), 1);
            chef.cucina(bruschetta, cotturaNormale);
            assertAll(
                    () -> assertEquals(gradoNuovo, chef.getGrado()),
                    () -> assertEquals(3, chef.getReputazione()));
        }

        @Test
        @DisplayName("tornando a un grado già raggiunto (dopo una retrocessione) le vite restano quelle")
        void ritornoAUnGradoGiaRaggiuntoNonRidaLeVite() {
            chef.setProgressiSalvati(new Carriera(2, 190, Grado.CAPO_PARTITA), 1);
            chef.cucina(bruschetta, cotturaNormale);
            assertAll(
                    () -> assertEquals(Grado.CAPO_PARTITA, chef.getGrado()),
                    () -> assertEquals(1, chef.getReputazione()));
        }

        @ParameterizedTest(name = "Chef al livello {0} con {1} XP: sale di livello, le vite restano 1")
        @CsvSource({"6, 590", "7, 690"})
        @DisplayName("una volta raggiunto lo Chef le vite non tornano più a 3")
        void daChefLeViteNonTornanoPiu(int livello, int esperienza) {
            // livello 6 con lo Chef già raggiunto = retrocesso che torna Chef; livello 7 = Chef che sale all'8
            chef.setProgressiSalvati(new Carriera(livello, esperienza, Grado.CHEF), 1);
            chef.cucina(bruschetta, cotturaNormale);
            assertAll(
                    () -> assertEquals(livello + 1, chef.getCarriera().livello()),
                    () -> assertEquals(Grado.CHEF, chef.getGrado()),
                    () -> assertEquals(1, chef.getReputazione()));
        }
    }
}
