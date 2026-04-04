package org.example.partida;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.example.Main;
import org.example.entity.jugador.JugadorEntity;
import org.example.entity.mapa.EquipoEntity;
import org.example.mapa.GameMap;
import org.example.scoreboard.WoolScoreboard;
import org.example.stats.StatsManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gestiona el ciclo de vida completo de una partida: cuenta atrás,
 * inicio, temporizador, fin y reparto de victorias y derrotas.
 * Controla la BossBar de cuenta atrás y la BossBar del tiempo restante.
 */
public class GameMatch {

    /** Segundos restantes de la cuenta atrás previa al inicio. */
    private int countdown = 30;

    /** Tarea periódica de la cuenta atrás. */
    private BukkitTask countdownTask;

    /** BossBar mostrada durante la cuenta atrás. */
    private BossBar countdownBar;

    /** BossBar mostrada durante la partida con el tiempo restante. */
    private BossBar matchBar;

    /** Tarea periódica del temporizador de partida. */
    private BukkitTask matchTask;

    /** Mapa en el que se disputa la partida. */
    private final GameMap gameMap;

    /** Número máximo de jugadores por equipo, determina la duración de la partida. */
    private final int playersPerTeam;

    /** IDs de equipos que han tocado al menos una lana durante la partida. */
    private final Set<String> equiposTocaron = new HashSet<>();

    /** Referencia a la partida de equipos con la asignación jugador-equipo. */
    private final Match match;

    /** Segundos restantes de la partida. */
    private int timeLeft;

    /** Indica si la partida está en curso. */
    private boolean started = false;

    /** Scoreboard de lanas que muestra el estado de los objetivos. */
    private WoolScoreboard woolScoreboard;

    /**
     * Crea una nueva instancia de GameMatch.
     * La duración se determina automáticamente según el tamaño del equipo:
     * 20 minutos para equipos de hasta 5 jugadores, 40 minutos para equipos mayores.
     *
     * @param gameMap mapa en el que se disputará la partida
     * @param match   asignación de jugadores a equipos
     */
    public GameMatch(GameMap gameMap, Match match) {
        this.gameMap = gameMap;
        this.match = match;

        EquipoEntity eq = gameMap.getMapaEntity().getEquipos().get(0);
        this.playersPerTeam = eq.getMaxPlayers();

        if (playersPerTeam <= 5) {
            timeLeft = 20 * 60;
        } else {
            timeLeft = 40 * 60;
        }
    }

