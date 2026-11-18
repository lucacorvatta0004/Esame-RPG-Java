package it.unicam.cs.mpgc.rpg130402.model;

/**
 * I tipi di attrezzo da cucina. Lo chef usa al massimo un attrezzo per tipo (vedi {@link Equipaggiamento}).
 * Un tipo nuovo è una costante qui più i suoi attrezzi in attrezzi.json.
 */
public enum TipoAttrezzo {
    COLTELLO("Coltello"),
    PADELLA("Padella"),
    FORNO("Forno");

    private final String nome;

    TipoAttrezzo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
