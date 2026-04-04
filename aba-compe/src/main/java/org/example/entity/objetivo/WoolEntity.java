package org.example.entity.objetivo;

import org.bukkit.Material;

/**
 * Representa una lana objetivo del mapa. Almacena el equipo al que pertenece,
 * su color, su posición original en el mapa y la posición del monumento
 * donde debe colocarse para puntuar.
 */
public class WoolEntity {

    /** ID del equipo propietario de esta lana. */
    private final String team;

    /** Material de la lana, determina su color (p. ej. {@code CYAN_WOOL}). */
    private final Material color;

    /** Coordenadas de la posición original de la lana en el mapa. */
    private final double locX, locY, locZ;

    /** Coordenadas del bloque del monumento donde debe colocarse la lana. */
    private final double monX, monY, monZ;

    /**
     * Crea una nueva entidad de lana objetivo.
     *
     * @param team ID del equipo propietario
     * @param color material de la lana
     * @param locX coordenada X de la posición original
     * @param locY coordenada Y de la posición original
     * @param locZ coordenada Z de la posición original
     * @param monX coordenada X del monumento
     * @param monY coordenada Y del monumento
     * @param monZ coordenada Z del monumento
     */
    public WoolEntity(String team, Material color, double locX, double locY, double locZ, double monX, double monY, double monZ) {
        this.team = team;
        this.color = color;
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;
        this.monX = monX;
        this.monY = monY;
        this.monZ = monZ;
    }

    /** @return ID del equipo propietario */
    public String getTeam() { return team; }

    /** @return material de la lana */
    public Material getColor() { return color; }

    /** @return coordenada X de la posición original */
    public double getLocX() { return locX; }

    /** @return coordenada Y de la posición original */
    public double getLocY() { return locY; }

    /** @return coordenada Z de la posición original */
    public double getLocZ() { return locZ; }

    /** @return coordenada X del monumento */
    public double getMonX() { return monX; }

    /** @return coordenada Y del monumento */
    public double getMonY() { return monY; }

    /** @return coordenada Z del monumento */
    public double getMonZ() { return monZ; }
}