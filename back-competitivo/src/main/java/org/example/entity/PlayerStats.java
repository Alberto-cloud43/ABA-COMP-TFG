package org.example.entity;

import lombok.Data;

/**
 * Entidad que representa las estadísticas de un jugador almacenadas en Supabase.
 * Los getters, setters y demás métodos estándar son generados por Lombok {@code @Data}.
 */
@Data
public class PlayerStats {

    /** UUID único del jugador. */
    private String uuid;

    /** Nombre de usuario del jugador. */
    private String username;

    /** Número de kills realizados. */
    private int kills;

    /** Número de muertes. */
    private int deaths;

    /** Número de victorias. */
    private int wins;

    /** Número de derrotas. */
    private int losses;

    /** Número de lanas colocadas en el monumento. */
    private int woolsPlaced;

    /** Tiempo total jugado en segundos. */
    private long playTime;

    /** Fecha de creación del registro en formato ISO 8601. */
    private String createdAt;

    /** Daño infligido. */
    private int damageDone;

    /** Daño recibido. */
    private int damageTaken;
}