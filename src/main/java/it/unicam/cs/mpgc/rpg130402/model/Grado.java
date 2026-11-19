package it.unicam.cs.mpgc.rpg130402.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * I gradi della carriera dello chef, dal più basso al più alto.
 * Ogni grado si raggiunge a un livello minimo, sblocca le sue azioni e tiene quelle dei gradi precedenti.
 * Ogni grado ha anche un massimo di stelle per i piatti che riceve: al Commis arrivano solo piatti
 * che si riescono con la Cottura normale (2 stelle = 20 maestria, un Commis col Coltello in Ceramica ne ha 25:
 * è l'attrezzo che il Commis sblocca in attrezzi.json, quindi lo ha ogni chef nuovo).
 * Ogni grado dice quanti tavoli arrivano in un servizio: più si sale, più tavoli.
 * Infine ogni grado dice quanto è difficile un servizio (vedi {@link Menu#generaComandeDelServizio(Grado)}):
 * la percentuale di piatti sfida, cioè di una stella sopra il massimo (mai al Commis, che così non brucia
 * piatti con la Cottura normale; mai allo Chef, perché piatti oltre le 5 stelle non esistono), e la percentuale
 * di servizi in cui arriva l'ispettore Michelin in incognito (solo allo Chef: è la sua sfida).
 * Anche gli attrezzi li sblocca il grado (vedi {@link Attrezzo#isSbloccatoDa(Grado)}), con la stessa regola delle azioni.
 */
public enum Grado {
    //            nome, livello minimo, stelle massime, tavoli, % piatti sfida, % ispettore, azioni sbloccate
    COMMIS("Commis", 1, 2, 3, 0, 0, List.of(CotturaNormale::new, Improvvisazione::new)),
    CAPO_PARTITA("Capo Partita", 3, 3, 4, 10, 0, List.of(FuocoAlto::new)),
    SOUS_CHEF("Sous-Chef", 5, 4, 5, 10, 0, List.of(RicettaCollaudata::new)),
    CHEF("Chef", 7, 5, 6, 0, 25, List.of(PiattoFirma::new));

    private final String nome;
    private final int livelloMinimo;
    private final int stelleMassime;
    private final int tavoliPerServizio;
    private final int percentualePiattiSfida;
    private final int percentualeIspettore;
    private final List<Supplier<Azione>> azioniSbloccate;

    Grado(String nome, int livelloMinimo, int stelleMassime, int tavoliPerServizio,
          int percentualePiattiSfida, int percentualeIspettore, List<Supplier<Azione>> azioniSbloccate) {
        this.nome = nome;
        this.livelloMinimo = livelloMinimo;
        this.stelleMassime = stelleMassime;
        this.tavoliPerServizio = tavoliPerServizio;
        this.percentualePiattiSfida = percentualePiattiSfida;
        this.percentualeIspettore = percentualeIspettore;
        this.azioniSbloccate = azioniSbloccate;
    }

    /** Il grado più alto raggiunto al livello dato (almeno 1): così il grado non può contraddire il livello. */
    public static Grado perLivello(int livello) {
        Validazione.verificaAlmeno(livello, 1, "Il livello");
        return Arrays.stream(values())
                .filter(grado -> grado.livelloMinimo <= livello)
                .max(Comparator.naturalOrder())
                .orElseThrow();
    }

    public String getNome() {
        return nome;
    }

    /** Le stelle massime dei piatti normali che lo chef può ricevere a questo grado. */
    public int getStelleMassime() {
        return stelleMassime;
    }

    /** Quanti tavoli arrivano in un servizio dello chef che ha questo grado. */
    public int getTavoliPerServizio() {
        return tavoliPerServizio;
    }

    /** Su 100 piatti ordinati, quanti in media sono piatti sfida (da 0 a 100). */
    public int getPercentualePiattiSfida() {
        return percentualePiattiSfida;
    }

    /** Su 100 servizi, in quanti in media arriva l'ispettore Michelin (da 0 a 100). */
    public int getPercentualeIspettore() {
        return percentualeIspettore;
    }

    /** Il piatto è un piatto sfida per questo grado: ha più stelle di quelle massime del grado. */
    public boolean isPiattoSfida(Piatto piatto) {
        Validazione.verificaNonNullo(piatto, "Il piatto");
        return piatto.getDifficoltaStelle() > stelleMassime;
    }

    /**
     * Crea azioni nuove per un servizio: quelle di questo grado e di tutti i gradi precedenti,
     * in ordine di carriera. Le azioni speciali partono con tutti i loro usi.
     */
    public List<Azione> creaAzioniPerServizio() {
        return Arrays.stream(values())
                .filter(this::comprende)
                .flatMap(grado -> grado.azioniSbloccate.stream())
                .map(Supplier::get)
                .toList();
    }

    /** Questo grado comprende l'altro, cioè è lo stesso o uno più alto: quello che sblocca un grado resta nei successivi. */
    boolean comprende(Grado altroGrado) {
        return altroGrado.compareTo(this) <= 0;
    }
}
