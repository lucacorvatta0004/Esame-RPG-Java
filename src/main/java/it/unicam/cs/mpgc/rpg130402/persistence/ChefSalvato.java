package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Carriera;
import it.unicam.cs.mpgc.rpg130402.model.Chef;
import it.unicam.cs.mpgc.rpg130402.model.Grado;

import java.util.Objects;

/**
 * Lo chef come è scritto nel file di salvataggio: solo dati, senza regole (un DTO).
 * L'esperienza è quella dentro il livello. Grado, velocità e maestria non ci sono, perché si ricavano dal livello;
 * il grado più alto raggiunto invece c'è, scritto con il nome della costante (per esempio "CAPO_PARTITA").
 */
record ChefSalvato(String nome, int livello, int esperienza, String gradoMassimoRaggiunto, int reputazione) {

    static ChefSalvato da(Chef chef) {
        Objects.requireNonNull(chef, "Lo chef da salvare non può essere null");
        Carriera carriera = chef.getCarriera();
        return new ChefSalvato(chef.getNome(), carriera.livello(), carriera.esperienza(),
                carriera.gradoMassimoRaggiunto().name(), chef.getReputazione());
    }

    /**
     * Ricrea lo chef passando dal costruttore e dalla validazione del model.
     * Un salvataggio di prima della sessione 6a si carica lo stesso: senza esperienza Gson la lascia a 0
     * (inizio del livello), senza grado più alto si usa il grado del livello salvato,
     * e la velocità e la maestria scritte nel file si ignorano.
     *
     * @throws IllegalArgumentException se un valore non è valido, anche quando nel file manca un campo
     *                                  obbligatorio (Gson lo lascia a 0 o a null)
     */
    Chef creaChef() {
        Chef chef = new Chef(nome);
        chef.setProgressiSalvati(new Carriera(livello, esperienza, leggiGradoMassimo()), reputazione);
        return chef;
    }

    private Grado leggiGradoMassimo() {
        if (gradoMassimoRaggiunto == null) {
            return Grado.perLivello(livello);
        }
        try {
            return Grado.valueOf(gradoMassimoRaggiunto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Il grado più alto raggiunto " + gradoMassimoRaggiunto + " non esiste", e);
        }
    }
}
