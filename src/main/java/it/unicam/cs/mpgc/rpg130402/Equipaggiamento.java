package it.unicam.cs.mpgc.rpg130402;

public class Equipaggiamento {
    private String nomeOggetto;
    private int bonusVelocita;
    private int bonusMaestria;

    // Costruttore
    public Equipaggiamento(String nomeOggetto, int bonusVelocita, int bonusMaestria) {
        this.nomeOggetto = nomeOggetto;
        this.bonusVelocita = bonusVelocita;
        this.bonusMaestria = bonusMaestria;
    }

    public String getNomeOggetto() { return nomeOggetto; }
    public int getBonusVelocita() { return bonusVelocita; }
    public int getBonusMaestria() { return bonusMaestria; }
}