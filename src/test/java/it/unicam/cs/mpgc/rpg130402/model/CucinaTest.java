package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Cucina: menu e attrezzatura della partita")
class CucinaTest {
    private Menu menu;
    private Attrezzatura attrezzatura;

    @BeforeEach
    void preparaCucina() {
        menu = new Menu(List.of(new Piatto("Carbonara", 2)));
        attrezzatura = new Attrezzatura(AttrezziDelGioco.tutti());
    }

    @Test
    @DisplayName("menu e attrezzatura non possono essere null")
    void parteNullRifiutata() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Cucina(null, attrezzatura)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Cucina(menu, null)));
    }

    @Test
    @DisplayName("la cucina tiene il menu e l'attrezzatura ricevuti")
    void tieneLeDueParti() {
        Cucina cucina = new Cucina(menu, attrezzatura);
        assertAll(
                () -> assertSame(menu, cucina.menu()),
                () -> assertSame(attrezzatura, cucina.attrezzatura()));
    }
}
