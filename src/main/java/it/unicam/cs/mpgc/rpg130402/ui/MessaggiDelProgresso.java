package it.unicam.cs.mpgc.rpg130402.ui;

import it.unicam.cs.mpgc.rpg130402.model.Carriera;
import it.unicam.cs.mpgc.rpg130402.model.EsitoPiatto;
import it.unicam.cs.mpgc.rpg130402.model.Progresso;

import java.util.ArrayList;
import java.util.List;

/**
 * I messaggi su come è cambiata la carriera dopo un piatto: livello, promozione e retrocessione.
 * Sono scritti in un solo punto, così la console e la GUI dicono le stesse cose con le stesse parole.
 */
public final class MessaggiDelProgresso {

    private MessaggiDelProgresso() {
    }

    /** Le righe da mostrare dopo il piatto, in ordine: nessuna se livello e grado non sono cambiati. */
    public static List<String> di(EsitoPiatto esitoPiatto) {
        Progresso progresso = esitoPiatto.progresso();
        Carriera carriera = progresso.dopo();
        List<String> righe = new ArrayList<>();
        if (progresso.isSalitoDiLivello()) {
            righe.add("Sei salito al livello " + carriera.livello() + "!");
        }
        if (progresso.isScesoDiLivello()) {
            righe.add("Sei sceso al livello " + carriera.livello() + ".");
        }
        if (progresso.isPromosso()) {
            righe.add(promozione(esitoPiatto));
            righe.add("Le mosse e gli attrezzi nuovi arrivano dal prossimo servizio.");
        }
        if (progresso.isRetrocesso()) {
            righe.add("Retrocesso a " + carriera.grado().getNome() + ". Dal prossimo servizio non hai più "
                    + "le mosse e gli attrezzi da " + progresso.prima().grado().getNome() + ".");
        }
        return righe;
    }

    /** Le vite tornano solo se il grado è nuovo: tornare a un grado già raggiunto non le ridà. */
    private static String promozione(EsitoPiatto esitoPiatto) {
        Progresso progresso = esitoPiatto.progresso();
        String messaggio = "Promosso a " + progresso.dopo().grado().getNome() + "!";
        if (progresso.isNuovoGradoRaggiunto()) {
            messaggio += " Vite di nuovo a " + esitoPiatto.reputazioneDopo() + ".";
        }
        return messaggio;
    }
}
