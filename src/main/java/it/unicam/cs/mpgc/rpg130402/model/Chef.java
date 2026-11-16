package it.unicam.cs.mpgc.rpg130402.model;

public class Chef {
    // le vite con cui si inizia e a cui si torna quando si raggiunge un grado nuovo
    private static final int REPUTAZIONE_MASSIMA = 3;

    private final String nome;
    private Carriera carriera;
    private int reputazione;
    private Equipaggiamento strumentoEquipaggiato;

    /** Crea un nuovo chef: la carriera parte dal livello 1, quindi dal grado di Commis. */
    public Chef(String nome) {
        Validazione.verificaTestoNonVuoto(nome, "Il nome dello chef");
        this.nome = nome;
        this.carriera = Carriera.INIZIALE;
        this.reputazione = REPUTAZIONE_MASSIMA;
    }

    public void equipaggia(Equipaggiamento oggetto) {
        Validazione.verificaNonNullo(oggetto, "L'equipaggiamento da equipaggiare");
        this.strumentoEquipaggiato = oggetto;
        System.out.println(this.nome + " ha equipaggiato: " + oggetto.getNomeOggetto() + "!");
    }

    /** Le statistiche con cui lo chef cucina adesso: quelle base del livello più i bonus dell'attrezzo. */
    public Statistiche getStatisticheEffettive() {
        Statistiche base = this.carriera.statisticheBase();
        if (this.strumentoEquipaggiato == null) {
            return base;
        }
        return base.conBonus(this.strumentoEquipaggiato.getBonusMaestria(), this.strumentoEquipaggiato.getBonusVelocita());
    }

    public void mostraStatistiche() {
        Statistiche effettive = getStatisticheEffettive();
        System.out.println("---------------------------------");
        System.out.println("NOME: " + this.nome);
        System.out.println("GRADO: " + getGrado().getNome());
        System.out.println("LIVELLO: " + this.carriera.livello());
        System.out.println("ESPERIENZA: " + this.carriera.esperienza() + "/" + this.carriera.esperienzaPerSalire());
        System.out.println("VELOCITÀ: " + effettive.velocita());
        System.out.println("MAESTRIA: " + effettive.maestria());
        System.out.println("REPUTAZIONE: " + Testo.conNumero(this.reputazione, "vita", "vite"));
        System.out.println("---------------------------------");
    }

    public String getNome() { return this.nome; }
    public Carriera getCarriera() { return this.carriera; }
    public Grado getGrado() { return this.carriera.grado(); }
    public int getReputazione() { return this.reputazione; }

    /**
     * Ripristina i progressi letti da un salvataggio. La carriera è già valida (la controlla il suo costruttore).
     * La reputazione deve essere da 1 a 3: con 0 lo chef è licenziato (game over)
     * e una partita finita non si può caricare.
     */
    public void setProgressiSalvati(Carriera carriera, int reputazione) {
        Validazione.verificaNonNullo(carriera, "La carriera salvata");
        Validazione.verificaIntervallo(reputazione, 1, REPUTAZIONE_MASSIMA, "La reputazione salvata");
        this.carriera = carriera;
        this.reputazione = reputazione;
    }

    /**
     * Prepara un piatto con l'azione scelta. Se il piatto brucia toglie un punto di reputazione.
     * Poi aggiunge o toglie l'esperienza del piatto (vedi {@link Carriera}).
     * Il tempo di preparazione si calcola dalla velocità usata dall'azione.
     * Visibile solo nel model: da fuori si cucina con un {@link Servizio}, che accetta
     * solo le azioni sbloccate dal grado e ancora disponibili.
     */
    EsitoPiatto cucina(Piatto piatto, Azione azione) {
        Validazione.verificaNonNullo(piatto, "Il piatto da cucinare");
        Validazione.verificaNonNullo(azione, "L'azione con cui cucinare");

        Preparazione preparazione = azione.esegui(piatto, getStatisticheEffettive());
        if (!preparazione.riuscito()) {
            this.reputazione--;
        }
        Progresso progresso = aggiungiEsperienza(Carriera.esperienzaPer(piatto, preparazione.riuscito()));
        int minutiPreparazione = piatto.minutiDiPreparazione(preparazione.statisticheUsate().velocita());
        return new EsitoPiatto(piatto, preparazione, minutiPreparazione, progresso, this.reputazione);
    }

    /** Cambia l'esperienza (anche in negativo); se lo chef arriva a un grado mai raggiunto, le vite tornano al massimo. */
    private Progresso aggiungiEsperienza(int variazione) {
        Carriera prima = this.carriera;
        this.carriera = prima.conEsperienza(variazione);
        Progresso progresso = new Progresso(variazione, prima, this.carriera);
        if (progresso.isNuovoGradoRaggiunto()) {
            this.reputazione = REPUTAZIONE_MASSIMA;
        }
        return progresso;
    }

    public boolean isLicenziato() {
        return this.reputazione <= 0;
    }
}
