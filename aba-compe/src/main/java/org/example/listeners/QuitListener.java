package org.example.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.example.database.DAO.JugadorDAO;
import org.example.entity.jugador.JugadorEntity;
import org.example.stats.StatsManager;

/**
 * Listener que gestiona la salida de jugadores del servidor.
 * Persiste las estadísticas en la base de datos y acumula el tiempo de sesión jugado.
 */
public class QuitListener implements Listener {

    /**
     * Guarda las estadísticas del jugador al desconectarse y registra
     * el tiempo transcurrido desde que entró al servidor.
     *
     * @param e evento de salida del jugador
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        JugadorEntity j = StatsManager.get().getJugador(p.getUniqueId(), p.getName());
        JugadorDAO.get().saveStats(j);
        long duration = (System.currentTimeMillis() - j.getJoinTime()) / 1000;
        j.addTimePlayed(duration);
    }
}