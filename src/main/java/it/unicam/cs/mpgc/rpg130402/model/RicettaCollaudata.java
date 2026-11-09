package it.unicam.cs.mpgc.rpg130402.model;

/** Azione speciale del Sous-Chef: una ricetta provata tante volte dà più maestria sul piatto. */
public class RicettaCollaudata extends AzioneSpeciale {
    private static final int USI_PER_SERVIZIO = 2;
    private static final int BONUS_MAESTRIA = 20;

    public RicettaCollaudata() {
        super(USI_PER_SERVIZIO);
    }

    @Override
    public String getNome() {
        return "Ricetta collaudata";
    }

    @Override
    public String getDescrizione() {
        return "+" + BONUS_MAESTRIA + " maestria su questo piatto";
    }

    @Override
    protected Preparazione prepara(Piatto piatto, Statistiche statistiche) {
        Statistiche conRicetta = statistiche.conBonus(BONUS_MAESTRIA, 0);
        return Preparazione.secondoMaestria(piatto, conRicetta);
    }
}
