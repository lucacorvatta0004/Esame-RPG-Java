package it.unicam.cs.mpgc.rpg130402.model;

public class Equipaggiamento {
    private String nomeOggetto;
    private int bonusVelocita;
    private int bonusMaestria;

    // Costruttore
    public Equipaggiamento(String nomeOggetto, int bonusVelocita, int bonusMaestria) {
        Validazione.verificaTestoNonVuoto(nomeOggetto, "Il nome dell'equipaggiamento");
        Validazione.verificaAlmeno(bonusVelocita, 0, "Il bonus velocità");
        Validazione.verificaAlmeno(bonusMaestria, 0, "Il bonus maestria");
        this.nomeOggetto = nomeOggetto;
        this.bonusVelocita = bonusVelocita;
        this.bonusMaestria = bonusMaestria;
    }

    public String getNomeOggetto() { return nomeOggetto; }
    public int getBonusVelocita() { return bonusVelocita; }
    public int getBonusMaestria() { return bonusMaestria; }
}