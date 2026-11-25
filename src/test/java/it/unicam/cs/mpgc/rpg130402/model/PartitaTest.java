package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Partita: gli attrezzi da scegliere prima del servizio")
class PartitaTest {
    private Chef chef;
    private Partita partita;

    @BeforeEach
    void preparaPartita() {
        chef = new Chef("Mario");
        Cucina cucina = new Cucina(new Menu(List.of(new Piatto("Carbonara", 2))),
                new Attrezzatura(AttrezziDelGioco.tutti()));
        partita = new Partita(chef, cucina);
    }

    @Test
    @DisplayName("un Commis può usare solo il Coltello in Ceramica")
    void commisSoloColtello() {
        assertEquals(List.of(List.of(AttrezziDelGioco.coltelloInCeramica())),
                partita.getAttrezziSbloccatiPerTipo());
    }

    @Test
    @DisplayName("un Capo Partita sceglie tra due padelle, il coltello resta uno solo")
    void capoPartitaColtelloEDuePadelle() {
        chef.setProgressiSalvati(new Carriera(3, 0, Grado.CAPO_PARTITA), 3);
        assertEquals(List.of(
                        List.of(AttrezziDelGioco.coltelloInCeramica()),
                        List.of(AttrezziDelGioco.padellaAntiaderente(), AttrezziDelGioco.padellaInRame())),
                partita.getAttrezziSbloccatiPerTipo());
    }
}
