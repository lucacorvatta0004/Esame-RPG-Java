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

@DisplayName("Ricetta collaudata: +20 maestria")
class RicettaCollaudataTest {
    private RicettaCollaudata ricetta;
    private Piatto filetto;

    @BeforeEach
    void preparaMossa() {
        ricetta = new RicettaCollaudata();
        filetto = new Piatto("Filetto alla Wellington", 4);
    }

    @Test
    @DisplayName("cucina con 20 di maestria in più e la stessa velocità")
    void statisticheUsate() {
        assertEquals(new Statistiche(45, 18), ricetta.esegui(filetto, new Statistiche(25, 18)).statisticheUsate());
    }

    @ParameterizedTest(name = "maestria {0} su un piatto da 4 stelle: riuscito = {1}")
    @CsvSource({"20, true", "19, false", "40, true"})
    @DisplayName("riesce se la maestria più 20 arriva a quella richiesta")
    void riesceConLaMaestriaInPiu(int maestria, boolean riuscitoAtteso) {
        assertEquals(riuscitoAtteso, ricetta.esegui(filetto, new Statistiche(maestria, 18)).riuscito());
    }

    @Test
    @DisplayName("salva un piatto che la Cottura normale brucerebbe")
    void meglioDellaCotturaNormale() {
        Statistiche statistiche = new Statistiche(30, 18);
        assertAll(
                () -> assertFalse(new CotturaNormale().esegui(filetto, statistiche).riuscito()),
                () -> assertTrue(ricetta.esegui(filetto, statistiche).riuscito()));
    }
}
