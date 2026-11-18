package it.unicam.cs.mpgc.rpg130402.model;

/**
 * La cucina del ristorante in cui lo chef fa carriera: il menu con i piatti che si possono ordinare
 * e l'attrezzatura con tutti gli attrezzi. Sono i dati fissi della {@link Partita}, che arrivano insieme dal catalogo.
 */
public record Cucina(Menu menu, Attrezzatura attrezzatura) {

    public Cucina {
        Validazione.verificaNonNullo(menu, "Il menu della cucina");
        Validazione.verificaNonNullo(attrezzatura, "L'attrezzatura della cucina");
    }
}
