package it.unicam.cs.mpgc.rpg130402.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Progresso: come cambia la carriera dopo un piatto")
class ProgressoTest {
    private Carriera commisQuasiCapoPartita;
    private Carriera capoPartitaAppenaPromosso;

    @BeforeEach
    void preparaCarriere() {
        commisQuasiCapoPartita = new Carriera(2, 190, Grado.COMMIS);
        capoPartitaAppenaPromosso = new Carriera(3, 5, Grado.CAPO_PARTITA);
    }

    private static Progresso progresso(Carriera prima, int esperienza) {
        return new Progresso(esperienza, prima, prima.conEsperienza(esperienza));
    }

    @Test
    @DisplayName("le carriere prima e dopo non possono essere null")
    void carriereNullRifiutate() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Progresso(10, null, Carriera.INIZIALE)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Progresso(10, Carriera.INIZIALE, null)));
    }

    @Test
    @DisplayName("restando nel livello non si sale, non si scende, nessuna promozione")
    void stessoLivello() {
        Progresso progresso = progresso(Carriera.INIZIALE, 20);
        assertAll(
                () -> assertEquals(20, progresso.esperienzaGuadagnata()),
                () -> assertFalse(progresso.isSalitoDiLivello()),
                () -> assertFalse(progresso.isScesoDiLivello()),
                () -> assertFalse(progresso.isPromosso()),
                () -> assertFalse(progresso.isRetrocesso()),
                () -> assertFalse(progresso.isNuovoGradoRaggiunto()));
    }

    @Test
    @DisplayName("salire dal livello 1 al 2 non è una promozione: si resta Commis")
    void salitoSenzaPromozione() {
        Progresso progresso = progresso(new Carriera(1, 90, Grado.COMMIS), 20);
        assertAll(
                () -> assertTrue(progresso.isSalitoDiLivello()),
                () -> assertFalse(progresso.isPromosso()),
                () -> assertFalse(progresso.isNuovoGradoRaggiunto()));
    }

    @Test
    @DisplayName("la prima volta a Capo Partita: salito, promosso e grado nuovo")
    void primaPromozione() {
        Progresso progresso = progresso(commisQuasiCapoPartita, 10);
        assertAll(
                () -> assertTrue(progresso.isSalitoDiLivello()),
                () -> assertTrue(progresso.isPromosso()),
                () -> assertTrue(progresso.isNuovoGradoRaggiunto()));
    }

    @Test
    @DisplayName("tornando Commis: sceso e retrocesso, nessun grado nuovo")
    void retrocessione() {
        Progresso progresso = progresso(capoPartitaAppenaPromosso, -25);
        assertAll(
                () -> assertTrue(progresso.isScesoDiLivello()),
                () -> assertTrue(progresso.isRetrocesso()),
                () -> assertFalse(progresso.isPromosso()),
                () -> assertFalse(progresso.isNuovoGradoRaggiunto()));
    }

    @Test
    @DisplayName("tornare a Capo Partita dopo una retrocessione è una promozione, ma il grado non è nuovo")
    void ritornoAUnGradoGiaRaggiunto() {
        Carriera retrocesso = capoPartitaAppenaPromosso.conEsperienza(-25);
        Progresso progresso = progresso(retrocesso, 30);
        assertAll(
                () -> assertTrue(progresso.isPromosso()),
                () -> assertFalse(progresso.isNuovoGradoRaggiunto()));
    }
}
