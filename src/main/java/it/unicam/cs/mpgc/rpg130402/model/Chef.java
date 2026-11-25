package it.unicam.cs.mpgc.rpg130402.model;

public class Chef {
    // le vite con cui si inizia e a cui si torna quando si raggiunge un grado nuovo
    private static final int REPUTAZIONE_MASSIMA = 3;

    private final String nome;
    private Carriera carriera;
    private int reputazione;
    private Equipaggiamento equipaggiamento;

    /**
     * Crea un nuovo chef: la carriera parte dal livello 1, quindi dal grado di Commis.
     * Non ha ancora attrezzi: li riceve prima del primo servizio (vedi {@link Partita#scegliAttrezzi}).
     */
    public Chef(String nome) {
        Validazione.verificaTestoNonVuoto(nome, "Il nome dello chef");
        this.nome = nome;
        this.carriera = Carriera.INIZIALE;
        this.reputazione = REPUTAZIONE_MASSIMA;
        this.equipaggiamento = Equipaggiamento.VUOTO;
    }

    /** Le statistiche con cui lo chef cucina adesso: quelle base del livello più i bonus di tutti gli attrezzi. */
    public Statistiche getStatisticheEffettive() {
        return getStatisticheCon(this.equipaggiamento);
    }

    /**
     * Le statistiche che lo chef avrebbe con gli attrezzi dati: quelle base del livello più i loro bonus.
     * Non cambia gli attrezzi dello chef: serve per mostrare una scelta prima di confermarla.
     */
    public Statistiche getStatisticheCon(Equipaggiamento equipaggiamento) {
        Validazione.verificaNonNullo(equipaggiamento, "Gli attrezzi con cui calcolare le statistiche");
        return equipaggiamento.applicaA(this.carriera.statisticheBase());
    }

    public String getNome() { return this.nome; }
    public Carriera getCarriera() { return this.carriera; }
    public Grado getGrado() { return this.carriera.grado(); }
    public int getReputazione() { return this.reputazione; }
    public Equipaggiamento getEquipaggiamento() { return this.equipaggiamento; }

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
     * Ripristina gli attrezzi letti da un salvataggio: va chiamato dopo {@link #setProgressiSalvati}, perché
     * controlla la carriera ripristinata. Ogni attrezzo deve essere di un grado che lo chef ha già raggiunto;
     * può essere di un grado perso con una retrocessione, perché quelli si tolgono solo prima del servizio successivo.
     *
     * @throws IllegalArgumentException se un attrezzo è di un grado che lo chef non ha mai raggiunto
     */
    public void setAttrezziSalvati(Equipaggiamento equipaggiamento) {
        Validazione.verificaNonNullo(equipaggiamento, "Gli attrezzi salvati");
        Grado gradoMassimo = this.carriera.gradoMassimoRaggiunto();
        if (!equipaggiamento.isSbloccatoDa(gradoMassimo)) {
            throw new IllegalArgumentException("Gli attrezzi salvati devono essere sbloccati da un grado già raggiunto ("
                    + gradoMassimo.getNome() + ")");
        }
        usaAttrezzi(equipaggiamento);
    }

    /** Gli attrezzi da usare nel prossimo servizio: li sceglie la {@link Partita}, che li prende tra quelli sbloccati. */
    void usaAttrezzi(Equipaggiamento equipaggiamento) {
        Validazione.verificaNonNullo(equipaggiamento, "Gli attrezzi dello chef");
        this.equipaggiamento = equipaggiamento;
    }

    /**
     * Prepara un piatto con l'azione scelta. Se il piatto brucia toglie una vita (vedi {@link #perdiReputazione()}).
     * Poi aggiunge o toglie l'esperienza del piatto (vedi {@link Carriera}).
     * Il tempo di preparazione si calcola dalla velocità usata dall'azione.
     * Visibile solo nel model: da fuori si cucina giocando un servizio della {@link Partita};
     * il {@link Servizio} accetta solo le azioni sbloccate dal grado e ancora disponibili.
     */
    EsitoPiatto cucina(Piatto piatto, Azione azione) {
        Validazione.verificaNonNullo(piatto, "Il piatto da cucinare");
        Validazione.verificaNonNullo(azione, "L'azione con cui cucinare");

        Preparazione preparazione = azione.esegui(piatto, getStatisticheEffettive());
        if (!preparazione.riuscito()) {
            perdiReputazione();
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

    /**
     * Toglie una vita: succede quando un piatto brucia e quando un tavolo se ne va perché ha aspettato troppo.
     * Visibile solo nel model, come {@link #cucina}.
     *
     * @throws IllegalStateException se lo chef è già licenziato: la reputazione non scende sotto 0
     */
    void perdiReputazione() {
        if (isLicenziato()) {
            throw new IllegalStateException("Lo chef " + this.nome + " è già stato licenziato");
        }
        this.reputazione--;
    }

    public boolean isLicenziato() {
        return this.reputazione <= 0;
    }
}
