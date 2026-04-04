package org.example.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.example.Main;
import org.example.entity.mapa.BlockDropEntity;
import org.example.mapa.GameMap;
import org.example.partida.MatchManager;

/**
 * Listener que reemplaza los drops vanilla de ciertos bloques por drops
 * personalizados definidos en la configuración del mapa.
 */
public class BlockDropListener implements Listener {

    /**
     * Intercepta la rotura de bloques durante una partida y aplica los drops
     * personalizados configurados en el mapa actual.
     *
     * <p>Si el bloque roto tiene una entrada en {@link GameMap#getBlockDrops()},
     * se cancelan los drops vanilla y se generan los ítems configurados.
     * Cuando {@code wrongTool} está activo, el drop solo se aplica si el jugador
     * usa una herramienta incorrecta; si hay tools definidas en el YAML se comprueba
     * contra esa lista, de lo contrario se usa la herramienta preferida del bloque.</p>
     *
     * @param e evento de rotura de bloque
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!MatchManager.get().isInGame()) return;

        GameMap map = Main.get().getCurrentMap();
        if (map == null) return;

        Player p = e.getPlayer();

        for (BlockDropEntity blockDrop : map.getBlockDrops()) {
            if (blockDrop.getMaterial() != e.getBlock().getType()) continue;

            System.out.println("Bloque encontrado: " + e.getBlock().getType());
            System.out.println("Tools en lista: " + blockDrop.getTools().size());
            System.out.println("Herramienta en mano: " + p.getInventory().getItemInMainHand().getType());
            System.out.println("Tools contiene herramienta: " + blockDrop.getTools().contains(p.getInventory().getItemInMainHand().getType()));

            if (blockDrop.isWrongTool()) {
                Material inHand = p.getInventory().getItemInMainHand().getType();

                if (blockDrop.getTools().isEmpty()) {
                    if (e.getBlock().isPreferredTool(p.getInventory().getItemInMainHand())) continue;
                } else {
                    if (blockDrop.getTools().contains(inHand)) continue;
                }
            }

            e.setDropItems(false);

            for (ItemStack drop : blockDrop.getDrops()) {
                e.getBlock().getWorld().dropItemNaturally(
                        e.getBlock().getLocation(),
                        drop.clone()
                );
            }

            break;
        }
    }
}