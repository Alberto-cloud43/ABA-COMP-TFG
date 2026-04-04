package org.example.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;
import org.example.menus.TeamSelectorMenu;

/**
 * Listener que detecta el uso del ítem selector de equipo y abre el menú correspondiente.
 */
public class TeamSelectorListener implements Listener {

    /**
     * Abre el menú de selección de equipo cuando el jugador interactúa
     * con el ítem marcado con la clave {@code team_selector}.
     *
     * @param event evento de interacción del jugador
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta()) return;

        if (item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey(Main.get(), "team_selector"),
                PersistentDataType.INTEGER
        )) {
            TeamSelectorMenu.open(player);
            event.setCancelled(true);
        }
    }
}