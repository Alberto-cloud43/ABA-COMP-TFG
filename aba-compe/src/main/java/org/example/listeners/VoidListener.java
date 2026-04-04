package org.example.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.example.Main;
import org.example.mapa.GameMap;
import org.example.partida.Match;
import org.example.partida.MatchManager;

/**
 * Listener que gestiona el daño por void durante una partida.
 * Los espectadores y jugadores sin equipo son teleportados al spawn de espectadores
 * en lugar de recibir daño. Los jugadores con equipo sí reciben el daño normalmente.
 */
public class VoidListener implements Listener {

    /**
     * Intercepta el daño por void con máxima prioridad. Si el jugador es espectador
     * o no hay partida en curso, cancela el daño y lo teleporta al spawn de espectadores.
     * Si el jugador pertenece a un equipo, fuerza el daño anulando la inmunidad temporal.
     *
     * @param e evento de daño a una entidad
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVoidDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.VOID) return;

        GameMap map = Main.get().getCurrentMap();
        if (map == null) return;

        Match match = MatchManager.get().getPartida();

        if (!MatchManager.get().isInGame()) {
            e.setCancelled(true);
            p.teleport(map.getSpectatorSpawn());
            return;
        }

        if (match == null) return;

        String team = match.getTeamOf(p);

        if (team == null) {
            e.setCancelled(true);
            p.teleport(map.getSpectatorSpawn());
        } else {
            e.setCancelled(false);
            p.setNoDamageTicks(0);
            p.setMaximumNoDamageTicks(20);
        }
    }
}