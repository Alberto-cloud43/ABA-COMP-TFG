package org.example.partida;

import org.bukkit.entity.Player;
import org.example.entity.mapa.MapaEntity;

/**
 * Gestor singleton del estado global de la partida.
 * Controla si hay una partida en curso, la instancia activa de {@link Match}
 * y si el ciclo de mapa debe ejecutarse al finalizar.
 */
public class MatchManager {

    /** Indica si hay una partida actualmente en curso. */
    private boolean inGame = false;

    /** Instancia única del gestor. */
    private static MatchManager instance;

    /** Partida activa, o {@code null} si no hay ninguna. */
    private Match partida;

    /** Indica si se debe rotar al siguiente mapa al finalizar la partida. */
    private boolean needsCycle = false;

    /**
     * Devuelve la instancia única del MatchManager, creándola si no existe.
     *
     * @return instancia singleton de MatchManager
     */
    public static MatchManager get() {
        if (instance == null) instance = new MatchManager();
        return instance;
    }

    /**
     * Crea una nueva partida para el mapa indicado.
     *
     * @param mapa información del mapa con los equipos definidos
     */
    public void createPartida(MapaEntity mapa) {
        partida = new Match(mapa);
    }

    /**
     * Comprueba si el jugador está asignado a algún equipo en la partida activa.
     *
     * @param player jugador a comprobar
     * @return {@code true} si el jugador está en un equipo
     */
    public boolean estaEnPartida(Player player) {
        return partida != null && partida.estaEnEquipo(player);
    }

    /** @return la partida activa, o {@code null} si no hay ninguna */
    public Match getPartida() { return partida; }

    /** @return {@code true} si hay una partida en curso */
    public boolean isInGame() { return inGame; }

    /**
     * Establece el estado de partida en curso.
     *
     * @param value {@code true} para indicar que la partida está en curso
     */
    public void setInGame(boolean value) { this.inGame = value; }

    /**
     * Establece si se debe rotar al siguiente mapa al finalizar la partida.
     *
     * @param b {@code true} para activar la rotación de mapa
     */
    public void setNeedsCycle(boolean b) { this.needsCycle = b; }

    /** @return {@code true} si se debe rotar al siguiente mapa */
    public boolean needsCycle() { return needsCycle; }
}