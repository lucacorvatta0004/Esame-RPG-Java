package it.unicam.cs.mpgc.rpg130402.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg130402.model.Chef;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Salva la partita in un file JSON (in UTF-8) con Gson.
 * Nel file c'è un {@link ChefSalvato}; lo chef si ricrea sempre passando dal model, che lo valida.
 */
public class RepositoryPartitaJson implements RepositoryPartita {
    private final Path percorsoFile;
    // indentato per leggerlo a occhio; senza l'escape HTML un apostrofo resta un apostrofo (D'Angelo, non D'Angelo)
    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /** Il file può anche non esistere ancora: lo crea il primo salvataggio. */
    public RepositoryPartitaJson(Path percorsoFile) {
        this.percorsoFile = Objects.requireNonNull(percorsoFile, "Il percorso del salvataggio non può essere null");
    }

    @Override
    public void salva(Chef chef) throws SalvataggioException {
        ChefSalvato chefSalvato = ChefSalvato.da(chef);
        try (Writer scrittore = Files.newBufferedWriter(percorsoFile)) {
            gson.toJson(chefSalvato, scrittore);
        } catch (IOException | JsonIOException e) {
            throw new SalvataggioException("impossibile scrivere il file " + percorsoFile, e);
        }
    }

    @Override
    public Optional<Chef> carica() throws SalvataggioException {
        if (Files.notExists(percorsoFile)) {
            return Optional.empty();
        }
        return Optional.of(creaChef(leggiFile()));
    }

    @Override
    public void elimina() throws SalvataggioException {
        try {
            Files.deleteIfExists(percorsoFile);
        } catch (IOException e) {
            throw new SalvataggioException("impossibile cancellare il file " + percorsoFile, e);
        }
    }

    private ChefSalvato leggiFile() throws SalvataggioException {
        try (Reader lettore = Files.newBufferedReader(percorsoFile)) {
            ChefSalvato chefSalvato = gson.fromJson(lettore, ChefSalvato.class);
            if (chefSalvato == null) {
                throw new SalvataggioException("il file " + percorsoFile + " è vuoto");
            }
            return chefSalvato;
        } catch (JsonParseException e) {
            throw new SalvataggioException("il file " + percorsoFile + " è rovinato", e);
        } catch (IOException e) {
            throw new SalvataggioException("impossibile leggere il file " + percorsoFile, e);
        }
    }

    private Chef creaChef(ChefSalvato chefSalvato) throws SalvataggioException {
        try {
            return chefSalvato.creaChef();
        } catch (IllegalArgumentException e) {
            throw new SalvataggioException(e.getMessage(), e);
        }
    }
}
