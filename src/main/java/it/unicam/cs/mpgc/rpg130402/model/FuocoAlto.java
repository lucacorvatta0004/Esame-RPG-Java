package it.unicam.cs.mpgc.rpg130402.model;

/** Azione speciale del Capo Partita: si cucina più in fretta, ma è più facile bruciare il piatto. */
public class FuocoAlto extends AzioneSpeciale {
    private static final int USI_PER_SERVIZIO = 3;
    private static final int PERDITA_MAESTRIA = 10;
    private static final int GUADAGNO_VELOCITA = 10;

    public FuocoAlto() {
        super(USI_PER_SERVIZIO);
    }

    @Override
    public String getNome() {
        return "Fuoco alto";
    }

    @Override
    protected Preparazione prepara(Piatto piatto, Statistiche statistiche) {
        Statistiche aFuocoAlto = statistiche.conBonus(-PERDITA_MAESTRIA, GUADAGNO_VELOCITA);
        return Preparazione.secondoMaestria(piatto, aFuocoAlto);
    }
}
