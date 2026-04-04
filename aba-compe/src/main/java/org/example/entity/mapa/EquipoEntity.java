package org.example.entity.mapa;

/**
 * Representa un equipo del mapa con su identificador, color y límite de jugadores.
 */
public class EquipoEntity {

    /** Identificador único del equipo (p. ej. {@code "red"}, {@code "blue"}). */
    private final String id;

    /** Color del equipo en formato de nombre (p. ej. {@code "RED"}, {@code "BLUE"}). */
    private final String color;

    /** Número máximo de jugadores permitidos en el equipo. */
    private final int maxPlayers;

    /**
     * Crea una nueva entidad de equipo.
     *
     * @param id         identificador del equipo
     * @param color      color del equipo
     * @param maxPlayers número máximo de jugadores
     */
    public EquipoEntity(String id, String color, int maxPlayers) {
        this.id = id;
        this.color = color;
        this.maxPlayers = maxPlayers;
    }

    /** @return identificador del equipo */
    public String getId() { return id; }

    /** @return color del equipo */
    public String getColor() { return color; }

    /** @return número máximo de jugadores del equipo */
    public int getMaxPlayers() { return maxPlayers; }
}