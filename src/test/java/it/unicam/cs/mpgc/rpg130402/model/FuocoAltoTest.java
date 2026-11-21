package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Fuoco alto: -10 maestria e +10 velocità")
class FuocoAltoTest {
    private FuocoAlto fuocoAlto;
    private Piatto carbonara;

    @BeforeEach
    void preparaMossa() {
        fuocoAlto = new FuocoAlto();
        carbonara = new Piatto("Carbonara", 2);
    }

    @Test
    @DisplayName("cucina con 10 di maestria in meno e 10 di velocità in più")
    void statisticheUsate() {
        assertEquals(new Statistiche(20, 30), fuocoAlto.esegui(carbonara, new Statistiche(30, 20)).statisticheUsate());
    }

    @ParameterizedTest(name = "maestria {0} su un piatto da 2 stelle: riuscito = {1}")
    @CsvSource({"30, true", "29, false", "40, true", "20, false"})
    @DisplayName("riesce solo se la maestria meno 10 arriva a quella richiesta")
    void riesceConLaMaestriaRidotta(int maestria, boolean riuscitoAtteso) {
        assertEquals(riuscitoAtteso, fuocoAlto.esegui(carbonara, new Statistiche(maestria, 20)).riuscito());
    }

    @Test
    @DisplayName("con meno di 10 di maestria si cucina con maestria 0, non negativa")
    void maestriaNonNegativa() {
        Preparazione preparazione = fuocoAlto.esegui(carbonara, new Statistiche(5, 20));
        assertAll(
                () -> assertEquals(0, preparazione.statisticheUsate().maestria()),
                () -> assertFalse(preparazione.riuscito()));
    }

    @Test
    @DisplayName("più velocità vuol dire meno minuti per il piatto")
    void piuVeloce() {
        int velocitaUsata = fuocoAlto.esegui(carbonara, new Statistiche(30, 20)).statisticheUsate().velocita();
        assertTrue(carbonara.minutiDiPreparazione(velocitaUsata) < carbonara.minutiDiPreparazione(20));
    }

    @Test
    @DisplayName("la descrizione dice i numeri della mossa")
    void descrizioneConINumeri() {
        assertAll(
                () -> assertEquals("Fuoco alto", fuocoAlto.getNome()),
                () -> assertTrue(fuocoAlto.getDescrizione().contains("-10 maestria")),
                () -> assertTrue(fuocoAlto.getDescrizione().contains("+10 velocità")));
    }
}
