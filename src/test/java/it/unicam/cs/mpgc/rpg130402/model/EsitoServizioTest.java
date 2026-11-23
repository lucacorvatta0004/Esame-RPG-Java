package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("EsitoServizio: come sta la carriera dopo un servizio")
class EsitoServizioTest {
    private Chef chef;
    private Tavolo tavoloNormale;
    private Tavolo tavoloDellIspettore;

    @BeforeEach
    void preparaTavoli() {
        // col Coltello in Ceramica un Commis ha 25 di maestria: con la Cottura normale il Soufflé (5 stelle, 50) brucia
        chef = new Chef("Mario");
        chef.usaAttrezzi(new Equipaggiamento(List.of(AttrezziDelGioco.coltelloInCeramica())));
        tavoloNormale = tavoloConIlSoufle(new Comanda(1));
        tavoloDellIspettore = tavoloConIlSoufle(Comanda.dellIspettore(2));
    }

    private static Tavolo tavoloConIlSoufle(Comanda comanda) {
        comanda.aggiungiPiatto(new Piatto("Soufflé al Cioccolato", 5));
        return new Tavolo(comanda);
    }

    /** Lo chef prepara il piatto del tavolo: con il Piatto firma riesce sempre, con la Cottura normale brucia. */
    private void servi(Tavolo tavolo, boolean riuscito) {
        Azione azione = riuscito ? new PiattoFirma() : new CotturaNormale();
        tavolo.registraPiatto(chef.cucina(tavolo.prossimoPiatto(), azione));
    }

    @Test
    @DisplayName("senza ispettore e senza licenziamento la carriera è in corso")
    void inCorso() {
        servi(tavoloNormale, true);
        StatoCarriera stato = new EsitoServizio(List.of(tavoloNormale), false).statoCarriera();
        assertAll(
                () -> assertEquals(StatoCarriera.IN_CORSO, stato),
                () -> assertFalse(stato.isFinita()));
    }

    @Test
    @DisplayName("con lo chef licenziato la carriera è finita: game over")
    void licenziato() {
        servi(tavoloNormale, false);
        StatoCarriera stato = new EsitoServizio(List.of(tavoloNormale), true).statoCarriera();
        assertAll(
                () -> assertEquals(StatoCarriera.LICENZIATO, stato),
                () -> assertTrue(stato.isFinita()),
                () -> assertFalse(stato.isVittoria()));
    }

    @ParameterizedTest(name = "piatto bruciato all'ispettore, chef licenziato = {0}")
    @ValueSource(booleans = {false, true})
    @DisplayName("un piatto bruciato all'ispettore boccia il ristorante, anche se lo chef è licenziato")
    void bocciatoDallIspettore(boolean chefLicenziato) {
        servi(tavoloDellIspettore, false);
        EsitoServizio esito = new EsitoServizio(List.of(tavoloNormale, tavoloDellIspettore), chefLicenziato);
        assertAll(
                () -> assertEquals(StatoCarriera.BOCCIATO_DALL_ISPETTORE, esito.statoCarriera()),
                () -> assertEquals(2, esito.tavoloDellIspettore().orElseThrow().getNumero()));
    }

    @Test
    @DisplayName("tutti i piatti dell'ispettore riusciti e in tempo: il ristorante è stellato, vittoria")
    void ristoranteStellato() {
        servi(tavoloDellIspettore, true);
        StatoCarriera stato = new EsitoServizio(List.of(tavoloNormale, tavoloDellIspettore), false).statoCarriera();
        assertAll(
                () -> assertEquals(StatoCarriera.RISTORANTE_STELLATO, stato),
                () -> assertTrue(stato.isFinita()),
                () -> assertTrue(stato.isVittoria()));
    }
}
