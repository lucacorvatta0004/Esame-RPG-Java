package it.unicam.cs.mpgc.rpg130402.model;

/** Azione base: il piatto riesce se la maestria dello chef basta. */
public class CotturaNormale implements Azione {

    @Override
    public String getNome() {
        return "Cottura normale";
    }

    @Override
    public String getDescrizione() {
        return "riesce se la tua maestria arriva a quella richiesta dal piatto";
    }

    @Override
    public Preparazione esegui(Piatto piatto, Statistiche statistiche) {
        return Preparazione.secondoMaestria(piatto, statistiche);
    }
}
