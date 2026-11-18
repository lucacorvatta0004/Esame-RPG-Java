package it.unicam.cs.mpgc.rpg130402.persistence;

/**
 * Legge le costanti delle enum che nei file JSON sono scritte per nome (per esempio "CAPO_PARTITA").
 * Gson con un nome sbagliato metterebbe null senza dire niente: qui invece l'errore dice cosa non va.
 */
final class LetturaCostanti {

    private LetturaCostanti() {
    }

    /**
     * La costante del tipo dato che ha quel nome; la descrizione serve per il messaggio d'errore.
     *
     * @throws IllegalArgumentException se il nome manca o non è il nome di una costante del tipo
     */
    static <E extends Enum<E>> E perNome(Class<E> tipo, String nome, String descrizione) {
        if (nome == null) {
            throw new IllegalArgumentException(descrizione + " manca");
        }
        try {
            return Enum.valueOf(tipo, nome);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(descrizione + " " + nome + " non esiste", e);
        }
    }
}
