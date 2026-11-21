package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Menu: le comande di un servizio, con piatti sfida e ispettore Michelin")
class MenuTest {
    private static final int SERVIZI_DI_PROVA = 1000;

    private List<Piatto> piatti;

    /**
     * Un generatore truccato che restituisce sempre 0: ogni percentuale sopra 0 "succede"
     * (0 è minore di 10 e di 25), l'ispettore si siede al tavolo 1 e si prende il primo piatto adatto.
     */
    private static final class RandomSempreZero extends Random {
        @Override
        public int nextInt(int limite) {
            return 0;
        }
    }

    /** Un generatore truccato che restituisce sempre il numero più alto: nessuna percentuale sotto 100 succede. */
    private static final class RandomSempreIlMassimo extends Random {
        @Override
        public int nextInt(int limite) {
            return limite - 1;
        }
    }

    @BeforeEach
    void preparaPiatti() {
        piatti = List.of(
                new Piatto("Spaghetti al Pomodoro", 1), new Piatto("Insalata Caprese", 1),
                new Piatto("Carbonara", 2), new Piatto("Tiramisù", 2),
                new Piatto("Risotto ai Funghi", 3), new Piatto("Lasagne alla Bolognese", 3),
                new Piatto("Filetto alla Wellington", 4), new Piatto("Ossobuco alla Milanese", 4),
                new Piatto("Soufflé al Cioccolato", 5), new Piatto("Cassata Siciliana", 5));
    }

    private static List<Piatto> tuttiIPiatti(List<Comanda> comande) {
        return comande.stream()
                .flatMap(comanda -> comanda.getPiattiOrdinati().stream())
                .toList();
    }

    private static long comandeDellIspettore(List<Comanda> comande) {
        return comande.stream().filter(Comanda::isDellIspettore).count();
    }

    @Nested
    @DisplayName("Costruttore")
    class Costruttore {

