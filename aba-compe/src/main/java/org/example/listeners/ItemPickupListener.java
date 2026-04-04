package org.example.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.example.Main;
import org.example.mapa.GameMap;
import org.example.partida.Match;
import org.example.partida.MatchManager;

/**
 * Listener que controla la recogida y el descarte de ítems durante una partida.
 * Impide que los espectadores recojan ítems del kit y elimina del suelo
 * cualquier ítem del kit que un jugador intente descartar.
 */
public class ItemPickupListener implements Listener {

    /**
     * Cancela la recogida de ítems del kit para jugadores que no pertenecen a ningún equipo.
     * Los jugadores en partida pueden recoger cualquier ítem sin restricción.
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
        if (match != null && match.isInTeam(p)) return;

        ItemStack item = e.getItem().getItemStack();

        boolean esDelKit = map.getSpawnKit().stream()
                .filter(kitItem -> kitItem != null)
                .anyMatch(kitItem -> kitItem.getType() == item.getType());

        if (esDelKit) {
            e.setCancelled(true);
        }
    }

    /**
     * Elimina del suelo los ítems del kit que un jugador descarte durante la partida.
     * La eliminación se aplica con un pequeño delay para asegurar que el ítem
     * ya existe como entidad en el mundo.
     *
     * @param e evento de descarte de ítem
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!MatchManager.get().isInGame()) return;

        GameMap map = Main.get().getCurrentMap();
        if (map == null) return;

        ItemStack item = e.getItemDrop().getItemStack();

        boolean esDelKit = map.getSpawnKit().stream()
                .filter(kitItem -> kitItem != null)
                .anyMatch(kitItem -> kitItem.getType() == item.getType());

        if (esDelKit) {
            Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
                e.getItemDrop().remove();
            }, 4L);
        }
    }
}