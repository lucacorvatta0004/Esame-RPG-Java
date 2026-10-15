package it.unicam.cs.mpgc.rpg130402;

public class Chef {

    // anagrafica e ruolo scelto all'inizio
    private String nome;
    private String ruolo;

    // stats che crescono giocando
    private int livello;
    private int velocita;
    private int maestria; // se è alta, sbaglio meno piatti

    // setup iniziale quando creo il pg
    public Chef(String nome, String ruolo) {
        this.nome = nome;
        this.ruolo = ruolo;

        // si parte dal basso
        this.livello = 1;
        this.velocita = 10;
        this.maestria = 10;
    }

    // da chiamare quando si finisce uno stage
    public void saliDiLivello() {
        this.livello++;
        this.velocita += 5; // TODO: bilanciare se 5 è troppo
        this.maestria += 5;

        System.out.println("\n*** LEVEL UP! " + this.nome + " e' ora di livello " + this.livello + "! ***");
    }

    // debug per vedere se le stats si aggiornano bene
    public void mostraStatistiche() {
        System.out.println("---------------------------------");
        System.out.println("NOME: " + this.nome);
        System.out.println("RUOLO: " + this.ruolo);
        System.out.println("LIVELLO: " + this.livello);
        System.out.println("VELOCITA': " + this.velocita);
        System.out.println("MAESTRIA: " + this.maestria);
        System.out.println("---------------------------------");
    }
}