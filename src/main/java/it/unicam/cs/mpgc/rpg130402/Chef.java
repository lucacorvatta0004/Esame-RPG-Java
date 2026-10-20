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
    // L'azione per usare uno strumento
    public void equipaggia(Equipaggiamento oggetto) {
        this.velocita += oggetto.getBonusVelocita();
        this.maestria += oggetto.getBonusMaestria();
        System.out.println(this.nome + " ha equipaggiato: " + oggetto.getNomeOggetto() + "!");
    }

    // provo a preparare il piatto (è il nostro equivalente del combattimento)
    public void cucina(Comanda ordine) {
        // chiedo alla comanda quanta maestria serve
        int sfida = ordine.getMaestriaRichiesta();

        System.out.println("\n" + this.nome + " inizia a preparare " + ordine.getNomePiatto() + "...");

        // check sulle stats: se ne ho abbastanza vinco, altrimenti brucio tutto
        if (this.maestria >= sfida) {
            System.out.println("SUCCESSO! " + ordine.getNomePiatto() + " cucinato alla perfezione.");
        } else {
            System.out.println("DISASTRO! Piatto bruciato. Ho solo " + this.maestria + " di maestria, me ne serviva " + sfida + ".");
        }
    }
}