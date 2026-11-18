package it.unicam.cs.mpgc.rpg130402.ui;

import it.unicam.cs.mpgc.rpg130402.model.Carriera;
import it.unicam.cs.mpgc.rpg130402.model.EsitoPiatto;
import it.unicam.cs.mpgc.rpg130402.model.OsservatoreServizio;
import it.unicam.cs.mpgc.rpg130402.model.Progresso;
import it.unicam.cs.mpgc.rpg130402.model.Tavolo;
import it.unicam.cs.mpgc.rpg130402.model.Testo;

/** Racconta il servizio nel terminale, mentre succede. */
public class OsservatoreServizioDaConsole implements OsservatoreServizio {

    @Override
    public void tavoloArrivato(Tavolo tavolo) {
        System.out.println("\nNuovo tavolo! Il Tavolo " + tavolo.getNumero() + " si siede al minuto "
                + tavolo.getMinutoArrivo() + " e aspetta fino al minuto " + tavolo.getMinutoLimite() + ".");
        tavolo.getComanda().stampaScontrino();
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
