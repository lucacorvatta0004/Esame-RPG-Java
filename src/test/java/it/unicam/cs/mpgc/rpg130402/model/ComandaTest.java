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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Comanda: l'ordinazione di un tavolo")
class ComandaTest {
    private Comanda comanda;
    private Piatto carbonara;
    private Piatto tiramisu;

    @BeforeEach
    void preparaComanda() {
        comanda = new Comanda(3);
        carbonara = new Piatto("Carbonara", 2);
        tiramisu = new Piatto("Tiramisù", 2);
    }

    @ParameterizedTest(name = "tavolo {0} rifiutato")
    @ValueSource(ints = {0, -1})
    @DisplayName("il numero del tavolo deve essere almeno 1")
    void numeroTavoloSottoUnoRifiutato(int numeroTavolo) {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Comanda(numeroTavolo)),
                () -> assertThrows(IllegalArgumentException.class, () -> Comanda.dellIspettore(numeroTavolo)));
    }

    @Test
    @DisplayName("una comanda nuova ha il suo tavolo, nessun piatto e non è dell'ispettore")
    void comandaNuova() {
        assertAll(
                () -> assertEquals(3, comanda.getNumeroTavolo()),
                () -> assertTrue(comanda.getPiattiOrdinati().isEmpty()),
                () -> assertFalse(comanda.isDellIspettore()));
    }

    @Test
    @DisplayName("i piatti restano nell'ordine in cui sono stati aggiunti")
    void piattiInOrdine() {
        comanda.aggiungiPiatto(carbonara);
        comanda.aggiungiPiatto(tiramisu);
        assertEquals(List.of(carbonara, tiramisu), comanda.getPiattiOrdinati());
    }

    @Test
    @DisplayName("un piatto null viene rifiutato")
    void piattoNullRifiutato() {
        assertThrows(IllegalArgumentException.class, () -> comanda.aggiungiPiatto(null));
    }

    @Test
    @DisplayName("la lista dei piatti non si può modificare da fuori")
    void listaNonModificabile() {
        comanda.aggiungiPiatto(carbonara);
        List<Piatto> piatti = comanda.getPiattiOrdinati();
        assertThrows(UnsupportedOperationException.class, () -> piatti.add(tiramisu));
    }

    @Test
    @DisplayName("la comanda dell'ispettore si riconosce solo nel model e ha il suo tavolo")
    void comandaDellIspettore() {
        Comanda dellIspettore = Comanda.dellIspettore(2);
        assertAll(
                () -> assertTrue(dellIspettore.isDellIspettore()),
                () -> assertEquals(2, dellIspettore.getNumeroTavolo()),
                () -> assertTrue(dellIspettore.getPiattiOrdinati().isEmpty()));
    }
}
