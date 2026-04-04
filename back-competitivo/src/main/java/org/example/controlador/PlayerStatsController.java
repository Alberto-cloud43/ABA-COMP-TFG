package org.example.controlador;

import org.example.entity.PlayerStats;
import org.example.service.PlayerStatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone los endpoints de estadísticas de jugadores bajo {@code /api/stats}.
 * Permite peticiones desde cualquier origen y desde {@code localhost:4200} en desarrollo.
 */
@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class PlayerStatsController {

    /** Servicio que gestiona las consultas de estadísticas. */
    private PlayerStatsService playerStatsService;

    /**
     * Crea una nueva instancia del controlador con el servicio inyectado.
     *
     * @param playerStatsService servicio de estadísticas de jugadores
     */
    public PlayerStatsController(PlayerStatsService playerStatsService) {
        this.playerStatsService = playerStatsService;
    }

    /**
     * Devuelve la lista completa de estadísticas de todos los jugadores.
     *
     * @return lista de {@link PlayerStats}
     */
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<PlayerStats> getPlayerStats() {
        return playerStatsService.getAll();
    }

    /**
     * Devuelve las estadísticas del jugador con el nombre de usuario indicado.
     *
     * @param username nombre de usuario del jugador
     * @return {@link PlayerStats} del jugador, o {@code null} si no existe
     */
    @GetMapping("/username/{username}")
    public PlayerStats getByUsername(@PathVariable String username) {
        return playerStatsService.getByUsername(username);
    }
}