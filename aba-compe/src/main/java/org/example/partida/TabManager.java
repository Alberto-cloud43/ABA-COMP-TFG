package org.example.partida;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.example.entity.mapa.EquipoEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona el scoreboard del TAB para mostrar los equipos con sus colores
 * y mantener el orden de aparición de los jugadores en la lista.
 */
public class TabManager {

    /** Scoreboard que controla la visualización del TAB. */
    private final Scoreboard scoreboard;

    /** Teams del scoreboard indexados por ID de equipo. */
    private final Map<String, Team> scoreboardTeams = new HashMap<>();

    /**
     * Crea una nueva instancia de TabManager con un scoreboard limpio.
     */
    public TabManager() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    /**
     * Registra un team en el scoreboard por cada equipo del mapa,
     * aplicando color, prefijo y orden de aparición en el TAB.
     * Se añade una entrada fantasma por equipo para forzar el orden visual.
     *
     * @param equipos mapa de equipos indexados por ID
     */
    public void crearEquiposEnTab(Map<String, EquipoEntity> equipos) {
        int index = 0;

        for (EquipoEntity eq : equipos.values()) {
            Team team = scoreboard.registerNewTeam(eq.getId());
            team.setDisplayName(ChatColor.valueOf(eq.getColor()) + eq.getId().toUpperCase() + " TEAM");
            team.setPrefix(ChatColor.valueOf(eq.getColor()) + "");
            team.setColor(ChatColor.valueOf(eq.getColor()));
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.addEntry("§" + index);
            index++;
            scoreboardTeams.put(eq.getId(), team);
        }
    }

    /**
     * Añade al jugador al team de su equipo en el scoreboard y le asigna
     * el scoreboard para que el TAB se actualice visualmente.
     *
     * @param p        jugador a añadir
     * @param equipoId ID del equipo al que pertenece el jugador
     */
    public void meterJugadorEnTab(Player p, String equipoId) {
        Team team = scoreboardTeams.get(equipoId);
        if (team != null) {
            team.addEntry(p.getName());
        }
        p.setScoreboard(scoreboard);
    }
}