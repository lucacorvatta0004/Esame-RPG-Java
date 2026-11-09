package it.unicam.cs.mpgc.rpg130402.model;

/** Azione speciale dello Chef: il piatto firma riesce sempre, ma una sola volta per servizio. */
public class PiattoFirma extends AzioneSpeciale {
    private static final int USI_PER_SERVIZIO = 1;

    public PiattoFirma() {
        super(USI_PER_SERVIZIO);
    }

    @Override
    public String getNome() {
        return "Piatto firma";
    }

    @Override
    public String getDescrizione() {
        return "il piatto riesce sempre";
    }

    @Override
    protected Preparazione prepara(Piatto piatto, Statistiche statistiche) {
        return new Preparazione(true, statistiche);
    }
}
