package org.example.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.example.Main;
import org.example.mapa.GameMap;
import org.example.partida.Match;
import org.example.partida.MatchManager;
import org.example.stats.StatsManager;

import java.util.List;

/**
 * Listener que gestiona la muerte y el respawn de los jugadores durante una partida.
 * Se encarga de recolocar al jugador en el spawn de su equipo y restaurar su kit y armadura,
 * así como de registrar kills y muertes en el StatsManager.
 */
public class DeathListener implements Listener {

    /**
     * Gestiona el respawn del jugador. Siempre aplica la velocidad de ataque máxima
     * al reaparecer. Si hay una partida en curso, teleporta al jugador al spawn
     * de su equipo y le restaura el kit de ítems y la armadura correspondiente
     * con un pequeño delay para asegurar que el inventario esté listo.
     *
     * @param e evento de respawn del jugador
     */
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024);
        }, 1L);

        if (!MatchManager.get().isInGame()) return;

        Match match = MatchManager.get().getPartida();
        GameMap map = Main.get().getCurrentMap();

        if (match == null || map == null) return;

        String team = match.getTeamOf(p);
        if (team == null) return;

        List<Location> spawns = map.getTeamSpawns(team);
        if (!spawns.isEmpty()) {
            e.setRespawnLocation(spawns.get(0));
        }

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            p.getInventory().clear();

            List<ItemStack> kit = map.getSpawnKit();
            for (int i = 0; i < kit.size(); i++) {
                ItemStack item = kit.get(i);
                if (item != null) {
                    p.getInventory().setItem(i, item.clone());
                }
            }

            String kitId = team + "-kit";
            List<ItemStack> armor = map.getTeamArmor(kitId);
            ItemStack[] armorArray = new ItemStack[4];
            for (int i = 0; i < 4; i++) {
                ItemStack piece = armor.get(i);
                armorArray[i] = piece != null ? piece.clone() : null;
            }
            p.getInventory().setArmorContents(armorArray);

        }, 2L);
    }

    /**
     * Registra la muerte de la víctima y, si existe un asesino, su kill en el StatsManager.
     *
     * @param e evento de muerte del jugador
     */
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!MatchManager.get().isInGame()) return;

        Player victim = e.getEntity();
        Player killer = victim.getKiller();

        StatsManager.get().getJugador(victim.getUniqueId(), victim.getName()).addDeath();

        if (killer != null) {
            StatsManager.get().getJugador(killer.getUniqueId(), killer.getName()).addKill();
        }
    }
}