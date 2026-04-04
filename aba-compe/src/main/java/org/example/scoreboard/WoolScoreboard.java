package org.example.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.example.entity.objetivo.WoolEntity;
import org.example.mapa.GameMap;
import org.example.partida.Match;

import java.util.HashSet;
import java.util.Set;

/**
 * Scoreboard lateral que muestra el estado de las lanas durante la partida.
 * Cada lana puede estar sin tocar (gris), tocada (color del equipo) o colocada (color de la lana).
 */
public class WoolScoreboard {

    /** Scoreboard compartido con la partida. */
    private final Scoreboard scoreboard;

    /** Objetivo del scoreboard que se muestra en la barra lateral. */
    private final Objective objective;

    /** Claves de lanas ya colocadas en su monumento, con formato {@code teamId_colorLana}. */
    private final Set<String> woolesColocadas = new HashSet<>();

    /** Claves de lanas que han sido tocadas pero aún no colocadas, con formato {@code teamId_colorLana}. */
    private final Set<String> woolesTocadas = new HashSet<>();

    /**
     * Crea el scoreboard de lanas reutilizando el scoreboard de la partida
     * y registra el objetivo en la barra lateral.
     *
     * @param map   mapa actual con la lista de lanas
     * @param match partida activa de la que se obtiene el scoreboard
     */
    public WoolScoreboard(GameMap map, Match match) {
        scoreboard = match.getScoreboard();
        objective = scoreboard.registerNewObjective("wools", Criteria.DUMMY, "§6Capture the Wool");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        actualizar(map, match);
    }

    /**
     * Marca una lana como tocada para reflejarla en el scoreboard.
     *
     * @param wool lana que ha sido tocada
     */
    public void marcarTocada(WoolEntity wool) {
        woolesTocadas.add(wool.getTeam() + "_" + wool.getColor().name());
    }

    /**
     * Marca una lana como colocada en su monumento para reflejarla en el scoreboard.
     *
     * @param wool lana que ha sido colocada
     */
    public void marcarColocada(WoolEntity wool) {
        woolesColocadas.add(wool.getTeam() + "_" + wool.getColor().name());
    }

    /**
     * Recalcula y redibuja el scoreboard con el estado actual de todas las lanas.
     * Cada lana muestra un indicador de color según su estado:
     * gris si no ha sido tocada, color del equipo si fue tocada y color de la lana si fue colocada.
     *
     * @param map   mapa actual con la lista de lanas
     * @param match partida activa con la información de equipos
     */
    public void actualizar(GameMap map, Match match) {
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        int score = map.getWools().size() * 2 + 2;

        for (WoolEntity wool : map.getWools()) {
            String teamColor = map.getMapaEntity().getEquipos().stream()
                    .filter(eq -> eq.getId().equalsIgnoreCase(wool.getTeam()))
                    .findFirst()
                    .map(eq -> eq.getColor().toUpperCase())
                    .orElse("WHITE");

            ChatColor color = ChatColor.valueOf(teamColor);
            String teamName = wool.getTeam().substring(0, 1).toUpperCase() + wool.getTeam().substring(1);
            objective.getScore(color + teamName).setScore(score--);

            String woolKey = wool.getTeam() + "_" + wool.getColor().name();
            String woolColorName = wool.getColor().name().replace("_WOOL", "").toLowerCase();
            ChatColor woolChatColor = getWoolChatColor(woolColorName);
            String woolName = wool.getColor().name().replace("_WOOL", "") + " Wool";

            String woolLine;
            if (woolesColocadas.contains(woolKey)) {
                woolLine = "  " + woolChatColor + "■ " + woolName;
            } else if (woolesTocadas.contains(woolKey)) {
                woolLine = "  " + color + "■ " + woolName;
            } else {
                woolLine = "  §7■ " + woolName;
            }

            objective.getScore(woolLine).setScore(score--);
        }
    }

    /**
     * Convierte el nombre de un color de lana a su {@link ChatColor} más representativo.
     *
     * @param woolName nombre del color en minúsculas
     * @return {@link ChatColor} correspondiente, o {@link ChatColor#WHITE} si no se reconoce
     */
    private ChatColor getWoolChatColor(String woolName) {
        switch (woolName.toLowerCase()) {
            case "cyan":        return ChatColor.DARK_AQUA;
            case "orange":      return ChatColor.GOLD;
            case "red":         return ChatColor.RED;
            case "blue":        return ChatColor.BLUE;
            case "green":       return ChatColor.DARK_GREEN;
            case "yellow":      return ChatColor.YELLOW;
            case "purple":      return ChatColor.DARK_PURPLE;
            case "white":       return ChatColor.WHITE;
            case "black":       return ChatColor.BLACK;
            case "pink":        return ChatColor.LIGHT_PURPLE;
            case "lime":        return ChatColor.GREEN;
            case "magenta":     return ChatColor.LIGHT_PURPLE;
            case "gray":        return ChatColor.DARK_GRAY;
            case "light_gray":  return ChatColor.GRAY;
            default:            return ChatColor.WHITE;
        }
    }
}