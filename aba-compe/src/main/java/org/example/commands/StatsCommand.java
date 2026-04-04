package org.example.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.entity.jugador.JugadorEntity;
import org.example.stats.StatsManager;

/**
 * Comando que muestra las estadísticas de un jugador.
 * Si no se indica argumento, muestra las del propio ejecutor.
 */
public class StatsCommand implements CommandExecutor {

    /**
     * Muestra kills, muertes, KD, lanas, victorias, derrotas y tiempo jugado
     * del jugador indicado o del propio ejecutor si no se pasa argumento.
     *
     * @param sender ejecutor del comando
     * @param cmd    comando ejecutado
     * @param label  alias utilizado
     * @param args   argumentos opcionales; args[0] puede ser el nombre del jugador objetivo
     * @return {@code true} siempre
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cUsa: /stats <jugador>");
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cJugador no encontrado.");
                return true;
            }
        }

        JugadorEntity s = StatsManager.get().getJugador(target.getUniqueId(), target.getName());

        sender.sendMessage("§8§m--------------------");
        sender.sendMessage("§6§lStats de §e" + target.getName());
        sender.sendMessage("§8§m--------------------");
        sender.sendMessage("§c⚔ Kills: §f" + s.getKills());
        sender.sendMessage("§4💀 Deaths: §f" + s.getDeaths());
        sender.sendMessage("§e📈 KD: §f" + s.getKD());
        sender.sendMessage("§b🟦 Wools placed: §f" + s.getWoolsPlaced());
        sender.sendMessage("§3👆 Wools touched: §f" + s.getWoolsTouched());
        sender.sendMessage("§a🏆 Wins: §f" + s.getWins());
        sender.sendMessage("§c❌ Losses: §f" + s.getLosses());
        sender.sendMessage("§7⏱ Time played: §f" + s.getTimePlayedFormatted());
        sender.sendMessage("§8§m--------------------");

        return true;
    }
}