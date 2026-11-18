package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Attrezzo;
import it.unicam.cs.mpgc.rpg130402.model.Carriera;
import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;
import it.unicam.cs.mpgc.rpg130402.model.Grado;

import java.util.List;
import java.util.Objects;

/**
 * Lo chef come è scritto nel file di salvataggio: solo dati, senza regole (un DTO).
 * L'esperienza è quella dentro il livello. Grado, velocità e maestria non ci sono, perché si ricavano dal livello;
 * il grado più alto raggiunto invece c'è, scritto con il nome della costante (per esempio "CAPO_PARTITA").
 * Degli attrezzi in uso c'è solo il nome: i bonus restano nel catalogo, che è l'unica fonte dei dati degli attrezzi.
 */
record ChefSalvato(String nome, int livello, int esperienza, String gradoMassimoRaggiunto, int reputazione,
                   List<String> attrezzi) {

    static ChefSalvato da(Chef chef) {
        Objects.requireNonNull(chef, "Lo chef da salvare non può essere null");
        Carriera carriera = chef.getCarriera();
        List<String> nomiAttrezzi = chef.getEquipaggiamento().getAttrezzi().stream()
                .map(Attrezzo::getNome)
                .toList();
        return new ChefSalvato(chef.getNome(), carriera.livello(), carriera.esperienza(),
                carriera.gradoMassimoRaggiunto().name(), chef.getReputazione(), nomiAttrezzi);
    }

    /**
     * Ricrea lo chef passando dal costruttore e dalla validazione del model; gli attrezzi si cercano per nome nel catalogo.
     * Un salvataggio di prima della sessione 6a si carica lo stesso: senza esperienza Gson la lascia a 0
     * (inizio del livello), senza grado più alto si usa il grado del livello salvato,
     * e la velocità e la maestria scritte nel file si ignorano.
     * Un salvataggio di prima della sessione 6c, senza attrezzi, ricrea lo chef senza attrezzi:
     * come uno chef nuovo, li riceve prima del servizio.
     *
     * @throws IllegalArgumentException se un valore non è valido, anche quando nel file manca un campo
     *                                  obbligatorio (Gson lo lascia a 0 o a null), o se un attrezzo non è nel catalogo
     */
    Chef creaChef(Catalogo catalogo) {
        Chef chef = new Chef(nome);
        chef.setProgressiSalvati(new Carriera(livello, esperienza, leggiGradoMassimo()), reputazione);
        chef.setAttrezziSalvati(leggiAttrezzi(catalogo));
        return chef;
    }

    private Grado leggiGradoMassimo() {
        if (gradoMassimoRaggiunto == null) {
            return Grado.perLivello(livello);
        }
        return LetturaCostanti.perNome(Grado.class, gradoMassimoRaggiunto, "Il grado più alto raggiunto");
    }

    private Equipaggiamento leggiAttrezzi(Catalogo catalogo) {
        if (attrezzi == null) {
            return Equipaggiamento.VUOTO;
        }
        return new Equipaggiamento(attrezzi.stream()
                .map(nomeAttrezzo -> cercaNelCatalogo(catalogo, nomeAttrezzo))
                .toList());
    }

    private static Attrezzo cercaNelCatalogo(Catalogo catalogo, String nomeAttrezzo) {
        return catalogo.cercaAttrezzo(nomeAttrezzo)
                .orElseThrow(() -> new IllegalArgumentException("L'attrezzo " + nomeAttrezzo + " non è nel catalogo"));
    }
}
