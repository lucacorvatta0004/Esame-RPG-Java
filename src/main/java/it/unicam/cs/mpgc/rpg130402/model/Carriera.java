package it.unicam.cs.mpgc.rpg130402.model;

/**
 * La carriera dello chef: il livello, l'esperienza dentro quel livello e il grado più alto mai raggiunto.
 * Qui stanno tutte le regole della progressione: quanta esperienza vale un piatto, quanta ne serve
 * per salire e quali statistiche base dà ogni livello. È immutabile: quando l'esperienza cambia
 * ne nasce una nuova (vedi {@link #conEsperienza(int)}).
 */
public record Carriera(int livello, int esperienza, Grado gradoMassimoRaggiunto) {
    private static final int LIVELLO_INIZIALE = 1;
    private static final int ESPERIENZA_PER_STELLA_RIUSCITA = 10;
    private static final int ESPERIENZA_PER_STELLA_BRUCIATA = 5;
    // dal livello N al livello N+1 servono N × 100 punti di esperienza
    private static final int ESPERIENZA_PER_LIVELLO = 100;
    private static final int MAESTRIA_INIZIALE = 10;
    private static final int VELOCITA_INIZIALE = 10;
    private static final int MAESTRIA_PER_LIVELLO = 5;
    private static final int VELOCITA_PER_LIVELLO = 2;

    /** La carriera di un nuovo chef: livello 1, nessuna esperienza, il primo grado. */
    public static final Carriera INIZIALE =
            new Carriera(LIVELLO_INIZIALE, 0, Grado.perLivello(LIVELLO_INIZIALE));

    /**
     * Il livello deve essere almeno 1 e l'esperienza da 0 a quella che serve per salire, esclusa.
     * Il grado più alto raggiunto non può essere sotto il grado del livello attuale.
     */
    public Carriera {
        Validazione.verificaAlmeno(livello, LIVELLO_INIZIALE, "Il livello");
        Validazione.verificaIntervallo(esperienza, 0, esperienzaPerSalireDa(livello) - 1,
                "L'esperienza del livello " + livello);
        Validazione.verificaNonNullo(gradoMassimoRaggiunto, "Il grado più alto raggiunto");
        if (gradoMassimoRaggiunto.compareTo(Grado.perLivello(livello)) < 0) {
            throw new IllegalArgumentException("Il grado più alto raggiunto (" + gradoMassimoRaggiunto.getNome()
                    + ") non può essere sotto il grado del livello " + livello);
        }
    }

    /** L'esperienza che vale un piatto: stelle × 10 se è riuscito, −(stelle × 5) se è bruciato. */
    static int esperienzaPer(Piatto piatto, boolean riuscito) {
        int stelle = piatto.getDifficoltaStelle();
        return riuscito ? stelle * ESPERIENZA_PER_STELLA_RIUSCITA : -stelle * ESPERIENZA_PER_STELLA_BRUCIATA;
    }

    /**
     * Restituisce la carriera dopo aver aggiunto la variazione di esperienza (negativa = esperienza persa).
     * Se l'esperienza arriva a quella che serve, si sale di livello e l'avanzo resta;
     * se va sotto zero si scende di livello, e al livello 1 l'esperienza si ferma a 0.
     * Il grado più alto raggiunto non scende mai.
     */
    public Carriera conEsperienza(int variazione) {
        int nuovoLivello = livello;
        int nuovaEsperienza = esperienza + variazione;
        while (nuovaEsperienza >= esperienzaPerSalireDa(nuovoLivello)) {
            nuovaEsperienza -= esperienzaPerSalireDa(nuovoLivello);
            nuovoLivello++;
        }
        while (nuovaEsperienza < 0 && nuovoLivello > LIVELLO_INIZIALE) {
            nuovoLivello--;
            nuovaEsperienza += esperienzaPerSalireDa(nuovoLivello);
        }
        Grado nuovoGrado = Grado.perLivello(nuovoLivello);
        return new Carriera(nuovoLivello, Math.max(nuovaEsperienza, 0), piuAlto(gradoMassimoRaggiunto, nuovoGrado));
    }

    /** L'esperienza che serve per passare al livello successivo. */
    public int esperienzaPerSalire() {
        return esperienzaPerSalireDa(livello);
    }

    public Grado grado() {
        return Grado.perLivello(livello);
    }

    /** Maestria e velocità dello chef senza attrezzo: crescono a ogni livello, quindi si ricavano dal livello. */
    public Statistiche statisticheBase() {
        int livelliSaliti = livello - LIVELLO_INIZIALE;
        return new Statistiche(MAESTRIA_INIZIALE + livelliSaliti * MAESTRIA_PER_LIVELLO,
                VELOCITA_INIZIALE + livelliSaliti * VELOCITA_PER_LIVELLO);
    }

    private static int esperienzaPerSalireDa(int livello) {
        return livello * ESPERIENZA_PER_LIVELLO;
    }

    private static Grado piuAlto(Grado primo, Grado secondo) {
        return primo.compareTo(secondo) >= 0 ? primo : secondo;
    }
}
