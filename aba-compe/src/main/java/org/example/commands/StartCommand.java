package org.example.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.partida.GameMatch;
import org.example.partida.MatchManager;

/**
 * Comando que inicia la cuenta atrás de la partida.
 * Acepta un argumento opcional con la duración en segundos (por defecto 30).
 */
public class StartCommand implements CommandExecutor {

    /** Partida activa sobre la que actúa el comando. */
    private final GameMatch match;

    /**
     * Crea una nueva instancia del comando.
     *
     * @param match partida activa
     */
    public StartCommand(GameMatch match) {
        this.match = match;
    }

    /**
     * Inicia la cuenta atrás de la partida. Solo puede ejecutarlo un jugador.
     * Si el servidor necesita un ciclo de mapa o la partida ya está en curso,
     * notifica al ejecutor sin iniciar la cuenta atrás.
     *
     * @param sender ejecutor del comando
     * @param cmd    comando ejecutado
     * @param label  alias utilizado
     * @param args   argumentos opcionales; args[0] puede ser la duración en segundos
     * @return {@code true} siempre
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }

        Player p = (Player) sender;

        if (MatchManager.get().needsCycle()) {
            p.sendMessage("§cDebes hacer §e/cycle <mapa> §cantes de iniciar otra partida.");
            return true;
        }

        if (match.isStarted()) {
            p.sendMessage("§cLa partida ya está en curso.");
            return true;
        }

        int seconds = 30;

        if (args.length == 1) {
            try {
                seconds = Integer.parseInt(args[0]);
            } catch (Exception ignored) {
                p.sendMessage("§cDebes poner un número. Ejemplo: /start 20");
                return true;
            }
        }

        match.startCountdown(seconds);
        p.sendMessage("§aCuenta atrás de §e" + seconds + "s §ainiciada.");
        return true;
    }
}