package it.unicam.cs.mpgc.rpg130402.model;

import java.util.ArrayList;

public class Chef {
    private String nome;
    private String ruolo;
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

    public void cucina(Comanda ordineTavolo) {
        System.out.println("\n" + this.nome + " inizia a preparare il Tavolo " + ordineTavolo.getNumeroTavolo() + "...");

        ArrayList<Piatto> piatti = ordineTavolo.getPiattiOrdinati();
        int maestriaEffettiva = getMaestriaTotale();

        // counter per i piatti andati a buon fine
        int piattiConsegnati = 0;

        // scorro la lista dei piatti ordinati
        for (int i = 0; i < piatti.size(); i++) {
            Piatto piattoCorrente = piatti.get(i);
            int sfida = piattoCorrente.getMaestriaRichiesta();

            System.out.println("\n-> Preparo: " + piattoCorrente.getNomePiatto());

            // check maestria vs difficolta'
            if (maestriaEffettiva >= sfida) {
                System.out.println("SUCCESSO! Piatto riuscito.");
                piattiConsegnati = piattiConsegnati + 1; // aggiorno il counter
            } else {
                System.out.println("DISASTRO! Piatto bruciato. (Serve " + sfida + " maestria, hai " + maestriaEffettiva + ")");
                this.reputazione = this.reputazione - 1;
                System.out.println("Attenzione! Reputazione scesa a: " + this.reputazione);

                // se perdo tutte le vite blocco il gioco
                if (this.reputazione <= 0) {
                    System.out.println("\nGAME OVER! Hai bruciato troppi piatti, sei stato licenziato.");
                    System.exit(0); // <-- chiude istantaneamente l'intero programma Java!
                }
            }
        }

        System.out.println("\nServizio per il Tavolo " + ordineTavolo.getNumeroTavolo() + " completato!");

        // feedback finale del tavolo a fine ciclo
        if (piattiConsegnati == piatti.size()) {
            System.out.println("I clienti sono entusiasti! Hanno ricevuto tutti i piatti in modo perfetto.");
        } else {
            System.out.println("I clienti sono FURIOSI! Hanno ricevuto solo " + piattiConsegnati + " piatti su " + piatti.size() + " e lasciano una recensione pessima!");
        }
    }
}