package org.example.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utilidad estática que genera el ítem selector de equipo.
 */
public class TeamSelectorItem {

    /**
     * Crea y devuelve una Nether Star con el nombre "Selecciona un equipo".
     *
     * @return ítem selector de equipo listo para entregar al jugador
     */
    public static ItemStack get() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Selecciona un equipo");
        item.setItemMeta(meta);
        return item;
    }
}