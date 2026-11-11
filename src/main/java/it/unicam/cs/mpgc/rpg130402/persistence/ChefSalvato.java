package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Chef;

import java.util.Objects;

/**
 * Lo chef come è scritto nel file di salvataggio: solo dati, senza regole (un DTO).
 * Velocità e maestria sono quelle base, senza l'attrezzo. Il grado non c'è, perché si ricava dal livello.
 */
record ChefSalvato(String nome, int livello, int velocita, int maestria, int reputazione) {

    static ChefSalvato da(Chef chef) {
        Objects.requireNonNull(chef, "Lo chef da salvare non può essere null");
        return new ChefSalvato(chef.getNome(), chef.getLivello(), chef.getVelocita(),
                chef.getMaestriaBase(), chef.getReputazione());
    }

    /**
     * Ricrea lo chef passando dal costruttore e dalla validazione del model.
     *
     * @throws IllegalArgumentException se un valore non è valido, anche quando nel file manca un campo
     *                                  (Gson lo lascia a 0 o a null)
     */
    Chef creaChef() {
        Chef chef = new Chef(nome);
        chef.setStatisticheSalvate(livello, velocita, maestria, reputazione);
        return chef;
    }
}