    /**
     * Formatea una cantidad de segundos en el formato MM:SS.
     *
     * @param seconds segundos a formatear
     * @return cadena con el formato {@code MM:SS}
     */
    private String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    /**
     * Inicia la partida. Elimina los bloques marcados en deleteOnStart,
     * teleporta a cada jugador a su spawn de equipo, le entrega su kit y armadura,
     * activa el estado de partida y arranca el temporizador con su BossBar.
     * Si la partida ya estaba iniciada, el método no hace nada.
     */
    public void start() {
        System.out.println("GAMEMATCH START EJECUTADO");

        if (started) return;
        started = true;

        World world = Bukkit.getWorld(gameMap.getMapName());
        if (world != null && !gameMap.getDeleteOnStart().isEmpty()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                            Block block = chunk.getBlock(x, y, z);
                            if (gameMap.getDeleteOnStart().contains(block.getType())) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }

        Bukkit.broadcastMessage("§aLa partida ha comenzado.");

        for (Player p : Bukkit.getOnlinePlayers()) {
            String team = match.getTeamOf(p);
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (p == otherPlayer) continue;
                String otherTeam = match.getTeamOf(otherPlayer);
                if (team != null && otherTeam == null) {
                    p.hidePlayer(Main.get(), otherPlayer);
                } else {
                    p.showPlayer(Main.get(), otherPlayer);
                }
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.setSaturation(20);

            String team = match.getTeamOf(p);

            if (team == null) {
                p.setGameMode(GameMode.CREATIVE);
                continue;
            }

            List<Location> spawns = gameMap.getTeamSpawns(team);
            if (!spawns.isEmpty()) {
                p.teleport(spawns.get(0));
            }

            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();

            List<ItemStack> kit = gameMap.getSpawnKit();
            for (int i = 0; i < kit.size(); i++) {
                ItemStack item = kit.get(i);
                if (item != null) {
                    p.getInventory().setItem(i, item.clone());
                }
            }

            String kitId = team + "-kit";
            List<ItemStack> armor = gameMap.getTeamArmor(kitId);
            ItemStack[] armorArray = new ItemStack[4];
            for (int i = 0; i < 4; i++) {
                ItemStack piece = armor.get(i);
                armorArray[i] = piece != null ? piece.clone() : null;
            }
            p.getInventory().setArmorContents(armorArray);

            Bukkit.broadcastMessage("DEBUG TEAM OF " + p.getName() + ": " + match.getTeamOf(p));
        }

        MatchManager.get().setInGame(true);

        matchBar = Bukkit.createBossBar(
                "Tiempo restante: " + formatTime(timeLeft),
                BarColor.GREEN,
                BarStyle.SOLID
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            matchBar.addPlayer(p);
        }

        int totalTime = (playersPerTeam <= 5 ? 20 * 60 : 40 * 60);
        matchTask = Bukkit.getScheduler().runTaskTimer(Main.get(), () -> {
            timeLeft--;

            matchBar.setTitle("Tiempo restante: " + formatTime(timeLeft));
            matchBar.setProgress(Math.max(0, (double) timeLeft / totalTime));

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.setPlayerListHeader("§bTiempo restante: §e" + formatTime(timeLeft));
            }

            if (timeLeft % 60 == 0 || timeLeft <= 10) {
                Bukkit.broadcastMessage("§bTiempo restante: §e" + formatTime(timeLeft));
            }

            if (timeLeft <= 0) {
                if (equiposTocaron.size() == 1) {
                    end(equiposTocaron.iterator().next());
                } else {
                    end(null);
                }
            }
        }, 20L, 20L);
    }

    /**
     * Finaliza la partida, cancela el temporizador, registra victorias y derrotas
     * y notifica a los jugadores. Si la partida no estaba en curso, no hace nada.
     *
     * @param winnerTeam ID del equipo ganador, o {@code null} si hubo empate
     */
    public void end(String winnerTeam) {
        if (!started) return;
        started = false;

        if (matchTask != null) matchTask.cancel();
        if (matchBar != null) matchBar.removeAll();

        MatchManager.get().setInGame(false);
        MatchManager.get().setNeedsCycle(true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            String team = match.getTeamOf(p);
            if (team == null) continue;
            JugadorEntity j = StatsManager.get().getJugador(p.getUniqueId(), p.getName());
            if (winnerTeam != null && team.equalsIgnoreCase(winnerTeam)) {
                j.addWin();
            } else {
                j.addLoss();
            }
        }

        Bukkit.broadcastMessage("§cLa partida ha terminado.");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setPlayerListHeader("");

            p.setGameMode(GameMode.CREATIVE);
            p.setPlayerListHeader("§cPartida terminada");
            p.setPlayerListFooter("§7Esperando siguiente mapa...");
        }
    }

    /**
     * Inicia la cuenta atrás previa al comienzo de la partida mostrando
     * una BossBar que se va vaciando. Al llegar a cero llama a {@link #start()}.
     * Si la partida ya ha comenzado, no hace nada.
     *
     * @param seconds duración de la cuenta atrás en segundos
     */
    public void startCountdown(int seconds) {
        if (started) return;

        this.countdown = seconds;

        countdownBar = Bukkit.createBossBar(
                "La partida comienza en " + countdown + "s",
                BarColor.BLUE,
                BarStyle.SOLID
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            countdownBar.addPlayer(p);
        }

        countdownBar.setProgress(1.0);
        Bukkit.broadcastMessage("§aLa partida comenzará en §e" + countdown + " §asegundos.");

        countdownTask = Bukkit.getScheduler().runTaskTimer(Main.get(), () -> {
            countdown--;

            countdownBar.setTitle("La partida comienza en " + countdown + "s");
            countdownBar.setProgress(Math.max(0, (double) countdown / seconds));

            if (countdown == seconds || countdown == 10 || countdown <= 5) {
                Bukkit.broadcastMessage("§bLa partida comienza en §e" + countdown + "§bs.");
            }

            if (countdown <= 0) {
                countdownTask.cancel();
                countdownBar.removeAll();
                start();
            }
        }, 20L, 20L);
    }

    /**
     * Añade un jugador a la BossBar del temporizador de partida.
     * No tiene efecto si la BossBar no está activa.
     *
     * @param p jugador a añadir
     */
    public void addPlayerToMatchBar(Player p) {
        if (matchBar != null) matchBar.addPlayer(p);
    }

    /**
     * @return {@code true} si la partida está en curso
     */
    public boolean isStarted() { return started; }

    /**
     * @param woolScoreboard scoreboard de lanas a asignar
     */
    public void setWoolScoreboard(WoolScoreboard woolScoreboard) { this.woolScoreboard = woolScoreboard; }

    /**
     * @return scoreboard de lanas activo, o {@code null} si no se ha asignado
     */
    public WoolScoreboard getWoolScoreboard() { return woolScoreboard; }

    /**
     * @return conjunto de IDs de equipos que han tocado alguna lana
     */
    public Set<String> getEquiposTocaron() { return equiposTocaron; }
}