package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * I conti del tempo: il tavolo N arriva al minuto (N − 1) × 25 e aspetta al massimo 40 minuti.
 * Lo chef nuovo senza attrezzi ha 10 di velocità e 10 di maestria: un piatto da 1 stella richiede 10 minuti
 * e riesce sempre con la Cottura normale, uno da 2 stelle brucia.
 */
@DisplayName("Servizio: ordine dei tavoli, tavoli che se ne vanno, game over e usi delle speciali")
class ServizioTest {
    private Chef chef;
    private SceltaAzione sempreLaPrima;
    private OsservatoreCheRicorda osservatore;

    /** Un osservatore che non stampa niente: si segna solo gli eventi, per controllarli dopo. */
    private static final class OsservatoreCheRicorda implements OsservatoreServizio {
        private final List<Integer> tavoliDeiPiattiPreparati = new ArrayList<>();
        private final List<Integer> tavoliServiti = new ArrayList<>();
        private final List<Integer> reputazioneDopoTavoloAndatoVia = new ArrayList<>();

        @Override
        public void tavoloArrivato(Tavolo tavolo) {
        }

        @Override
        public void piattoDaPreparare(Tavolo tavolo, int minuto) {
            tavoliDeiPiattiPreparati.add(tavolo.getNumero());
        }

        @Override
        public void piattoPreparato(EsitoPiatto esitoPiatto) {
        }

        @Override
        public void tavoloServito(Tavolo tavolo, int minuto) {
            tavoliServiti.add(tavolo.getNumero());
        }

        @Override
        public void tavoloAndatoVia(Tavolo tavolo, int reputazioneDopo) {
            reputazioneDopoTavoloAndatoVia.add(reputazioneDopo);
        }
    }

    @BeforeEach
    void preparaServizio() {
        chef = new Chef("Mario");
        // la prima azione proposta è sempre la Cottura normale
        sempreLaPrima = (piatto, azioni) -> azioni.getFirst();
        osservatore = new OsservatoreCheRicorda();
    }

    /** Una comanda per il tavolo dato con un piatto per ogni numero di stelle ricevuto. */
    private static Comanda comanda(int numeroTavolo, int... stelleDeiPiatti) {
        Comanda comanda = new Comanda(numeroTavolo);
        for (int stelle : stelleDeiPiatti) {
            comanda.aggiungiPiatto(new Piatto("Piatto da " + stelle, stelle));
        }
        return comanda;
    }

    @Test
    @DisplayName("primo arrivato primo servito: il tavolo arrivato mentre lo chef cucina aspetta il suo turno")
    void primoArrivatoPrimoServito() {
        // le comande arrivano in disordine; il tavolo 2 si siede al minuto 25, mentre lo chef cucina per il tavolo 1
        List<Comanda> comande = List.of(comanda(3, 1, 1, 1), comanda(1, 1, 1, 1), comanda(2, 1, 1, 1));
        EsitoServizio esito = new Servizio(chef, sempreLaPrima, comande).gioca(osservatore);
        assertAll(
                () -> assertEquals(List.of(1, 1, 1, 2, 2, 2, 3, 3, 3), osservatore.tavoliDeiPiattiPreparati),
                () -> assertEquals(List.of(1, 2, 3), osservatore.tavoliServiti),
                () -> assertEquals(3, esito.tavoliServiti()),
                () -> assertEquals(0, esito.tavoliAndatiVia()));
    }

    @Test
    @DisplayName("un tavolo che aspetta più di 40 minuti se ne va, tra un piatto e l'altro, e toglie una vita")
    void tavoloAndatoViaTogliUnaVita() {
        // 6 piatti da 10 minuti: al minuto 50 il tavolo ha superato il suo limite (40) e se ne va con un piatto mancante
        EsitoServizio esito = new Servizio(chef, sempreLaPrima, List.of(comanda(1, 1, 1, 1, 1, 1, 1)))
                .gioca(osservatore);
        Tavolo tavolo = esito.tavoli().getFirst();
        assertAll(
                () -> assertTrue(tavolo.isAndatoVia()),
                () -> assertEquals(5, tavolo.getPiattiPreparati()),
                () -> assertEquals(1, esito.tavoliAndatiVia()),
                () -> assertEquals(2, chef.getReputazione()),
                () -> assertEquals(List.of(2), osservatore.reputazioneDopoTavoloAndatoVia));
    }

    @Test
    @DisplayName("al game over il servizio si ferma subito: niente altri piatti né altri tavoli")
    void siFermaAlGameOver() {
        chef.setProgressiSalvati(Carriera.INIZIALE, 1);
        // il primo piatto da 2 stelle brucia e toglie l'ultima vita
        List<Comanda> comande = List.of(comanda(1, 2, 1, 1), comanda(2, 1, 1, 1), comanda(3, 1, 1, 1));
        EsitoServizio esito = new Servizio(chef, sempreLaPrima, comande).gioca(osservatore);
        assertAll(
                () -> assertTrue(chef.isLicenziato()),
                () -> assertEquals(StatoCarriera.LICENZIATO, esito.statoCarriera()),
                () -> assertEquals(List.of(1), osservatore.tavoliDeiPiattiPreparati),
                () -> assertEquals(0, esito.tavoliServiti()),
                () -> assertEquals(0, esito.tavoliAndatiVia()),
                () -> assertFalse(esito.tavoli().get(1).isServito()));
    }

    @Test
    @DisplayName("all'inizio di ogni nuovo servizio il Fuoco alto torna con tutti i suoi 3 usi")
    void specialiRicaricateAOgniServizio() {
        // un Capo Partita col coltello: il Fuoco alto riesce i piatti da 1 stella; 300 XP per salire, qui se ne fanno 60
        chef.setProgressiSalvati(new Carriera(3, 0, Grado.CAPO_PARTITA), 3);
        chef.usaAttrezzi(new Equipaggiamento(List.of(AttrezziDelGioco.coltelloInCeramica())));
        int fuochiAltiPrimoServizio = fuochiAltiUsatiInUnServizio();
        int fuochiAltiSecondoServizio = fuochiAltiUsatiInUnServizio();
        assertAll(
                () -> assertEquals(3, fuochiAltiPrimoServizio),
                () -> assertEquals(3, fuochiAltiSecondoServizio),
                () -> assertEquals(Grado.CAPO_PARTITA, chef.getGrado()));
    }

    /**
     * Gioca un servizio nuovo con 6 piatti scegliendo il Fuoco alto ogni volta che è proposto
     * (la Cottura normale quando ha finito gli usi) e conta quante volte è stato usato.
     */
    private int fuochiAltiUsatiInUnServizio() {
        List<Azione> scelte = new ArrayList<>();
        SceltaAzione preferisciIlFuocoAlto = (piatto, azioni) -> {
            Azione scelta = azioni.stream()
                    .filter(FuocoAlto.class::isInstance)
                    .findFirst()
                    .orElse(azioni.getFirst());
            scelte.add(scelta);
            return scelta;
        };
        List<Comanda> comande = List.of(comanda(1, 1, 1, 1), comanda(2, 1, 1, 1));
        new Servizio(chef, preferisciIlFuocoAlto, comande).gioca(osservatore);
        return (int) scelte.stream()
                .filter(FuocoAlto.class::isInstance)
                .count();
    }
}
