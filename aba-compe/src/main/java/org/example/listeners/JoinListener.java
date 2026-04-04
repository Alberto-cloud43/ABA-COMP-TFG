package org.example.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.example.Main;
import org.example.database.DAO.JugadorDAO;
import org.example.mapa.GameMap;
import org.example.partida.Match;
import org.example.partida.MatchManager;
import org.example.stats.StatsManager;

/**
 * Listener que gestiona la entrada de jugadores al servidor.
 * Inicializa las estadísticas del jugador, lo coloca en modo espectador
 * en el spawn correspondiente y le entrega el selector de equipo.
 */
public class JoinListener implements Listener {

    /**
     * Prepara al jugador al unirse al servidor con un pequeño delay para asegurar
     * que la conexión está completamente establecida. Registra el jugador en la base
     * de datos si no existe, lo teleporta al spawn de espectadores, aplica el scoreboard
     * de espectador, gestiona la visibilidad respecto a jugadores en partida y entrega
     * el ítem selector de equipo.
     *
     * @param e evento de entrada del jugador
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        StatsManager.get().getJugador(p.getUniqueId(), p.getName()).setJoinTime(System.currentTimeMillis());

        Bukkit.getScheduler().runTaskLater(Main.get(), () -> {
            JugadorDAO.get().findOrCreate(StatsManager.get().getJugador(p.getUniqueId(), p.getName()));

            if (!p.isOnline()) return;
            if (!e.getPlayer().equals(p)) return;

            p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1024);

            GameMap map = Main.get().getCurrentMap();
            if (map == null || map.getSpectatorSpawn() == null) return;

            p.teleport(map.getSpectatorSpawn());
            p.setGameMode(GameMode.CREATIVE);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.getInventory().clear();

            Match match = MatchManager.get().getPartida();
            if (match != null) {
                match.aplicarScoreboardEspectador(p);
            }

            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other == p) continue;
                if (MatchManager.get().estaEnPartida(other)) {
                    other.hidePlayer(Main.get(), p);
                } else {
                    other.showPlayer(Main.get(), p);
                }
            }

            ItemStack selector = new ItemStack(Material.NETHER_STAR);
            ItemMeta meta = selector.getItemMeta();
            meta.setDisplayName("§eSeleccionar equipo");
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(Main.get(), "team_selector"),
                    PersistentDataType.INTEGER, 1
            );
            selector.setItemMeta(meta);
            p.getInventory().setItem(0, selector);

            p.setPlayerListHeader("§bABA-Compe");
            p.setPlayerListFooter("§7Selecciona un equipo para comenzar");

        }, 5L);
    }
}