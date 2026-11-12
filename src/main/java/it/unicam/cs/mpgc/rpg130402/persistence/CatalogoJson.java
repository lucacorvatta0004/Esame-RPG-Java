package it.unicam.cs.mpgc.rpg130402.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;
import it.unicam.cs.mpgc.rpg130402.model.Piatto;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Il catalogo letto con Gson dai file JSON (in UTF-8) tra le risorse del gioco, cioè in src/main/resources/dati.
 * Ogni voce dei file è un DTO che diventa un oggetto del model passando dal suo costruttore, che la valida.
 */
public class CatalogoJson implements Catalogo {
    private static final String FILE_PIATTI = "dati/piatti.json";
    private static final String FILE_ATTREZZI = "dati/attrezzi.json";

    private final Gson gson = new Gson();
    private final List<Piatto> piatti;
    private final Map<String, Equipaggiamento> attrezziPerNome;

    /**
     * Legge subito i due file, così un dato rovinato si scopre all'avvio e non a metà partita.
     *
     * @throws CatalogoException se un file manca, è rovinato, è vuoto o ha una voce non valida
     */
    public CatalogoJson() {
        this.piatti = leggi(FILE_PIATTI, VocePiatto[].class, VocePiatto::creaPiatto);
        this.attrezziPerNome = perNome(leggi(FILE_ATTREZZI, VoceAttrezzo[].class, VoceAttrezzo::creaEquipaggiamento));
    }

    @Override
    public List<Piatto> getPiatti() {
        return piatti;
    }

    @Override
    public Equipaggiamento getAttrezzo(String nome) {
        Equipaggiamento attrezzo = attrezziPerNome.get(nome);
        if (attrezzo == null) {
            throw new CatalogoException("nel file " + FILE_ATTREZZI + " non c'è l'attrezzo " + nome);
        }
        return attrezzo;
    }

    /**
     * Legge dal file risorsa una lista di voci di tipo V e trasforma ogni voce in un oggetto T del model
     * con creaOggetto. Restituisce una lista non modificabile, nello stesso ordine del file.
     */
    private <V, T> List<T> leggi(String risorsa, Class<V[]> tipoVoci, Function<V, T> creaOggetto) {
        V[] voci = leggiVoci(risorsa, tipoVoci);
        List<T> oggetti = new ArrayList<>();

        for (int i = 0; i < voci.length; i++) {
            String voce = "nel file " + risorsa + " la voce " + (i + 1);
            if (voci[i] == null) {
                // Gson legge una virgola finale, come in [ {...}, ], come un elemento null
                throw new CatalogoException(voce + " è vuota: forse c'è una virgola di troppo");
            }
            try {
                oggetti.add(creaOggetto.apply(voci[i]));
            } catch (IllegalArgumentException e) {
                throw new CatalogoException(voce + " non è valida: " + e.getMessage(), e);
            }
        }

        return List.copyOf(oggetti);
    }

    private <V> V[] leggiVoci(String risorsa, Class<V[]> tipoVoci) {
        try (InputStream flusso = apriRisorsa(risorsa);
             Reader lettore = new InputStreamReader(flusso, StandardCharsets.UTF_8)) {
            V[] voci = gson.fromJson(lettore, tipoVoci);
            if (voci == null || voci.length == 0) {
                throw new CatalogoException("il file " + risorsa + " non contiene nessuna voce");
            }
            return voci;
        } catch (JsonParseException e) {
            throw new CatalogoException("il file " + risorsa + " è rovinato: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new CatalogoException("impossibile leggere il file " + risorsa, e);
        }
    }

    /** Le risorse si cercano nel classpath (nel JAR o in build/resources), non nella cartella da cui si avvia. */
    private static InputStream apriRisorsa(String risorsa) {
        InputStream flusso = CatalogoJson.class.getClassLoader().getResourceAsStream(risorsa);
        if (flusso == null) {
            throw new CatalogoException("manca il file " + risorsa + " tra le risorse del gioco");
        }
        return flusso;
    }

    /** Il nome è la chiave con cui si cerca un attrezzo, quindi deve essere unico. */
    private static Map<String, Equipaggiamento> perNome(List<Equipaggiamento> attrezzi) {
        Map<String, Equipaggiamento> attrezziPerNome = new HashMap<>();
        for (Equipaggiamento attrezzo : attrezzi) {
            if (attrezziPerNome.putIfAbsent(attrezzo.getNomeOggetto(), attrezzo) != null) {
                throw new CatalogoException("nel file " + FILE_ATTREZZI + " l'attrezzo "
                        + attrezzo.getNomeOggetto() + " compare due volte");
            }
        }
        return attrezziPerNome;
    }
}
