package org.example.stats;

import org.example.entity.jugador.JugadorEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gestor singleton de estadísticas de jugadores en memoria.
 * Mantiene un registro de {@link JugadorEntity} indexado por UUID
 * durante el tiempo de vida del servidor.
 */
public class StatsManager {

    /** Instancia única del gestor. */
    private static StatsManager instance;

    /** Registro de jugadores indexado por UUID. */
    private final Map<UUID, JugadorEntity> jugadores = new HashMap<>();

    /**
     * Devuelve la instancia única del StatsManager, creándola si no existe.
     *
     * @return instancia singleton de StatsManager
     */
    public static StatsManager get() {
        if (instance == null) instance = new StatsManager();
        return instance;
    }

    /**
     * Devuelve el jugador asociado al UUID indicado.
     * Si no existe, crea una nueva entrada con el nombre proporcionado.
     *
     * @param uuid UUID del jugador
     * @param name nombre del jugador, usado al crear la entrada si no existe
     * @return entidad de estadísticas del jugador
     */
    public JugadorEntity getJugador(UUID uuid, String name) {
        return jugadores.computeIfAbsent(uuid, id -> new JugadorEntity(id, name));
    }
}