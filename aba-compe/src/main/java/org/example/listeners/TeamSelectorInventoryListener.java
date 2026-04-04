package org.example.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;
import org.example.partida.MatchManager;

/**
 * Listener que gestiona la selección de equipo desde el inventario de selección.
 * Lee el ID de equipo almacenado en el PersistentDataContainer del ítem clicado
 * y asigna al jugador al equipo correspondiente.
 */
public class TeamSelectorInventoryListener implements Listener {

    /**
     * Procesa el clic en el inventario de selección de equipo.
     * Cancela el evento, obtiene el ID de equipo del ítem clicado y
     * asigna al jugador a ese equipo cerrando el inventario al finalizar.
     *
     * @param e evento de clic en inventario
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Selecciona un equipo")) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (item == null || !item.hasItemMeta()) return;

        String equipoId = item.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(Main.get(), "team_id"),
                PersistentDataType.STRING
        );

        if (equipoId == null) return;

        MatchManager.get().getPartida().meterEnEquipo(p, equipoId);
        p.closeInventory();
    }
}