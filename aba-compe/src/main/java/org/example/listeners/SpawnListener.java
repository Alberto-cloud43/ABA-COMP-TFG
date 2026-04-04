package org.example.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.example.partida.MatchManager;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Listener que configura el modo de juego del jugador al aparecer en el servidor.
 * Los jugadores sin equipo activo se colocan en modo creativo con vuelo habilitado.
 */
public class SpawnListener implements Listener {

    /**
     * Aplica el modo espectador (creativo con vuelo) al jugador si no hay partida
     * en curso o si el jugador no pertenece a ningún equipo. Si el jugador está
     * en partida y tiene equipo asignado, no se modifica su modo de juego.
     *
     * @param e evento de aparición del jugador
     */
    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent e) {
        Player p = e.getPlayer();

        if (!MatchManager.get().isInGame()) {
            p.setGameMode(GameMode.CREATIVE);
            p.setAllowFlight(true);
            p.setFlying(true);
            return;
        }

        if (!MatchManager.get().getPartida().isInTeam(p)) {
            p.setGameMode(GameMode.CREATIVE);
            p.setAllowFlight(true);
            p.setFlying(true);
        }
    }
}