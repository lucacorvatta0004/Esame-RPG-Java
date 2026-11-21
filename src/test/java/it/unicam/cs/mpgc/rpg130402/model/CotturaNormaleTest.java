package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Cottura normale: riesce se la maestria basta")
class CotturaNormaleTest {
    private CotturaNormale cottura;
    private Piatto risotto;

    @BeforeEach
    void preparaMossa() {
        cottura = new CotturaNormale();
        risotto = new Piatto("Risotto ai Funghi", 3);
    }

    @ParameterizedTest(name = "maestria {0} su un piatto da 3 stelle: riuscito = {1}")
    @CsvSource({"30, true", "29, false", "31, true", "0, false"})
    @DisplayName("riesce dalla maestria richiesta in su, brucia anche con un punto in meno")
    void riesceAlLimite(int maestria, boolean riuscitoAtteso) {
        assertEquals(riuscitoAtteso, cottura.esegui(risotto, new Statistiche(maestria, 10)).riuscito());
    }

    @Test
    @DisplayName("cucina con le statistiche ricevute, senza bonus")
    void statisticheSenzaBonus() {
        Statistiche statistiche = new Statistiche(30, 14);
        assertSame(statistiche, cottura.esegui(risotto, statistiche).statisticheUsate());
    }

    @Test
    @DisplayName("è sempre disponibile e non ha limite di usi, anche dopo tanti piatti")
    void usiIllimitati() {
        for (int i = 0; i < 100; i++) {
            cottura.esegui(risotto, new Statistiche(30, 14));
        }
        assertAll(
                () -> assertTrue(cottura.isDisponibile()),
                () -> assertEquals(OptionalInt.empty(), cottura.getUsiRimasti()));
    }

    @Test
    @DisplayName("ha un nome e una descrizione da mostrare al giocatore")
    void nomeEDescrizione() {
        assertAll(
                () -> assertEquals("Cottura normale", cottura.getNome()),
                () -> assertFalse(cottura.getDescrizione().isBlank()));
    }
}
