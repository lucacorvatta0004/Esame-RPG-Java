package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Attrezzo;
import it.unicam.cs.mpgc.rpg130402.model.Carriera;
import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;
import it.unicam.cs.mpgc.rpg130402.model.Grado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Il salvataggio in JSON. Ogni test lavora in una cartella temporanea nuova (@TempDir), creata da JUnit
 * prima del test e cancellata dopo: così non si tocca il vero salvataggio.json e i test non si disturbano.
 */
@DisplayName("RepositoryPartitaJson: salvare e ricaricare la partita")
class RepositoryPartitaJsonTest {
    @TempDir
    Path cartella;

    private Path file;
    private Catalogo catalogo;
    private RepositoryPartita repository;

    @BeforeEach
    void preparaRepository() {
        file = cartella.resolve("salvataggio.json");
        catalogo = new CatalogoJson();
        repository = new RepositoryPartitaJson(file, catalogo);
    }

    @Test
    @DisplayName("uno chef salvato e ricaricato è uguale: nome, livello, esperienza, grado più alto, vite e attrezzi")
    void salvaERicarica() throws SalvataggioException {
        // un Capo Partita (livello 4) che era già arrivato a Sous-Chef: può ancora avere il Forno a Vapore
        Chef chef = new Chef("Mario D'Angelo");
        chef.setProgressiSalvati(new Carriera(4, 120, Grado.SOUS_CHEF), 2);
        chef.setAttrezziSalvati(new Equipaggiamento(List.of(attrezzo("Coltello in Ceramica"),
                attrezzo("Padella in Rame"), attrezzo("Forno a Vapore"))));

        repository.salva(chef);
        Chef ricaricato = repository.carica().orElseThrow();

        assertAll(
                () -> assertEquals("Mario D'Angelo", ricaricato.getNome()),
                () -> assertEquals(4, ricaricato.getCarriera().livello()),
                () -> assertEquals(120, ricaricato.getCarriera().esperienza()),
                () -> assertEquals(Grado.SOUS_CHEF, ricaricato.getCarriera().gradoMassimoRaggiunto()),
                () -> assertEquals(2, ricaricato.getReputazione()),
                () -> assertEquals(chef.getEquipaggiamento().getAttrezzi(), ricaricato.getEquipaggiamento().getAttrezzi()),
                () -> assertEquals(chef.getStatisticheEffettive(), ricaricato.getStatisticheEffettive()));
    }

    @Test
    @DisplayName("se il file non c'è, non c'è nessun salvataggio: Optional vuoto")
    void fileMancante() throws SalvataggioException {
        assertTrue(repository.carica().isEmpty());
    }

    @ParameterizedTest(name = "file rovinato: \"{0}\"")
    @ValueSource(strings = {"", "{ \"nome\": \"Mario\", \"livello\": ", "questo non è un salvataggio"})
    @DisplayName("un file vuoto, troncato o che non è JSON dà una SalvataggioException, non un crash")
    void fileRovinato(String contenuto) throws IOException {
        Files.writeString(file, contenuto);
        assertThrows(SalvataggioException.class, () -> repository.carica());
    }

    private Attrezzo attrezzo(String nome) {
        return catalogo.cercaAttrezzo(nome).orElseThrow();
    }
}
