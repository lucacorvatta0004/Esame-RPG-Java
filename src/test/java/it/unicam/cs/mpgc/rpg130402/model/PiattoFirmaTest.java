package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Piatto firma: riesce sempre, una volta per servizio")
class PiattoFirmaTest {
    private PiattoFirma piattoFirma;
    private Piatto cassata;

    @BeforeEach
    void preparaMossa() {
        piattoFirma = new PiattoFirma();
        cassata = new Piatto("Cassata Siciliana", 5);
    }

    @Test
    @DisplayName("riesce anche un 5 stelle con le statistiche minime, senza cambiarle")
    void riesceSempre() {
        Statistiche minime = new Statistiche(0, 1);
        Preparazione preparazione = piattoFirma.esegui(cassata, minime);
        assertAll(
                () -> assertTrue(preparazione.riuscito()),
                () -> assertSame(minime, preparazione.statisticheUsate()),
                () -> assertEquals("il piatto firma riesce sempre", preparazione.motivo()));
    }

    @Test
    @DisplayName("dopo un piatto non si può più usare in questo servizio")
    void unSoloUso() {
        piattoFirma.esegui(cassata, new Statistiche(40, 22));
        assertAll(
                () -> assertFalse(piattoFirma.isDisponibile()),
                () -> assertThrows(IllegalStateException.class,
                        () -> piattoFirma.esegui(cassata, new Statistiche(40, 22))));
    }
}
