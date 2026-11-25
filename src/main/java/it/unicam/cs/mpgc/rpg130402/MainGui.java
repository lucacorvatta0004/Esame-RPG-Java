package it.unicam.cs.mpgc.rpg130402;

import it.unicam.cs.mpgc.rpg130402.gui.GiocoGui;
import javafx.application.Application;

/**
 * Avvia il gioco con la finestra JavaFX (la console resta in {@link Main}).
 * Il main sta in una classe che non estende Application: se JavaFX è sul classpath, come nel nostro progetto
 * senza module-info, Java si rifiuta di avviare una classe Application direttamente
 * ("JavaFX runtime components are missing").
 */
public class MainGui {

    public static void main(String[] args) {
        Application.launch(GiocoGui.class, args);
    }
}
