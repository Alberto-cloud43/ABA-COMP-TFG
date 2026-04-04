package org.example.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.example.partida.GameMatch;

/**
 * Comando que permite a un jugador forzar el fin de la partida en curso.
 */
public class EndCommand implements CommandExecutor {

    /** Partida activa sobre la que actúa el comando. */
    private final GameMatch match;

    /**
     * Crea una nueva instancia del comando.
     *
     * @param match partida activa
     */
    public EndCommand(GameMatch match) {
        this.match = match;
    }

    /**
     * Finaliza la partida en curso sin ganador. Solo puede ejecutarlo un jugador.
     * Si no hay partida en curso, notifica al ejecutor.
     *
     * @param sender ejecutor del comando
     * @param cmd    comando ejecutado
     * @param label  alias utilizado
     * @param args   argumentos del comando (no se usan)
     * @return {@code true} siempre
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }

        Player p = (Player) sender;

        if (!match.isStarted()) {
            p.sendMessage("§cNo hay ninguna partida en curso.");
            return true;
        }

        match.end(null);
        p.sendMessage("§cHas forzado el fin de la partida.");
        return true;
    }
}