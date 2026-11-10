package it.unicam.cs.mpgc.rpg130402.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * I gradi della carriera dello chef, dal più basso al più alto.
 * Ogni grado si raggiunge a un livello minimo, sblocca le sue azioni e tiene quelle dei gradi precedenti.
 * Ogni grado ha anche un massimo di stelle per i piatti che riceve: al Commis arrivano solo piatti
 * che si riescono con la Cottura normale (2 stelle = 20 maestria, un Commis col coltello ne ha 25).
 */
public enum Grado {
    COMMIS("Commis", 1, 2, List.of(CotturaNormale::new, Improvvisazione::new)),
    CAPO_PARTITA("Capo Partita", 3, 3, List.of(FuocoAlto::new)),
    SOUS_CHEF("Sous-Chef", 5, 4, List.of(RicettaCollaudata::new)),
    CHEF("Chef", 7, 5, List.of(PiattoFirma::new));

    private final String nome;
    private final int livelloMinimo;
    private final int stelleMassime;
    private final List<Supplier<Azione>> azioniSbloccate;

    Grado(String nome, int livelloMinimo, int stelleMassime, List<Supplier<Azione>> azioniSbloccate) {
        this.nome = nome;
        this.livelloMinimo = livelloMinimo;
        this.stelleMassime = stelleMassime;
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

    /** Le stelle massime dei piatti che lo chef può ricevere a questo grado. */
    public int getStelleMassime() {
        return stelleMassime;
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

    private boolean comprende(Grado altroGrado) {
        return altroGrado.compareTo(this) <= 0;
    }
}
