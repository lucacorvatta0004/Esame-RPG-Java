package it.unicam.cs.mpgc.rpg130402.persistence;

import it.unicam.cs.mpgc.rpg130402.model.Piatto;

/** Un piatto come è scritto in piatti.json: solo dati, senza regole (un DTO). */
record VocePiatto(String nome, int stelle) {

    /**
     * Crea il piatto passando dal costruttore e dalla validazione del model.
     *
     * @throws IllegalArgumentException se un valore non è valido, anche quando nel file manca un campo
     *                                  (Gson lo lascia a 0 o a null)
     */
    Piatto creaPiatto() {
        return new Piatto(nome, stelle);
    }
}
