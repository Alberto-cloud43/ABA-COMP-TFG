package org.example.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.example.Main;
import org.example.entity.mapa.RegionEntity;
import org.example.mapa.GameMap;
import org.example.partida.MatchManager;

import java.util.Map;

/**
 * Listener que protege las regiones del mapa durante una partida.
 * Impide romper bloques en regiones never-build y restringe la colocación
 * de bloques fuera de las regiones de construcción permitidas y los límites de altura.
 */
public class RegionProtectionListener implements Listener {

    /**
     * Cancela la rotura de bloques en regiones protegidas.
     *
     * @param e evento de rotura de bloque
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (isProtected(e.getPlayer(), e.getBlock().getLocation())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cNo puedes modificar bloques aquí.");
        }
    }

    /**
     * Valida la colocación de bloques comprobando en orden:
     * si el bloque es una lana de objetivo en su monumento (se permite y delega al WoolListener),
     * los límites de altura máxima y mínima, si el bloque está dentro de alguna región
     * de construcción permitida y si la posición está en una región never-build.
     *
     * @param e evento de colocación de bloque
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent e) {
        GameMap map = Main.get().getCurrentMap();

        if (map != null) {
            for (org.example.entity.objetivo.WoolEntity wool : map.getWools()) {
                if (wool.getColor() == e.getBlock().getType()) {
                    double x = e.getBlock().getX();
                    double y = e.getBlock().getY();
                    double z = e.getBlock().getZ();
                    if (Math.abs(x - wool.getMonX()) < 1 &&
                            Math.abs(y - wool.getMonY()) < 1 &&
                            Math.abs(z - wool.getMonZ()) < 1) {
                        return;
                    }
                }
            }
        }

        if (map.getMaxBuildHeight() > 0 && e.getBlock().getY() > map.getMaxBuildHeight()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cNo puedes construir más alto de Y " + map.getMaxBuildHeight());
            return;
        }

        if (e.getBlock().getY() < map.getMinBuildHeight()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cNo puedes construir más abajo de Y " + map.getMinBuildHeight());
            return;
        }

        if (MatchManager.get().isInGame() && map != null && !map.getBuildRegions().isEmpty()) {
            double x = e.getBlock().getX();
            double y = e.getBlock().getY();
            double z = e.getBlock().getZ();

            boolean dentroDeAlguna = false;
            for (RegionEntity region : map.getBuildRegions()) {
                if (region.contains(x, y, z)) {
                    dentroDeAlguna = true;
                    break;
                }
            }

            if (!dentroDeAlguna) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("§cNo puedes construir fuera de los límites del mapa.");
                return;
            }
        }

        if (isProtected(e.getPlayer(), e.getBlock().getLocation())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cNo puedes modificar bloques aquí.");
        }
    }

    /**
     * Comprueba si una ubicación está dentro de alguna región never-build del mapa.
     *
     * @param p   jugador que intenta modificar el bloque
     * @param loc ubicación del bloque
     * @return {@code true} si la ubicación está protegida, {@code false} en caso contrario
     */
    private boolean isProtected(Player p, Location loc) {
        if (!MatchManager.get().isInGame()) return false;

        GameMap map = Main.get().getCurrentMap();
        if (map == null) return false;

        Map<String, RegionEntity> regions = map.getRegions();

        for (String regionId : map.getNeverBuildRegions()) {
            RegionEntity region = regions.get(regionId);
            if (region == null) continue;
            if (region.contains(loc.getX(), loc.getY(), loc.getZ())) {
                return true;
            }
        }

        return false;
    }
}