        @Test
        @DisplayName("la lista dei piatti non può essere null, vuota o con elementi null")
        void listaNonValidaRifiutata() {
            List<Piatto> conUnNull = Arrays.asList(piatti.getFirst(), null);
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> new Menu(null)),
                    () -> assertThrows(IllegalArgumentException.class, () -> new Menu(List.of())),
                    () -> assertThrows(IllegalArgumentException.class, () -> new Menu(conUnNull)));
        }

        @Test
        @DisplayName("il generatore casuale non può essere null")
        void generatoreNullRifiutato() {
            assertThrows(IllegalArgumentException.class, () -> new Menu(piatti, null));
        }

        @Test
        @DisplayName("la lista viene copiata: svuotarla dopo non cambia il menu")
        void listaCopiata() {
            List<Piatto> soloUnPiatto = new ArrayList<>(List.of(new Piatto("Carbonara", 2)));
            Menu menu = new Menu(soloUnPiatto, new Random(1));
            soloUnPiatto.clear();
            assertEquals(9, tuttiIPiatti(menu.generaComandeDelServizio(Grado.COMMIS)).size());
        }
    }

    @Nested
    @DisplayName("Le comande del servizio")
    class Comande {

        @Test
        @DisplayName("il grado non può essere null")
        void gradoNullRifiutato() {
            assertThrows(IllegalArgumentException.class, () -> new Menu(piatti, new Random(1)).generaComandeDelServizio(null));
        }

        @ParameterizedTest(name = "{0}")
        @EnumSource(Grado.class)
        @DisplayName("una comanda per tavolo del grado, tavoli numerati da 1, 3 piatti ciascuna")
        void unaComandaPerTavolo(Grado grado) {
            List<Comanda> comande = new Menu(piatti, new Random(2026)).generaComandeDelServizio(grado);
            assertAll(
                    () -> assertEquals(grado.getTavoliPerServizio(), comande.size()),
                    () -> assertEquals(IntStream.rangeClosed(1, grado.getTavoliPerServizio()).boxed().toList(),
                            comande.stream().map(Comanda::getNumeroTavolo).toList()),
                    () -> assertTrue(comande.stream().allMatch(comanda -> comanda.getPiattiOrdinati().size() == 3)));
        }

        @Test
        @DisplayName("la lista delle comande non si può modificare da fuori")
        void listaNonModificabile() {
            List<Comanda> comande = new Menu(piatti, new Random(1)).generaComandeDelServizio(Grado.COMMIS);
            assertThrows(UnsupportedOperationException.class, () -> comande.add(new Comanda(9)));
        }
    }

    @Nested
    @DisplayName("Stelle per grado e piatti sfida")
    class Stelle {

        @Test
        @DisplayName("il Commis non riceve mai piatti sfida né l'ispettore, anche quando ogni probabilità scatta")
        void commisMaiSfida() {
            List<Comanda> comande = new Menu(piatti, new RandomSempreZero()).generaComandeDelServizio(Grado.COMMIS);
            assertAll(
                    () -> assertTrue(tuttiIPiatti(comande).stream().noneMatch(Grado.COMMIS::isPiattoSfida)),
                    () -> assertEquals(0, comandeDellIspettore(comande)));
        }

        @Test
        @DisplayName("con un seme fisso, in 1000 servizi il Commis non riceve mai un piatto sopra le 2 stelle")
        void commisInMilleServizi() {
            Menu menu = new Menu(piatti, new Random(42));
            for (int servizio = 0; servizio < SERVIZI_DI_PROVA; servizio++) {
                List<Comanda> comande = menu.generaComandeDelServizio(Grado.COMMIS);
                assertTrue(tuttiIPiatti(comande).stream().allMatch(piatto -> piatto.getDifficoltaStelle() <= 2));
                assertEquals(0, comandeDellIspettore(comande));
            }
        }

        @Test
        @DisplayName("quando la probabilità scatta, il Capo Partita riceve piatti sfida di esattamente una stella in più (4)")
        void capoPartitaConPiattiSfida() {
            List<Piatto> ordinati = tuttiIPiatti(new Menu(piatti, new RandomSempreZero())
                    .generaComandeDelServizio(Grado.CAPO_PARTITA));
            assertTrue(ordinati.stream().allMatch(piatto -> piatto.getDifficoltaStelle() == 4));
        }

        @Test
        @DisplayName("quando la probabilità scatta, il Sous-Chef riceve piatti sfida da 5 stelle")
        void sousChefConPiattiSfida() {
            List<Piatto> ordinati = tuttiIPiatti(new Menu(piatti, new RandomSempreZero())
                    .generaComandeDelServizio(Grado.SOUS_CHEF));
            assertTrue(ordinati.stream().allMatch(piatto -> piatto.getDifficoltaStelle() == 5));
        }

        @ParameterizedTest(name = "{0}")
        @EnumSource(Grado.class)
        @DisplayName("quando nessuna probabilità scatta: niente piatti sfida, niente ispettore")
        void senzaSfida(Grado grado) {
            List<Comanda> comande = new Menu(piatti, new RandomSempreIlMassimo()).generaComandeDelServizio(grado);
            assertAll(
                    () -> assertTrue(tuttiIPiatti(comande).stream().noneMatch(grado::isPiattoSfida)),
                    () -> assertEquals(0, comandeDellIspettore(comande)));
        }

        @Test
        @DisplayName("con un seme fisso, in 1000 servizi il Capo Partita riceve qualche piatto sfida e mai sopra le 4 stelle")
        void capoPartitaInMilleServizi() {
            Menu menu = new Menu(piatti, new Random(42));
            List<Piatto> ordinati = IntStream.range(0, SERVIZI_DI_PROVA)
                    .mapToObj(servizio -> menu.generaComandeDelServizio(Grado.CAPO_PARTITA))
                    .flatMap(comande -> tuttiIPiatti(comande).stream())
                    .toList();
            assertAll(
                    () -> assertTrue(ordinati.stream().anyMatch(Grado.CAPO_PARTITA::isPiattoSfida)),
                    () -> assertTrue(ordinati.stream().allMatch(piatto -> piatto.getDifficoltaStelle() <= 4)));
        }
    }

    @Nested
    @DisplayName("Ispettore Michelin")
    class Ispettore {

        @Test
        @DisplayName("quando arriva prende un solo tavolo e ordina 3 piatti da 5 stelle; gli altri tavoli sono normali")
        void ispettoreAlTavoloUno() {
            List<Comanda> comande = new Menu(piatti, new RandomSempreZero()).generaComandeDelServizio(Grado.CHEF);
            Comanda tavoloUno = comande.getFirst();
            assertAll(
                    () -> assertEquals(1, comandeDellIspettore(comande)),
                    () -> assertTrue(tavoloUno.isDellIspettore()),
                    () -> assertEquals(1, tavoloUno.getNumeroTavolo()),
                    () -> assertEquals(3, tavoloUno.getPiattiOrdinati().size()),
                    () -> assertTrue(tavoloUno.getPiattiOrdinati().stream().allMatch(piatto -> piatto.getDifficoltaStelle() == 5)),
                    () -> assertEquals(Grado.CHEF.getTavoliPerServizio(), comande.size()));
        }

        @Test
        @DisplayName("ordina i piatti più difficili del menu: senza 5 stelle, quelli da 4")
        void piattiPiuDifficiliDelMenu() {
            List<Piatto> finoA4Stelle = piatti.stream().filter(piatto -> piatto.getDifficoltaStelle() <= 4).toList();
            Comanda dellIspettore = new Menu(finoA4Stelle, new RandomSempreZero())
                    .generaComandeDelServizio(Grado.CHEF).getFirst();
            assertTrue(dellIspettore.getPiattiOrdinati().stream().allMatch(piatto -> piatto.getDifficoltaStelle() == 4));
        }

        @Test
        @DisplayName("con un seme fisso, in 1000 servizi da Chef l'ispettore arriva qualche volta, mai due volte nello stesso servizio")
        void chefInMilleServizi() {
            Menu menu = new Menu(piatti, new Random(42));
            List<Long> ispettoriPerServizio = IntStream.range(0, SERVIZI_DI_PROVA)
                    .mapToObj(servizio -> comandeDellIspettore(menu.generaComandeDelServizio(Grado.CHEF)))
                    .toList();
            assertAll(
                    () -> assertTrue(ispettoriPerServizio.contains(1L)),
                    () -> assertTrue(ispettoriPerServizio.stream().allMatch(ispettori -> ispettori <= 1)));
        }
    }

    @Nested
    @DisplayName("Menu senza i piatti adatti")
    class PiattiMancanti {

        @Test
        @DisplayName("un menu con soli piatti da 5 stelle non ha niente per il Commis")
        void nienteDaCucinarePerIlCommis() {
            Menu soloCinqueStelle = new Menu(List.of(new Piatto("Cassata Siciliana", 5)), new Random(1));
            assertThrows(IllegalStateException.class, () -> soloCinqueStelle.generaComandeDelServizio(Grado.COMMIS));
        }

        @Test
        @DisplayName("senza piatti da 4 stelle il Capo Partita non può ricevere il suo piatto sfida")
        void mancaIlPiattoSfida() {
            Menu finoA2Stelle = new Menu(List.of(new Piatto("Carbonara", 2)), new RandomSempreZero());
            assertThrows(IllegalStateException.class, () -> finoA2Stelle.generaComandeDelServizio(Grado.CAPO_PARTITA));
        }

        @Test
        @DisplayName("se la probabilità non scatta, lo stesso menu basta al Capo Partita")
        void senzaSfidaBasta() {
            Menu finoA2Stelle = new Menu(List.of(new Piatto("Carbonara", 2)), new RandomSempreIlMassimo());
            assertFalse(finoA2Stelle.generaComandeDelServizio(Grado.CAPO_PARTITA).isEmpty());
        }
    }
}
