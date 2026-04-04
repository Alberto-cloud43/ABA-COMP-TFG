package org.example.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.example.Main;
import org.example.entity.objetivo.WoolEntity;
import org.example.mapa.GameMap;
import org.example.partida.GameMatch;
import org.example.partida.Match;
import org.example.partida.MatchManager;
import org.example.stats.StatsManager;

/**
 * Listener que gestiona los objetivos de lana durante una partida.
 * Detecta cuando un jugador recoge una lana de su posición original
 * y cuando la coloca en el monumento rival para ganar la partida.
 */
public class WoolListener implements Listener {

    /** Instancia principal del plugin. */
    private final Main plugin;

    /**
     * Crea una nueva instancia del listener.
     *
     * @param plugin instancia principal del plugin
     */
    public WoolListener(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Detecta la recogida de una lana objetivo cerca de su posición original.
     * Actualiza el scoreboard, registra la estadística de lana tocada y
     * anota el equipo que la tocó.
     *
     * @param e evento de recogida de ítem
     */
    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (!MatchManager.get().isInGame()) return;

        GameMap map = Main.get().getCurrentMap();
        if (map == null) return;

        Match match = MatchManager.get().getPartida();
        if (match == null) return;

        String teamId = match.getTeamOf(p);
        if (teamId == null) return;

        ItemStack item = e.getItem().getItemStack();

        for (WoolEntity wool : map.getWools()) {
            if (wool.getColor() != item.getType()) continue;

            double ix = e.getItem().getLocation().getX();
            double iy = e.getItem().getLocation().getY();
            double iz = e.getItem().getLocation().getZ();

            if (Math.abs(ix - wool.getLocX()) > 3 ||
                    Math.abs(iy - wool.getLocY()) > 3 ||
                    Math.abs(iz - wool.getLocZ()) > 3) continue;

            GameMatch gameMatch = plugin.getMatch();
            if (gameMatch != null && gameMatch.getWoolScoreboard() != null) {
                gameMatch.getWoolScoreboard().marcarTocada(wool);
                gameMatch.getWoolScoreboard().actualizar(map, match);
            }

            StatsManager.get().getJugador(p.getUniqueId(), p.getName()).addWoolTouched();

            if (gameMatch != null) {
                gameMatch.getEquiposTocaron().add(teamId);
            }

            Bukkit.broadcastMessage("§e" + p.getName() + " §fha tocado la lana §e" + wool.getColor().name().replace("_WOOL", ""));
            break;
        }
    }

    /**
     * Detecta la colocación de una lana en el monumento rival para determinar el ganador.
     * Impide colocar la lana del equipo contrario y, si se coloca en la posición correcta
     * del monumento, finaliza la partida otorgando la victoria al equipo del jugador.
     *
     * @param e evento de colocación de bloque
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!MatchManager.get().isInGame()) return;

        GameMap map = Main.get().getCurrentMap();
        if (map == null) return;

        Match match = MatchManager.get().getPartida();
        if (match == null) return;

        Player p = e.getPlayer();
        String teamId = match.getTeamOf(p);
        if (teamId == null) return;

        for (WoolEntity wool : map.getWools()) {
            if (wool.getColor() != e.getBlock().getType()) continue;
            if (!wool.getTeam().equalsIgnoreCase(teamId)) {
                e.setCancelled(true);
                p.sendMessage("§cNo puedes colocar la lana del equipo rival.");
                return;
            }
        }

        for (WoolEntity wool : map.getWools()) {
            if (wool.getColor() != e.getBlock().getType()) continue;
            if (!wool.getTeam().equalsIgnoreCase(teamId)) continue;

            double x = e.getBlock().getX();
            double y = e.getBlock().getY();
            double z = e.getBlock().getZ();

            if (Math.abs(x - wool.getMonX()) < 1 &&
                    Math.abs(y - wool.getMonY()) < 1 &&
                    Math.abs(z - wool.getMonZ()) < 1) {

                Bukkit.broadcastMessage("§a¡El equipo §e" + teamId + " §aha ganado la partida!");

                GameMatch gameMatch = plugin.getMatch();
                if (gameMatch != null && gameMatch.getWoolScoreboard() != null) {
                    gameMatch.getWoolScoreboard().marcarColocada(wool);
                    gameMatch.getWoolScoreboard().actualizar(map, match);
                }

                StatsManager.get().getJugador(p.getUniqueId(), p.getName()).addWoolPlaced();
                gameMatch.end(teamId);
            }
        }
    }
}