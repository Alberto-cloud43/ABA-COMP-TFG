package org.example.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.example.partida.Match;
import org.example.partida.MatchManager;

/**
 * Listener que protege a los espectadores del combate durante una partida.
 * Impide que los espectadores inflijan o reciban daño de otros jugadores.
 */
public class ObserverProtectionListener implements Listener {

    /**
     * Cancela el daño si el atacante o la víctima no pertenecen a ningún equipo,
     * es decir, son espectadores.
     *
     * @param e evento de daño entre entidades
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player victim)) return;
        if (!(e.getDamager() instanceof Player attacker)) return;

        Match match = MatchManager.get().getPartida();
        if (match == null) return;

        String victimTeam = match.getTeamOf(victim);
        String attackerTeam = match.getTeamOf(attacker);

        if (attackerTeam == null) {
            e.setCancelled(true);
            return;
        }

        if (victimTeam == null) {
            e.setCancelled(true);
        }
    }
}