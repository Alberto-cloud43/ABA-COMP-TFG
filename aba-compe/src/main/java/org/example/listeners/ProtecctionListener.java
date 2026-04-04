package org.example.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.example.partida.Match;
import org.example.partida.MatchManager;

/**
 * Listener que restringe las acciones de los espectadores durante una partida.
 * Impide que los jugadores sin equipo rompan o coloquen bloques, recojan o
 * descarten ítems, lancen proyectiles, interactúen con contenedores o
 * manipulen su inventario.
 */
public class ProtecctionListener implements Listener {

    /**
     * Indica si un jugador es espectador, es decir, si no pertenece a ningún equipo
     * o si la partida aún no ha comenzado.
     *
     * @param p jugador a comprobar
     * @return {@code true} si el jugador es espectador, {@code false} si está en un equipo
     */
    private boolean isSpectator(Player p) {
        Match partida = MatchManager.get().getPartida();
        if (!MatchManager.get().isInGame()) return true;
        return !partida.isInTeam(p);
    }

    /**
     * Cancela la rotura de bloques para espectadores.
     *
     * @param event evento de rotura de bloque
     */
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    /**
     * Cancela la colocación de bloques para espectadores.
     *
     * @param event evento de colocación de bloque
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    /**
     * Cancela la recogida de ítems para espectadores.
     *
     * @param event evento de intento de recogida de ítem
     */
    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        if (isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    /**
     * Cancela el lanzamiento de proyectiles para espectadores.
     *
     * @param e evento de lanzamiento de proyectil
     */
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player p)) return;
        if (!MatchManager.get().isInGame()) return;

        Match match = MatchManager.get().getPartida();
        if (match == null) return;

        if (match.getTeamOf(p) == null) {
            e.setCancelled(true);
        }
    }

    /**
     * Cancela el descarte de ítems para espectadores.
     *
     * @param event evento de descarte de ítem
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isSpectator(event.getPlayer())) event.setCancelled(true);
    }

    /**
     * Cancela los clics en inventario para espectadores.
     *
     * @param event evento de clic en inventario
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isSpectator((Player) event.getWhoClicked())) event.setCancelled(true);
    }

    /**
     * Cancela el arrastre de ítems en inventario para espectadores.
     *
     * @param event evento de arrastre en inventario
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isSpectator((Player) event.getWhoClicked())) event.setCancelled(true);
    }

    /**
     * Cancela la interacción con contenedores (barriles, cofres de ender y
     * shulker boxes de cualquier color) para espectadores.
     *
     * @param event evento de interacción del jugador
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (!isSpectator(p)) return;
        if (event.getClickedBlock() == null) return;

        switch (event.getClickedBlock().getType()) {
            case BARREL:
            case ENDER_CHEST:
            case SHULKER_BOX:
            case BLACK_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
                event.setCancelled(true);
                break;
        }
    }
}