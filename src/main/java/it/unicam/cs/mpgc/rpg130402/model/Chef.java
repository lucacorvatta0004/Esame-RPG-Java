package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;
import java.util.List;

public class Chef {
    private final String nome;
    private final String ruolo;
    private int livello;
    private int velocita;
    private int maestria;
    private int reputazione;
    private Equipaggiamento strumentoEquipaggiato;

    public Chef(String nome, String ruolo) {
        Validazione.verificaTestoNonVuoto(nome, "Il nome dello chef");
        Validazione.verificaTestoNonVuoto(ruolo, "Il ruolo dello chef");
        this.nome = nome;
        this.ruolo = ruolo;
        this.livello = 1;
        this.velocita = 10;
        this.maestria = 10;
        this.reputazione = 3;
    }

    public void equipaggia(Equipaggiamento oggetto) {
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

    public void mostraStatistiche() {
        System.out.println("---------------------------------");
        System.out.println("NOME: " + this.nome);
        System.out.println("RUOLO: " + this.ruolo);
        System.out.println("LIVELLO: " + this.livello);
        System.out.println("VELOCITA': " + this.velocita);
        System.out.println("MAESTRIA: " + getMaestriaTotale());
        System.out.println("REPUTAZIONE: " + this.reputazione + " vite");
        System.out.println("---------------------------------");
    }
    // getter per il salvataggio
    public String getNome() { return this.nome; }
    public String getRuolo() { return this.ruolo; }
    public int getLivello() { return this.livello; }
    public int getVelocita() { return this.velocita; }
    public int getMaestriaBase() { return this.maestria; } // la maestria senza armi
    public int getReputazione() { return this.reputazione; }

    // metodo per forzare le stats quando carico il file di salvataggio
    public void setStatisticheSalvate(int livello, int velocita, int maestria, int reputazione) {
        Validazione.verificaAlmeno(livello, 1, "Il livello");
        Validazione.verificaAlmeno(velocita, 0, "La velocità");
        Validazione.verificaAlmeno(maestria, 0, "La maestria");
        Validazione.verificaIntervallo(reputazione, 0, 3, "La reputazione");
        this.livello = livello;
        this.velocita = velocita;
        this.maestria = maestria;
        this.reputazione = reputazione;
    }

    /**
     * Prepara i piatti della comanda nell'ordine in cui sono stati ordinati.
     * Ogni piatto bruciato toglie un punto di reputazione; se lo chef viene licenziato
     * il servizio si interrompe e i piatti rimanenti non vengono preparati.
     */
    public EsitoServizio cucina(Comanda ordineTavolo) {
        int maestriaEffettiva = getMaestriaTotale();
        List<EsitoPiatto> esitiPiatti = new ArrayList<>();

        for (Piatto piatto : ordineTavolo.getPiattiOrdinati()) {
            boolean riuscito = maestriaEffettiva >= piatto.getMaestriaRichiesta();
            if (!riuscito) {
                this.reputazione--;
            }
            esitiPiatti.add(new EsitoPiatto(piatto, riuscito, this.reputazione));
            if (isLicenziato()) {
                break;
            }
        }

        return new EsitoServizio(esitiPiatti, isLicenziato());
    }

    public boolean isLicenziato() {
        return this.reputazione <= 0;
    }
}