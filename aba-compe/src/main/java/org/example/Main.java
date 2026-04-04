package org.example;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.commands.CycleCommand;
import org.example.commands.EndCommand;
import org.example.commands.StartCommand;
import org.example.commands.StatsCommand;
import org.example.database.SupabaseClient;
import org.example.listeners.*;
import org.example.mapa.GameMap;
import org.example.partida.GameMatch;
import org.example.partida.Match;
import org.example.partida.MatchManager;
import org.example.scoreboard.WoolScoreboard;

/**
 * Clase principal del plugin. Gestiona el ciclo de vida del servidor:
 * carga del mapa, inicialización de la base de datos, registro de comandos y listeners,
 * y creación de la partida al arrancar.
 */
public class Main extends JavaPlugin {

    /** Instancia única del plugin. */
    private static Main instance;

    /** Mapa actualmente cargado. */
    private GameMap currentMap;

    /** Partida actualmente en curso. */
    private GameMatch match;

    /**
     * Se ejecuta al habilitar el plugin. Carga el mapa, inicializa Supabase,
     * crea la partida y registra comandos y listeners con un delay para
     * asegurar que los mundos estén completamente cargados.
     */
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                world.setAutoSave(false);
            }

            currentMap = new GameMap(this, "Abstract");
            currentMap.load();

            String supabaseUrl = getConfig().getString("supabase.url");
            String supabaseKey = getConfig().getString("supabase.key");
            SupabaseClient.init(supabaseUrl, supabaseKey);

            MatchManager.get().createPartida(currentMap.getMapaEntity());
            Match partida = MatchManager.get().getPartida();

            match = new GameMatch(currentMap, partida);
            if (!currentMap.getWools().isEmpty()) {
                WoolScoreboard woolScoreboard = new WoolScoreboard(currentMap, partida);
                match.setWoolScoreboard(woolScoreboard);
            }

            getCommand("start").setExecutor(new StartCommand(match));
            getCommand("end").setExecutor(new EndCommand(match));
            getCommand("cycle").setExecutor(new CycleCommand(this));
            getCommand("stats").setExecutor(new StatsCommand());

            getServer().getPluginManager().registerEvents(new JoinListener(), this);
            getServer().getPluginManager().registerEvents(new ProtecctionListener(), this);
            getServer().getPluginManager().registerEvents(new TeamSelectorListener(), this);
            getServer().getPluginManager().registerEvents(new TeamLeaveListener(), this);
            getServer().getPluginManager().registerEvents(new TeamSelectorInventoryListener(), this);
            getServer().getPluginManager().registerEvents(new DeathListener(), this);
            getServer().getPluginManager().registerEvents(new VoidListener(), this);
            getServer().getPluginManager().registerEvents(new ObserverProtectionListener(), this);
            getServer().getPluginManager().registerEvents(new ItemPickupListener(), this);
            getServer().getPluginManager().registerEvents(new KillRewardListener(), this);
            getServer().getPluginManager().registerEvents(new BlockDropListener(), this);
            getServer().getPluginManager().registerEvents(new RegionProtectionListener(), this);
            getServer().getPluginManager().registerEvents(new WoolListener(this), this);
            getServer().getPluginManager().registerEvents(new QuitListener(), this);

            getLogger().info("Mapa cargado correctamente después del delay.");
        }, 40L);
    }

    /**
     * Se ejecuta al deshabilitar el plugin.
     * Desactiva el autoguardado de todos los mundos cargados.
     */
    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) {
            world.setAutoSave(false);
        }
    }

    /**
     * Devuelve la instancia única del plugin.
     *
     * @return instancia singleton de Main
     */
    public static Main get() { return instance; }

    /**
     * @param map nuevo mapa a establecer como mapa actual
     */
    public void setCurrentMap(GameMap map) { this.currentMap = map; }

    /**
     * @param match nueva partida a establecer como partida actual
     */
    public void setMatch(GameMatch match) { this.match = match; }

    /** @return mapa actualmente cargado */
    public GameMap getCurrentMap() { return currentMap; }

    /** @return partida actualmente en curso */
    public GameMatch getMatch() { return match; }
}