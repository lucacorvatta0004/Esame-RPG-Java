package it.unicam.cs.mpgc.rpg130402.model;

import java.util.Objects;

/**
 * Un attrezzo da cucina: il suo tipo, il grado che lo sblocca e i bonus che aggiunge alle statistiche dello chef.
 * Il nome identifica l'attrezzo (è la chiave nel catalogo e nel salvataggio): due attrezzi con lo stesso nome
 * sono lo stesso attrezzo, anche se in memoria sono due oggetti diversi (vedi {@link #equals(Object)}).
 */
public final class Attrezzo {
    private final String nome;
    private final TipoAttrezzo tipo;
    private final Grado gradoMinimo;
    private final int bonusVelocita;
    private final int bonusMaestria;

    /** @throws IllegalArgumentException se il nome è vuoto, il tipo o il grado sono null o un bonus è negativo */
    public Attrezzo(String nome, TipoAttrezzo tipo, Grado gradoMinimo, int bonusVelocita, int bonusMaestria) {
        Validazione.verificaTestoNonVuoto(nome, "Il nome dell'attrezzo");
        Validazione.verificaNonNullo(tipo, "Il tipo dell'attrezzo");
        Validazione.verificaNonNullo(gradoMinimo, "Il grado che sblocca l'attrezzo");
        Validazione.verificaAlmeno(bonusVelocita, 0, "Il bonus velocità");
        Validazione.verificaAlmeno(bonusMaestria, 0, "Il bonus maestria");
        this.nome = nome;
        this.tipo = tipo;
        this.gradoMinimo = gradoMinimo;
        this.bonusVelocita = bonusVelocita;
        this.bonusMaestria = bonusMaestria;
    }

    public String getNome() { return nome; }
    public TipoAttrezzo getTipo() { return tipo; }
    public Grado getGradoMinimo() { return gradoMinimo; }
    public int getBonusVelocita() { return bonusVelocita; }
    public int getBonusMaestria() { return bonusMaestria; }

    /** Lo chef con il grado dato può usare l'attrezzo: il grado è quello che lo sblocca o uno più alto. */
    public boolean isSbloccatoDa(Grado grado) {
        Validazione.verificaNonNullo(grado, "Il grado dello chef");
        return grado.comprende(gradoMinimo);
    }

    /** Due attrezzi sono lo stesso attrezzo se hanno lo stesso nome, il dato che li rende unici. */
    @Override
    public boolean equals(Object oggetto) {
        if (this == oggetto) {
            return true;
        }
        if (!(oggetto instanceof Attrezzo altro)) {
            return false;
        }
        return nome.equals(altro.nome);
    }

    /** Usa lo stesso campo di equals: due attrezzi uguali hanno lo stesso hashCode. */
    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
