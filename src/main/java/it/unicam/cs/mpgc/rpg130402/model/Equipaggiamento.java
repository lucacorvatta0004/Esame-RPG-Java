package it.unicam.cs.mpgc.rpg130402.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

/**
 * Gli attrezzi che lo chef usa: al massimo uno per tipo, con i bonus di tutti che si sommano.
 * È immutabile: per cambiare attrezzi se ne crea uno nuovo (lo fa la {@link Partita} prima di ogni servizio).
 */
public final class Equipaggiamento {
    /** Nessun attrezzo: per esempio lo chef nuovo, prima del suo primo servizio. */
    public static final Equipaggiamento VUOTO = new Equipaggiamento(List.of());

    // la chiave è il tipo e una mappa non ha mai due volte la stessa chiave: così c'è un solo attrezzo per tipo
    private final Map<TipoAttrezzo, Attrezzo> attrezziPerTipo = new EnumMap<>(TipoAttrezzo.class);

    /** @throws IllegalArgumentException se la lista è null, contiene null o ha due attrezzi dello stesso tipo */
    public Equipaggiamento(List<Attrezzo> attrezzi) {
        Validazione.verificaSenzaElementiNull(attrezzi, "La lista degli attrezzi");
        for (Attrezzo attrezzo : attrezzi) {
            if (attrezziPerTipo.putIfAbsent(attrezzo.getTipo(), attrezzo) != null) {
                throw new IllegalArgumentException("Lo chef può usare un solo attrezzo di tipo "
                        + attrezzo.getTipo().getNome());
            }
        }
    }

    /** Gli attrezzi in uso, nell'ordine dei tipi: una lista non modificabile, vuota se non ce n'è nessuno. */
    public List<Attrezzo> getAttrezzi() {
        return List.copyOf(attrezziPerTipo.values());
    }

    /** L'attrezzo è quello in uso per il suo tipo. */
    public boolean contiene(Attrezzo attrezzo) {
        Validazione.verificaNonNullo(attrezzo, "L'attrezzo da cercare");
        return attrezzo.equals(attrezziPerTipo.get(attrezzo.getTipo()));
    }

    /** Le statistiche ricevute più i bonus di tutti gli attrezzi in uso. */
    public Statistiche applicaA(Statistiche statistiche) {
        return statistiche.conBonus(somma(Attrezzo::getBonusMaestria), somma(Attrezzo::getBonusVelocita));
    }

    /** Ogni attrezzo in uso è sbloccato dal grado dato (vale anche quando non ce n'è nessuno). */
    boolean isSbloccatoDa(Grado grado) {
        return attrezziPerTipo.values().stream()
                .allMatch(attrezzo -> attrezzo.isSbloccatoDa(grado));
    }

    private int somma(ToIntFunction<Attrezzo> bonus) {
        return attrezziPerTipo.values().stream()
                .mapToInt(bonus)
                .sum();
    }
}
