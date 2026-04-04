package org.example.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.example.Main;
import org.example.mapa.GameMap;
import org.example.partida.MatchManager;

/**
 * Listener que gestiona las recompensas por kill durante una partida.
 * Elimina los drops y la experiencia del jugador muerto y entrega
 * los ítems de recompensa configurados en el mapa al jugador asesino.
 */
public class KillRewardListener implements Listener {

    /**
     * Limpia los drops del jugador muerto y entrega las kill rewards al asesino.
     * Si la muerte se produjo por causas ambientales (void, etc.), no se entrega ninguna recompensa.
     *
     * @param e evento de muerte del jugador
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!MatchManager.get().isInGame()) return;

        Player killed = e.getEntity();
        Player killer = killed.getKiller();

        e.getDrops().clear();
        e.setDroppedExp(0);

        if (killer == null) return;

        GameMap map = Main.get().getCurrentMap();
        if (map == null) return;

        for (ItemStack reward : map.getKillRewards()) {
            killer.getInventory().addItem(reward.clone());
        }

        killer.sendMessage("§a+Kill reward recibido");
    }
}