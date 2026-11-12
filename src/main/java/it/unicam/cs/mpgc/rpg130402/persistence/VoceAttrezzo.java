package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Equipaggiamento;

/** Un attrezzo come è scritto in attrezzi.json: solo dati, senza regole (un DTO). */
record VoceAttrezzo(String nome, int bonusVelocita, int bonusMaestria) {

    /**
     * Crea l'attrezzo passando dal costruttore e dalla validazione del model.
     *
     * @throws IllegalArgumentException se un valore non è valido, anche quando nel file manca un campo
     *                                  (Gson lo lascia a 0 o a null)
     */
    Equipaggiamento creaEquipaggiamento() {
        return new Equipaggiamento(nome, bonusVelocita, bonusMaestria);
    }
}
