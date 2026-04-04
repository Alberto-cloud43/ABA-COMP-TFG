package org.example.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;
import org.example.entity.mapa.EquipoEntity;
import org.example.entity.mapa.MapaEntity;
import org.example.partida.MatchManager;
import org.bukkit.Material;

/**
 * Menú de selección de equipo. Genera un inventario con un ítem de lana
 * por cada equipo disponible en el mapa y lo abre al jugador indicado.
 */
public class TeamSelectorMenu {

    /**
     * Abre el inventario de selección de equipo para el jugador.
     * Cada ítem representa un equipo, usa la lana del color correspondiente
     * y almacena el ID del equipo en su PersistentDataContainer.
     *
     * @param p jugador al que se abre el menú
     */
    public static void open(Player p) {
        MapaEntity mapa = MatchManager.get().getPartida().getMapa();
        Inventory inv = Bukkit.createInventory(null, 9, "Selecciona un equipo");

        for (EquipoEntity eq : mapa.getEquipos()) {
            String color = eq.getColor().toUpperCase();
            Material wool = Material.valueOf(color + "_WOOL");

            ItemStack item = new ItemStack(wool);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.valueOf(color) + eq.getId().toUpperCase());
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(Main.get(), "team_id"),
                    PersistentDataType.STRING,
                    eq.getId()
            );
            item.setItemMeta(meta);
            inv.addItem(item);
        }

        p.openInventory(inv);
    }
}