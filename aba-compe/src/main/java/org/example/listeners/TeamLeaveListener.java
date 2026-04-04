package org.example.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.example.partida.MatchManager;

/**
 * Listener que limpia el estado del jugador al abandonar el servidor.
 * Lo elimina de su equipo y restablece su nombre y scoreboard al estado por defecto.
 */
public class TeamLeaveListener implements Listener {

    /**
     * Elimina al jugador de su equipo y resetea su nombre en el TAB,
     * en el chat y su scoreboard al desconectarse.
     *
     * @param e evento de salida del jugador
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();
        MatchManager.get().getPartida().quitarDeEquipo(p);
        p.setPlayerListName(p.getName());
        p.setDisplayName(p.getName());
        p.setScoreboard(p.getServer().getScoreboardManager().getNewScoreboard());
    }
}