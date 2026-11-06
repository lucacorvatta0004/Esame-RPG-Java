package it.unicam.cs.mpgc.rpg130402.model;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * I gradi della carriera dello chef, dal più basso al più alto.
 * Ogni grado sblocca le sue azioni e tiene quelle dei gradi precedenti.
 */
public enum Grado {
    COMMIS("Commis", List.of(CotturaNormale::new, Improvvisazione::new)),
    CAPO_PARTITA("Capo Partita", List.of(FuocoAlto::new)),
    SOUS_CHEF("Sous-Chef", List.of(RicettaCollaudata::new)),
    CHEF("Chef", List.of(PiattoFirma::new));

    private final String nome;
    private final List<Supplier<Azione>> azioniSbloccate;

    Grado(String nome, List<Supplier<Azione>> azioniSbloccate) {
        this.nome = nome;
        this.azioniSbloccate = azioniSbloccate;
    }

    public String getNome() {
        return nome;
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
