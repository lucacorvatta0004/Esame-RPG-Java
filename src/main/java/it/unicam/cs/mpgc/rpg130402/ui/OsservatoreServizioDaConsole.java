package it.unicam.cs.mpgc.rpg130402.ui;

import it.unicam.cs.mpgc.rpg130402.model.Carriera;
import it.unicam.cs.mpgc.rpg130402.model.EsitoPiatto;
import it.unicam.cs.mpgc.rpg130402.model.Grado;
import it.unicam.cs.mpgc.rpg130402.model.OsservatoreServizio;
import it.unicam.cs.mpgc.rpg130402.model.Piatto;
import it.unicam.cs.mpgc.rpg130402.model.Progresso;
import it.unicam.cs.mpgc.rpg130402.model.Tavolo;

import java.util.Objects;

/** Racconta un servizio nel terminale, mentre succede. Per ogni servizio se ne crea uno nuovo. */
public class OsservatoreServizioDaConsole implements OsservatoreServizio {
    private final Grado gradoDelServizio;

    /**
     * Il grado è quello con cui lo chef inizia il servizio: serve a segnare i piatti sfida nelle ordinazioni,
     * perché le comande si preparano con quel grado anche se a metà servizio lo chef cambia grado.
     */
    public OsservatoreServizioDaConsole(Grado gradoDelServizio) {
        this.gradoDelServizio = Objects.requireNonNull(gradoDelServizio, "Il grado del servizio non può essere null");
    }

    @Override
    public void tavoloArrivato(Tavolo tavolo) {
        System.out.println("\nNuovo tavolo! Il Tavolo " + tavolo.getNumero() + " si siede al minuto "
                + tavolo.getMinutoArrivo() + " e aspetta fino al minuto " + tavolo.getMinutoLimite() + ".");
        System.out.println("\n=== ORDINE TAVOLO " + tavolo.getNumero() + " ===");
        tavolo.getComanda().getPiattiOrdinati().forEach(this::mostraPiattoOrdinato);
    }

    private void mostraPiattoOrdinato(Piatto piatto) {
        String riga = "Piatto: " + piatto.getNomePiatto() + " | Difficoltà: "
                + Testo.conNumero(piatto.getDifficoltaStelle(), "stella", "stelle")
                + " (Richiede " + piatto.getMaestriaRichiesta() + " maestria)";
        if (gradoDelServizio.isPiattoSfida(piatto)) {
            riga += " -> PIATTO SFIDA: una stella sopra il tuo grado!";
        }
        System.out.println(riga);
    }

    @Override
    public void piattoDaPreparare(Tavolo tavolo, int minuto) {
        System.out.println("\n[Minuto " + minuto + "] Tavolo " + tavolo.getNumero()
                + ", piatto " + (tavolo.getPiattiPreparati() + 1) + " di " + tavolo.getNumeroPiatti()
                + " (aspetta fino al minuto " + tavolo.getMinutoLimite() + ")");
    }

    @Override
    public void piattoPreparato(EsitoPiatto esitoPiatto) {
        System.out.println("Tempo di preparazione: " + Testo.conNumero(esitoPiatto.minutiPreparazione(), "minuto", "minuti"));

        if (esitoPiatto.riuscito()) {
            System.out.println("SUCCESSO! Piatto riuscito.");
        } else {
            System.out.println("DISASTRO! Piatto bruciato (" + esitoPiatto.motivo() + ").");
            mostraReputazione(esitoPiatto.reputazioneDopo());
        }
        mostraProgresso(esitoPiatto);
    }

    @Override
    public void tavoloServito(Tavolo tavolo, int minuto) {
        System.out.println("[Minuto " + minuto + "] Tavolo " + tavolo.getNumero() + " servito!");
        if (tavolo.piattiBruciati() == 0) {
            System.out.println("I clienti sono entusiasti! Hanno ricevuto tutti i piatti in modo perfetto.");
        } else {
            System.out.println("I clienti sono FURIOSI! Hanno ricevuto solo " + Testo.conNumero(tavolo.piattiRiusciti(), "piatto", "piatti")
                    + " su " + tavolo.getNumeroPiatti() + " e lasciano una recensione pessima!");
        }
    }

    @Override
    public void tavoloAndatoVia(Tavolo tavolo, int reputazioneDopo) {
        System.out.println("\nIl Tavolo " + tavolo.getNumero() + " ha aspettato troppo (era seduto dal minuto "
                + tavolo.getMinutoArrivo() + ", limite minuto " + tavolo.getMinutoLimite() + ") e se ne va senza "
                + Testo.conNumero(tavolo.piattiMancanti(), "piatto", "piatti") + ".");
        mostraReputazione(reputazioneDopo);
    }

    private void mostraReputazione(int reputazione) {
        System.out.println("Attenzione! Reputazione scesa a: " + reputazione);
    }

    private void mostraProgresso(EsitoPiatto esitoPiatto) {
        Progresso progresso = esitoPiatto.progresso();
        Carriera carriera = progresso.dopo();
        System.out.println("Esperienza: " + Testo.conSegno(progresso.esperienzaGuadagnata())
                + " (" + carriera.esperienza() + "/" + carriera.esperienzaPerSalire()
                + " per salire al livello " + (carriera.livello() + 1) + ")");

        if (progresso.isSalitoDiLivello()) {
            System.out.println("Sei salito al livello " + carriera.livello() + "!");
        }
        if (progresso.isScesoDiLivello()) {
            System.out.println("Sei sceso al livello " + carriera.livello() + ".");
        }
        if (progresso.isPromosso()) {
            mostraPromozione(esitoPiatto);
        }
        if (progresso.isRetrocesso()) {
            System.out.println("Retrocesso a " + carriera.grado().getNome() + ". Dal prossimo servizio non hai più "
                    + "le mosse e gli attrezzi da " + progresso.prima().grado().getNome() + ".");
        }
    }

    /** Le vite tornano solo se il grado è nuovo: tornare a un grado già raggiunto non le ridà. */
    private void mostraPromozione(EsitoPiatto esitoPiatto) {
        Progresso progresso = esitoPiatto.progresso();
        String messaggio = "Promosso a " + progresso.dopo().grado().getNome() + "!";
        if (progresso.isNuovoGradoRaggiunto()) {
            messaggio += " Vite di nuovo a " + esitoPiatto.reputazioneDopo() + ".";
        }
        System.out.println(messaggio);
        System.out.println("Le mosse e gli attrezzi nuovi arrivano dal prossimo servizio.");
    }
}
