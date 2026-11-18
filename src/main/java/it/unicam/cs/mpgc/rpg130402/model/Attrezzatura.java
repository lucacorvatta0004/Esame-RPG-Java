package it.unicam.cs.mpgc.rpg130402.model;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tutti gli attrezzi della cucina, ricevuti da fuori (per esempio quelli del catalogo): l'attrezzatura
 * non sa da dove arrivano. Lo chef usa solo quelli sbloccati dal suo grado, al massimo uno per tipo.
 */
public class Attrezzatura {
    private final List<Attrezzo> attrezzi;

    /**
     * La lista viene copiata, quindi cambiarla dopo non cambia l'attrezzatura.
     *
     * @throws IllegalArgumentException se la lista è null, è vuota o contiene null
     */
    public Attrezzatura(List<Attrezzo> attrezzi) {
        Validazione.verificaListaNonVuota(attrezzi, "La lista degli attrezzi della cucina");
        this.attrezzi = List.copyOf(attrezzi);
    }

    /**
     * Gli attrezzi sbloccati dal grado, divisi per tipo: una lista per ogni tipo che ne ha almeno uno,
     * nell'ordine dei tipi. Dentro ogni lista gli attrezzi restano nell'ordine ricevuto.
     */
    List<List<Attrezzo>> sbloccatiPerTipo(Grado grado) {
        return List.copyOf(attrezzi.stream()
                .filter(attrezzo -> attrezzo.isSbloccatoDa(grado))
                .collect(Collectors.groupingBy(Attrezzo::getTipo,
                        () -> new EnumMap<>(TipoAttrezzo.class),
                        Collectors.toList()))
                .values());
    }
}
