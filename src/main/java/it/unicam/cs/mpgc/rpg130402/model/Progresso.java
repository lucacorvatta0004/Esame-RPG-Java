package it.unicam.cs.mpgc.rpg130402.model;

/**
 * Come è cambiata la carriera dello chef dopo un piatto: l'esperienza guadagnata secondo la regola
 * (negativa se persa) e la carriera prima e dopo. Da qui si sa se lo chef è salito o sceso
 * di livello, se è stato promosso o retrocesso e se ha raggiunto un grado nuovo.
 */
public record Progresso(int esperienzaGuadagnata, Carriera prima, Carriera dopo) {

    public Progresso {
        Validazione.verificaNonNullo(prima, "La carriera prima del piatto");
        Validazione.verificaNonNullo(dopo, "La carriera dopo il piatto");
    }

    public boolean isSalitoDiLivello() {
        return dopo.livello() > prima.livello();
    }

    public boolean isScesoDiLivello() {
        return dopo.livello() < prima.livello();
    }

    public boolean isPromosso() {
        return dopo.grado().compareTo(prima.grado()) > 0;
    }

    public boolean isRetrocesso() {
        return dopo.grado().compareTo(prima.grado()) < 0;
    }

    /**
     * Lo chef è arrivato a un grado mai raggiunto prima. Solo in questo caso le vite tornano al massimo:
     * tornare a un grado già raggiunto (dopo una retrocessione) è una promozione, ma non ridà le vite.
     */
    public boolean isNuovoGradoRaggiunto() {
        return dopo.gradoMassimoRaggiunto().compareTo(prima.gradoMassimoRaggiunto()) > 0;
    }
}
