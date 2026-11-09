package it.unicam.cs.mpgc.rpg130402.model;

public class Chef {
    private final String nome;
    private int livello;
    private int velocita;
    private int maestria;
    private int reputazione;
    private Equipaggiamento strumentoEquipaggiato;

    /** Crea un nuovo chef: la carriera parte dal livello 1, quindi dal grado di Commis. */
    public Chef(String nome) {
        Validazione.verificaTestoNonVuoto(nome, "Il nome dello chef");
        this.nome = nome;
        this.livello = 1;
        this.velocita = 10;
        this.maestria = 10;
        this.reputazione = 3;
    }

    public void equipaggia(Equipaggiamento oggetto) {
        Validazione.verificaNonNullo(oggetto, "L'equipaggiamento da equipaggiare");
        this.strumentoEquipaggiato = oggetto;
        System.out.println(this.nome + " ha equipaggiato: " + oggetto.getNomeOggetto() + "!");
    }

    public int getMaestriaTotale() {
        if (this.strumentoEquipaggiato != null) {
            return this.maestria + this.strumentoEquipaggiato.getBonusMaestria();
        } else {
            return this.maestria;
        }
    }

    public int getVelocitaTotale() {
        if (this.strumentoEquipaggiato != null) {
            return this.velocita + this.strumentoEquipaggiato.getBonusVelocita();
        } else {
            return this.velocita;
        }
    }

    /** Le statistiche con cui lo chef cucina adesso: quelle base più i bonus dell'attrezzo. */
    public Statistiche getStatisticheEffettive() {
        return new Statistiche(getMaestriaTotale(), getVelocitaTotale());
    }

    public void mostraStatistiche() {
        System.out.println("---------------------------------");
        System.out.println("NOME: " + this.nome);
        System.out.println("GRADO: " + getGrado().getNome());
        System.out.println("LIVELLO: " + this.livello);
        System.out.println("VELOCITA': " + getVelocitaTotale());
        System.out.println("MAESTRIA: " + getMaestriaTotale());
        System.out.println("REPUTAZIONE: " + this.reputazione + " vite");
        System.out.println("---------------------------------");
    }

    public String getNome() { return this.nome; }
    public Grado getGrado() { return Grado.perLivello(this.livello); }
    public int getLivello() { return this.livello; }
    public int getVelocita() { return this.velocita; }
    public int getMaestriaBase() { return this.maestria; } // senza il bonus dell'equipaggiamento
    public int getReputazione() { return this.reputazione; }

    /**
     * Ripristina le statistiche lette da un salvataggio.
     * La velocità deve essere almeno 1, perché da lei dipende il tempo di preparazione.
     * La reputazione deve essere almeno 1: con 0 lo chef è licenziato (game over)
     * e una partita finita non si può caricare.
     */
    public void setStatisticheSalvate(int livello, int velocita, int maestria, int reputazione) {
        Validazione.verificaAlmeno(livello, 1, "Il livello");
        Validazione.verificaAlmeno(velocita, 1, "La velocità");
        Validazione.verificaAlmeno(maestria, 0, "La maestria");
        Validazione.verificaIntervallo(reputazione, 1, 3, "La reputazione salvata");
        this.livello = livello;
        this.velocita = velocita;
        this.maestria = maestria;
        this.reputazione = reputazione;
    }

    /**
     * Prepara un piatto con l'azione scelta. Se il piatto brucia toglie un punto di reputazione.
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
        int minutiPreparazione = piatto.minutiDiPreparazione(preparazione.statisticheUsate().velocita());
        return new EsitoPiatto(piatto, preparazione, minutiPreparazione, this.reputazione);
    }

    public boolean isLicenziato() {
        return this.reputazione <= 0;
    }
}
