package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Attrezzo;
import it.unicam.cs.mpgc.rpg130402.model.Grado;
import it.unicam.cs.mpgc.rpg130402.model.TipoAttrezzo;

/**
 * Un attrezzo come è scritto in attrezzi.json: solo dati, senza regole (un DTO).
 * Il tipo e il grado che lo sblocca sono scritti con il nome della costante (per esempio "COLTELLO" e "COMMIS").
 */
record VoceAttrezzo(String nome, String tipo, String gradoMinimo, int bonusVelocita, int bonusMaestria) {

    /**
     * Crea l'attrezzo passando dal costruttore e dalla validazione del model.
     *
     * @throws IllegalArgumentException se un valore non è valido, anche quando nel file manca un campo
     *                                  (Gson lo lascia a 0 o a null)
     */
    Attrezzo creaAttrezzo() {
        return new Attrezzo(nome,
                LetturaCostanti.perNome(TipoAttrezzo.class, tipo, "Il tipo dell'attrezzo"),
                LetturaCostanti.perNome(Grado.class, gradoMinimo, "Il grado che sblocca l'attrezzo"),
                bonusVelocita, bonusMaestria);
    }
}
