package org.example.entity.jugador;

import java.util.UUID;

/**
 * Entidad que almacena las estadísticas de un jugador durante su sesión.
 * Los datos se persisten en Supabase a través de {@link org.example.database.DAO.JugadorDAO}.
 */
public class JugadorEntity {

    /** Identificador único del jugador. */
    private final UUID uuid;

    /** Nombre del jugador en el momento de la sesión. */
    private final String name;

    private int kills = 0;
    private int deaths = 0;
    private int woolsPlaced = 0;
    private int woolsTouched = 0;
    private int wins = 0;
    private int losses = 0;

    /** Tiempo total jugado acumulado, en segundos. */
    private long timePlayed = 0;

    /** Marca de tiempo en milisegundos del momento en que el jugador entró al servidor. */
    private long joinTime = 0;

    /**
     * Crea una nueva entidad de jugador.
     *
     * @param uuid UUID del jugador
     * @param name nombre del jugador
     */
    public JugadorEntity(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    /** @return UUID del jugador */
    public UUID getUuid() { return uuid; }

    /** @return nombre del jugador */
    public String getName() { return name; }

    /** @return número de kills */
    public int getKills() { return kills; }

    /** @return número de muertes */
    public int getDeaths() { return deaths; }

    /** @return número de lanas colocadas en el monumento */
    public int getWoolsPlaced() { return woolsPlaced; }

    /** @return número de lanas tocadas */
    public int getWoolsTouched() { return woolsTouched; }

    /** @return número de victorias */
    public int getWins() { return wins; }

    /** @return número de derrotas */
    public int getLosses() { return losses; }

    /** @return tiempo total jugado en segundos */
    public long getTimePlayed() { return timePlayed; }

    /** @return marca de tiempo en milisegundos de la última entrada al servidor */
    public long getJoinTime() { return joinTime; }

    /** Incrementa el contador de kills en 1. */
    public void addKill() { kills++; }

    /** Incrementa el contador de muertes en 1. */
    public void addDeath() { deaths++; }

    /** Incrementa el contador de lanas colocadas en 1. */
    public void addWoolPlaced() { woolsPlaced++; }

    /** Incrementa el contador de lanas tocadas en 1. */
    public void addWoolTouched() { woolsTouched++; }

    /** Incrementa el contador de victorias en 1. */
    public void addWin() { wins++; }

    /** Incrementa el contador de derrotas en 1. */
    public void addLoss() { losses++; }

    /**
     * Añade segundos al tiempo total jugado.
     *
     * @param seconds segundos a acumular
     */
    public void addTimePlayed(long seconds) { timePlayed += seconds; }

    /**
     * Establece la marca de tiempo de entrada al servidor.
     *
     * @param time tiempo en milisegundos ({@link System#currentTimeMillis()})
     */
    public void setJoinTime(long time) { joinTime = time; }

    /**
     * Calcula el ratio kill/death redondeado a dos decimales.
     * Si el jugador no tiene muertes, devuelve el número de kills directamente.
     *
     * @return ratio KD
     */
    public double getKD() {
        if (deaths == 0) return kills;
        return Math.round((double) kills / deaths * 100.0) / 100.0;
    }

    /**
     * Formatea el tiempo total jugado en el formato {@code Xh Ym Zs}.
     *
     * @return cadena con el tiempo jugado formateado
     */
    public String getTimePlayedFormatted() {
        long h = timePlayed / 3600;
        long m = (timePlayed % 3600) / 60;
        long s = timePlayed % 60;
        return h + "h " + m + "m " + s + "s";
    }
}