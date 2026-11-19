package it.unicam.cs.mpgc.rpg130402.model;

import java.util.List;

/**
 * La carriera dello chef giocata un servizio dopo l'altro, finché il giocatore vuole o finché la carriera
 * non finisce: con il licenziamento, oppure con il giudizio dell'ispettore Michelin (vittoria o game over,
 * vedi {@link StatoCarriera}). Ogni servizio si prepara con il grado che lo chef ha all'inizio del servizio:
 * quali attrezzi può usare e quali comande arrivano (vedi {@link Menu#generaComandeDelServizio(Grado)}).
 * Per questo una promozione o una retrocessione cambiano attrezzi e mosse dal servizio successivo.
 * Chi sceglie e chi guarda (il giocatore, la console, la GUI) si passa a ogni chiamata, come per un osservatore.
 */
public class Partita {
    private final Chef chef;
    private final Cucina cucina;
    private boolean attrezziScelti;
    private StatoCarriera statoCarriera;

    public Partita(Chef chef, Cucina cucina) {
        Validazione.verificaNonNullo(chef, "Lo chef della partita");
        Validazione.verificaNonNullo(cucina, "La cucina della partita");
        this.chef = chef;
        this.cucina = cucina;
        this.statoCarriera = StatoCarriera.IN_CORSO;
    }

    /**
     * Prepara gli attrezzi del prossimo servizio con il grado che lo chef ha adesso. Per ogni tipo con attrezzi
     * sbloccati lo chef usa l'unico che c'è, oppure quello scelto dal giocatore se ce n'è più di uno.
     * Gli attrezzi non sbloccati si tolgono, anche quelli di un grado perso con una retrocessione.
     * Va chiamato prima di ogni servizio (vedi {@link #giocaServizio}).
     *
     * @throws IllegalStateException se la carriera è già finita,
     *                               o se la scelta restituisce un attrezzo che non è tra quelli proposti
     */
    public void scegliAttrezzi(SceltaAttrezzi sceltaAttrezzi) {
        verificaCarrieraInCorso();
        Validazione.verificaNonNullo(sceltaAttrezzi, "La scelta degli attrezzi");
        List<Attrezzo> attrezzi = cucina.attrezzatura().sbloccatiPerTipo(chef.getGrado()).stream()
                .map(attrezziDelTipo -> scegliAttrezzo(attrezziDelTipo, sceltaAttrezzi))
                .toList();
        chef.usaAttrezzi(new Equipaggiamento(attrezzi));
        attrezziScelti = true;
    }

    /**
     * Gioca un servizio intero con tavoli nuovi: il giocatore sceglie l'azione per ogni piatto e l'osservatore
     * viene informato di ogni evento mentre accade. Restituisce come è andato il servizio,
     * anche se si è fermato perché la carriera è finita; dopo un esito con la carriera finita non si gioca più.
     *
     * @throws IllegalStateException se la carriera è già finita, se gli attrezzi di questo servizio
     *                               non sono ancora stati scelti (vedi {@link #scegliAttrezzi}) o se la scelta
     *                               restituisce un'azione che non è tra quelle disponibili
     */
    public EsitoServizio giocaServizio(SceltaAzione sceltaAzione, OsservatoreServizio osservatore) {
        verificaCarrieraInCorso();
        if (!attrezziScelti) {
            throw new IllegalStateException("Prima di ogni servizio vanno scelti gli attrezzi");
        }
        attrezziScelti = false;
        List<Comanda> comande = cucina.menu().generaComandeDelServizio(chef.getGrado());
        EsitoServizio esito = new Servizio(chef, sceltaAzione, comande).gioca(osservatore);
        statoCarriera = esito.statoCarriera();
        return esito;
    }

    private void verificaCarrieraInCorso() {
        if (statoCarriera.isFinita()) {
            throw new IllegalStateException("La carriera di " + chef.getNome() + " è finita ("
                    + statoCarriera.name() + ")");
        }
    }

    /** L'unico attrezzo sbloccato del tipo, oppure quello che il giocatore sceglie tra quelli proposti. */
    private Attrezzo scegliAttrezzo(List<Attrezzo> attrezziDelTipo, SceltaAttrezzi sceltaAttrezzi) {
        if (attrezziDelTipo.size() == 1) {
            return attrezziDelTipo.getFirst();
        }
        Attrezzo scelto = sceltaAttrezzi.scegli(attrezziDelTipo, chef.getEquipaggiamento());
        if (!attrezziDelTipo.contains(scelto)) {
            throw new IllegalStateException("L'attrezzo scelto deve essere uno di quelli proposti");
        }
        return scelto;
    }
}
