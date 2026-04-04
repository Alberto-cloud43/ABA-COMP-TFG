package org.example.entity.mapa;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un mapa con su identificador, nombre y lista de equipos.
 */
public class MapaEntity {

    /** Identificador interno del mapa. */
    private final int id;

    /** Nombre legible del mapa. */
    private String nombre;

    /** Equipos registrados en este mapa. */
    private final List<EquipoEntity> equipos = new ArrayList<>();

    /**
     * Crea una nueva entidad de mapa.
     *
     * @param id     identificador interno del mapa
     * @param nombre nombre legible del mapa
     */
    public MapaEntity(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    /** @return identificador interno del mapa */
    public int getId() { return id; }

    /** @return nombre legible del mapa */
    public String getNombre() { return nombre; }

    /**
     * @param nombre nuevo nombre del mapa
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Añade un equipo a la lista de equipos del mapa.
     *
     * @param equipo equipo a registrar
     */
    public void addEquipo(EquipoEntity equipo) { equipos.add(equipo); }

    /** @return lista de equipos registrados en el mapa */
    public List<EquipoEntity> getEquipos() { return equipos; }
}