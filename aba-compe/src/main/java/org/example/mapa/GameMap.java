package org.example.mapa;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.example.Main;
import org.example.entity.mapa.BlockDropEntity;
import org.example.entity.mapa.EquipoEntity;
import org.example.entity.mapa.MapaEntity;
import org.example.entity.mapa.RegionEntity;
import org.example.entity.objetivo.WoolEntity;

import java.io.File;
import java.util.*;

/**
 * Representa un mapa jugable del plugin.
 * Carga y almacena toda la configuración del mapa desde su archivo map.yml:
 * spawns, equipos, kits, armaduras, regiones, lanas y drops personalizados.
 */
public class GameMap {

    /** Kit base que reciben los jugadores al aparecer. */
    private final List<ItemStack> spawnKit = new ArrayList<>();

    /** Ítems que se entregan al jugador al realizar un kill. */
    private final List<ItemStack> killRewards = new ArrayList<>();

    /** Drops personalizados asociados a bloques concretos. */
    private final List<BlockDropEntity> blockDrops = new ArrayList<>();

    /** Materiales que se eliminan del inventario del jugador en ciertas situaciones. */
    private final List<Material> itemRemove = new ArrayList<>();

    /** Regiones del mapa indexadas por su ID. */
    private final Map<String, RegionEntity> regions = new HashMap<>();

    /** IDs de regiones donde la construcción está siempre prohibida. */
    private final List<String> neverBuildRegions = new ArrayList<>();

    /** Regiones donde los jugadores pueden construir. */
    private final List<RegionEntity> buildRegions = new ArrayList<>();

    /** Regiones excluidas del área de construcción aunque estén dentro de buildRegions. */
    private final List<RegionEntity> excludeRegions = new ArrayList<>();

    /** Lanas (objetivos de captura) presentes en el mapa. */
    private final List<WoolEntity> wools = new ArrayList<>();

    /** Materiales que se eliminan del mundo al iniciar la partida. */
    private final List<Material> deleteOnStart = new ArrayList<>();

    /** Altura máxima de construcción permitida. */
    private int maxBuildHeight = 0;

    /** Altura mínima de construcción permitida. */
    private int minBuildHeight = 0;

    /**
     * Armadura base del spawn. Lista de 4 elementos en orden:
     * botas (0), pantalones (1), pechera (2), casco (3).
     * Las posiciones vacías son null.
     */
    private final List<ItemStack> spawnArmor = new ArrayList<>(Arrays.asList(null, null, null, null));

    /** Armaduras personalizadas por equipo, indexadas por ID de equipo en minúsculas. */
    private final Map<String, List<ItemStack>> teamArmor = new HashMap<>();

    /** Instancia principal del plugin. */
    private final Main plugin;

    /** Nombre del mapa, coincide con el directorio en maps/ y el mundo de Bukkit. */
    private final String mapName;

    /** Punto de aparición para jugadores en modo espectador. */
    private Location spectatorSpawn;

    /** Información general del mapa (ID interno y nombre legible). */
    private MapaEntity mapaEntity;

    /** Spawns de cada equipo, indexados por ID de equipo. */
    private final Map<String, List<Location>> teamSpawns = new HashMap<>();

    /**
     * Crea una nueva instancia de GameMap.
     * Los datos no se cargan hasta llamar a {@link #load()}.
     *
     * @param plugin  instancia principal del plugin
     * @param mapName nombre del mapa
     */
    public GameMap(Main plugin, String mapName) {
        this.plugin = plugin;
        this.mapName = mapName;
    }

    /**
     * Carga toda la configuración del mapa desde maps/mapName/map.yml.
     * Si el archivo no existe, registra un error y no inicializa ningún dato.
     * Si no existe un mundo con el nombre del mapa, usa el mundo principal como fallback.
     */
    public void load() {
        File mapFile = new File(plugin.getDataFolder() + "/maps/" + mapName + "/map.yml");
        System.out.println("Cargando mapa desde: " + mapFile.getPath());
        System.out.println("Existe el archivo?: " + mapFile.exists());

        if (!mapFile.exists()) {
            Bukkit.getLogger().severe("No se encontró el mapa: " + mapFile.getPath());
            return;
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(mapFile);

        World world = Bukkit.getWorld(mapName);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }


        // SPECTATOR

        double sx = cfg.getDouble("map.spectator.x");
        double sy = cfg.getDouble("map.spectator.y");
        double sz = cfg.getDouble("map.spectator.z");
        spectatorSpawn = new Location(world, sx, sy, sz);


        // MAPA ENTITY

        mapaEntity = new MapaEntity(1, cfg.getString("map.name"));

        putTeams(cfg);
        putSpawns(cfg, world);
        putKillReward(cfg);
        putDeleteOnStart(cfg);
        blockDropsMap(cfg);
        kitTeam(cfg);
        itemRemoves(cfg);
        putRegions(cfg);
        putWools(cfg);
        putArmor(cfg);
    }

    /**
     * Carga la armadura base del spawn y las armaduras específicas de cada equipo.
     * Los equipos heredan la armadura base si se declaran como parents en el YAML.
     *
     * @param cfg configuración YAML del mapa
     */
    private void putArmor(YamlConfiguration cfg) {
        if (cfg.isConfigurationSection("map.kits.spawn.armor")) {
            List<ItemStack> armor = loadArmorSection(cfg, "map.kits.spawn.armor", null);
            spawnArmor.clear();
            spawnArmor.addAll(armor);
            System.out.println("Armor spawn cargada: " + armor.size() + " piezas");
        }

        if (cfg.isConfigurationSection("map.kits")) {
            for (String kitId : cfg.getConfigurationSection("map.kits").getKeys(false)) {
                if (kitId.equals("spawn")) continue;

                String kitPath = "map.kits." + kitId;

                List<ItemStack> parentArmor = new ArrayList<>(Arrays.asList(null, null, null, null));
                if (cfg.isList(kitPath + ".parents")) {
                    for (String parent : cfg.getStringList(kitPath + ".parents")) {
                        if (parent.equals("spawn")) {
                            parentArmor = new ArrayList<>(spawnArmor);
                        }
                    }
                }

                List<ItemStack> armor = loadArmorSection(cfg, kitPath + ".armor", parentArmor);
                teamArmor.put(kitId.toLowerCase(), armor);
                System.out.println("Armor kit '" + kitId + "' cargada: " + armor.size() + " piezas");
            }
        }
    }

    /**
     * Lee una sección de armadura del YAML y devuelve una lista de 4 piezas
     * (botas, pantalones, pechera, casco). Las piezas no definidas se heredan
     * de parentArmor si se proporciona. Soporta irrompibilidad, color para
     * armadura de cuero y encantamientos.
     *
     * @param cfg         configuración YAML del mapa
     * @param path        ruta a la sección de armadura dentro del YAML
     * @param parentArmor armadura base a heredar; puede ser null
     * @return lista de 4 ItemStack con las piezas de armadura (null en posiciones vacías)
     */
    private List<ItemStack> loadArmorSection(YamlConfiguration cfg, String path, List<ItemStack> parentArmor) {
        String[] slots = {"boots", "leggings", "chestplate", "helmet"};
        List<ItemStack> result = new ArrayList<>(Arrays.asList(null, null, null, null));

        if (parentArmor != null) {
            for (int i = 0; i < 4; i++) result.set(i, parentArmor.get(i));
        }

        if (!cfg.isConfigurationSection(path)) return result;

        for (int i = 0; i < slots.length; i++) {
            String slot = slots[i];
            String slotPath = path + "." + slot;
            if (!cfg.isConfigurationSection(slotPath)) continue;

            String matName = cfg.getString(slotPath + ".material");
            if (matName == null) continue;

            Material mat = Material.matchMaterial(matName);
            if (mat == null) {
                System.out.println("ERROR armor material inválido: " + matName);
                continue;
            }

            boolean unbreakable = cfg.getBoolean(slotPath + ".unbreakable", false);
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                meta.setUnbreakable(unbreakable);

                if (meta instanceof org.bukkit.inventory.meta.LeatherArmorMeta leatherMeta) {
                    String colorHex = cfg.getString(slotPath + ".color");
                    if (colorHex != null) {
                        try {
                            int rgb = Integer.parseInt(colorHex, 16);
                            leatherMeta.setColor(Color.fromRGB(rgb));
                        } catch (NumberFormatException e) {
                            System.out.println("ERROR color inválido: " + colorHex);
                        }
                    }
                }

                if (cfg.isConfigurationSection(slotPath + ".enchantments")) {
                    for (String enchantName : cfg.getConfigurationSection(slotPath + ".enchantments").getKeys(false)) {
                        int level = cfg.getInt(slotPath + ".enchantments." + enchantName);
                        org.bukkit.enchantments.Enchantment enchantment = Registry.ENCHANTMENT.get(
                                org.bukkit.NamespacedKey.minecraft(enchantName)
                        );
                        if (enchantment != null) {
                            meta.addEnchant(enchantment, level, true);
                        } else {
                            System.out.println("ERROR encantamiento inválido en armor: " + enchantName);
                        }
                    }
                }

                item.setItemMeta(meta);
            }

            result.set(i, item);
            System.out.println("Armor pieza cargada: " + slot + " → " + matName);
        }

        return result;
    }

    /**
     * Carga los equipos del mapa y los registra en el MapaEntity.
     * Lee el ID, color y número máximo de jugadores de cada equipo.
     *
     * @param cfg configuración YAML del mapa
     */
    private void putTeams(YamlConfiguration cfg) {
        if (cfg.isConfigurationSection("map.teams")) {
            for (String key : cfg.getConfigurationSection("map.teams").getKeys(false)) {
                String id = cfg.getString("map.teams." + key + ".id");
                String color = cfg.getString("map.teams." + key + ".color");
                int max = cfg.getInt("map.teams." + key + ".max");
                EquipoEntity eq = new EquipoEntity(id, color, max);
                mapaEntity.addEquipo(eq);
            }
        }
    }

    /**
     * Carga la lista de materiales que se eliminarán del mundo al iniciar la partida.
     *
     * @param cfg configuración YAML del mapa
     */
    private void putDeleteOnStart(YamlConfiguration cfg) {
        if (cfg.isList("map.delete-on-start")) {
            for (Map<?, ?> data : cfg.getMapList("map.delete-on-start")) {
                String matName = (String) data.get("material");
                Material mat = Material.matchMaterial(matName);
                if (mat == null) continue;
                deleteOnStart.add(mat);
                System.out.println("DeleteOnStart: " + matName);
            }
        }
    }

    /**
     * Carga las recompensas de ítems que recibe un jugador al eliminar a otro.
     *
     * @param cfg configuración YAML del mapa
     */
    private void putKillReward(YamlConfiguration cfg) {
        if (cfg.isList("map.kill-rewards")) {
            for (Map<?, ?> rewardData : cfg.getMapList("map.kill-rewards")) {
                if (rewardData.containsKey("items")) {
                    for (Map<?, ?> itemData : (List<Map<?, ?>>) rewardData.get("items")) {
                        String matName = (String) itemData.get("material");
                        int amount = itemData.containsKey("amount")
                                ? ((Number) itemData.get("amount")).intValue() : 1;
                        Material mat = Material.matchMaterial(matName);
                        if (mat == null) {
                            System.out.println("ERROR kill-reward material inválido: " + matName);
                            continue;
                        }
                        killRewards.add(new ItemStack(mat, amount));
                    }
                }
            }
        }
    }

    /**
     * Carga los puntos de spawn de cada equipo a partir de la sección map.spawns.
     * Cada spawn incluye coordenadas X, Y, Z y orientación yaw.
     *
     * @param cfg   configuración YAML del mapa
     * @param world mundo donde se sitúan las posiciones de spawn
     */
    private void putSpawns(YamlConfiguration cfg, World world) {
        if (cfg.isConfigurationSection("map.spawns")) {
            for (String teamId : cfg.getConfigurationSection("map.spawns").getKeys(false)) {
                List<Location> list = new ArrayList<>();
                for (Map<?, ?> spawnData : cfg.getMapList("map.spawns." + teamId)) {
                    double x = ((Number) spawnData.get("x")).doubleValue();
                    double y = ((Number) spawnData.get("y")).doubleValue();
                    double z = ((Number) spawnData.get("z")).doubleValue();
                    float yaw = spawnData.containsKey("yaw") ? ((Number) spawnData.get("yaw")).floatValue() : 0f;
                    list.add(new Location(world, x, y, z, yaw, 0f));
                }
                teamSpawns.put(teamId.toLowerCase(), list);
            }
        }
    }

    /**
     * Carga los drops personalizados de bloques definidos en map.block-drops.
     * Cada entrada indica qué bloques afecta, qué ítems suelta y con qué herramientas,
     * así como si se aplica también con la herramienta incorrecta.
     *
     * @param cfg configuración YAML del mapa
     */
    private void blockDropsMap(YamlConfiguration cfg) {
        if (cfg.isList("map.block-drops")) {
            System.out.println("Block-drops encontrados: " + cfg.getMapList("map.block-drops").size());
            for (Map<?, ?> dropData : cfg.getMapList("map.block-drops")) {

                boolean wrongTool = dropData.containsKey("wrong-tool")
                        && Boolean.TRUE.equals(dropData.get("wrong-tool"));

                Map<?, ?> filter = (Map<?, ?>) dropData.get("filter");
                if (filter == null) continue;

                List<?> materials = (List<?>) filter.get("materials");
                if (materials == null) continue;

                List<ItemStack> drops = new ArrayList<>();
                for (Map<?, ?> itemData : (List<Map<?, ?>>) dropData.get("drops")) {
                    String matName = (String) itemData.get("material");
                    int amount = itemData.containsKey("amount")
                            ? ((Number) itemData.get("amount")).intValue() : 1;
                    Material mat = Material.matchMaterial(matName);
                    if (mat != null) {
                        drops.add(new ItemStack(mat, amount));
                    } else {
                        System.out.println("ERROR block-drop material inválido: " + matName);
                    }
                }

                List<Material> tools = new ArrayList<>();
                System.out.println("Keys en dropData: " + dropData.keySet());

                if (dropData.containsKey("tools")) {
                    for (String toolName : (List<String>) dropData.get("tools")) {
                        Material mat = Material.matchMaterial(toolName);
                        if (mat != null) {
                            tools.add(mat);
                            System.out.println("Tool registrada: " + toolName);
                        } else {
                            System.out.println("ERROR tool inválida: " + toolName);
                        }
                    }
                } else {
                    System.out.println("no se encontro tool en dropsdata");
                }

                for (Object matName : materials) {
                    Material mat = Material.matchMaterial((String) matName);
                    if (mat != null) {
                        blockDrops.add(new BlockDropEntity(mat, drops, wrongTool, tools));
                        System.out.println("BlockDrop registrado: " + matName + " wrongTool: " + wrongTool + " tools: " + tools.size());
                    } else {
                        System.out.println("ERROR block-drop filter material inválido: " + matName);
                    }
                }
            }
        }
    }

    /**
     * Carga el kit base del spawn (map.kits.spawn.items) y lo almacena en spawnKit.
     * Cada ítem se coloca en el slot de inventario indicado en el YAML.
     * Soporta irrompibilidad y encantamientos por ítem.
     *
     * @param cfg configuración YAML del mapa
     */
    private void kitTeam(YamlConfiguration cfg) {
        if (cfg.isList("map.kits.spawn.items")) {
            List<Map<?, ?>> items = cfg.getMapList("map.kits.spawn.items");
            System.out.println("Items encontrados: " + items.size());

            for (Map<?, ?> itemData : items) {
                int slot = ((Number) itemData.get("slot")).intValue();
                String matName = (String) itemData.get("material");
                boolean unbreakable = itemData.containsKey("unbreakable")
                        && Boolean.TRUE.equals(itemData.get("unbreakable"));
                int amount = itemData.containsKey("amount")
                        ? ((Number) itemData.get("amount")).intValue() : 1;

                Material mat = Material.matchMaterial(matName);
                if (mat == null) {
                    System.out.println("ERROR: Material inválido: " + matName);
                    continue;
                }

                ItemStack item = new ItemStack(mat, amount);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setUnbreakable(unbreakable);
                    if (itemData.containsKey("enchantments")) {
                        Map<?, ?> enchants = (Map<?, ?>) itemData.get("enchantments");
                        for (Map.Entry<?, ?> entry : enchants.entrySet()) {
                            String enchantName = (String) entry.getKey();
                            int level = ((Number) entry.getValue()).intValue();
                            org.bukkit.enchantments.Enchantment enchantment = Registry.ENCHANTMENT.get(
                                    org.bukkit.NamespacedKey.minecraft(enchantName)
                            );
                            if (enchantment != null) {
                                meta.addEnchant(enchantment, level, true);
                            } else {
                                System.out.println("ERROR: Encantamiento inválido: " + enchantName);
                            }
                        }
                    }
                    item.setItemMeta(meta);
                }

                while (spawnKit.size() <= slot) spawnKit.add(null);
                spawnKit.set(slot, item);
                System.out.println("Item añadido: " + matName + " en slot " + slot);
            }
        }
    }

    /**
     * Carga la lista de materiales que se eliminan del inventario del jugador
     * en ciertas situaciones, definidos en map.itemremove.items.
     *
     * @param cfg configuración YAML del mapa
     */
    private void itemRemoves(YamlConfiguration cfg) {
        if (cfg.isList("map.itemremove.items")) {
            for (String matName : cfg.getStringList("map.itemremove.items")) {
                Material mat = Material.matchMaterial(matName);
                if (mat != null) {
                    itemRemove.add(mat);
                } else {
                    System.out.println("ERROR itemremove material inválido: " + matName);
                }
            }
            System.out.println("ItemRemove cargado: " + itemRemove.size() + " materiales");
        }
    }

    /**
     * Carga las lanas (objetivos) del mapa desde map.wools.
     * Cada lana tiene un equipo propietario, un color, una posición en el mapa
     * y una posición de monumento donde debe colocarse para puntuar.
     *
     * @param cfg configuración YAML del mapa
     */
    private void putWools(YamlConfiguration cfg) {
        if (cfg.isList("map.wools")) {
            for (Map<?, ?> woolData : cfg.getMapList("map.wools")) {
                String team = (String) woolData.get("team");
                String color = (String) woolData.get("color");

                String[] loc = ((String) woolData.get("location")).split(",");
                double locX = Double.parseDouble(loc[0].trim());
                double locY = Double.parseDouble(loc[1].trim());
                double locZ = Double.parseDouble(loc[2].trim());

                Map<?, ?> monument = (Map<?, ?>) woolData.get("monument");
                String[] mon = ((String) monument.get("block")).split(",");
                double monX = Double.parseDouble(mon[0].trim());
                double monY = Double.parseDouble(mon[1].trim());
                double monZ = Double.parseDouble(mon[2].trim());


                Material mat = Material.matchMaterial(color + "_wool");
                if (mat == null) {
                    System.out.println("ERROR wool color inválido: " + color);
                    continue;
                }

                wools.add(new WoolEntity(team, mat, locX, locY, locZ, monX, monY, monZ));
                System.out.println("Wool cargada: " + team + " " + color);
            }
        }
    }

    /**
     * Carga todas las regiones del mapa: regiones de construcción prohibida (never),
     * regiones simples identificadas por ID, regiones hijas de construcción (buildRegions),
     * regiones de exclusión (excludeRegions) y los límites de altura de construcción.
     * Soporta tipos de región cuboid y rectangle.
     *
     * @param cfg configuración YAML del mapa
     */
    private void putRegions(YamlConfiguration cfg) {

        if (cfg.isList("map.regions.apply")) {
            for (Map<?, ?> applyData : cfg.getMapList("map.regions.apply")) {
                if (!"never".equals(applyData.get("block"))) continue;

                Object regionObj = applyData.get("region");
                if (regionObj instanceof List) {
                    for (Object r : (List<?>) regionObj) {
                        neverBuildRegions.add((String) r);
                        System.out.println("Never build region: " + r);
                    }
                } else if (regionObj instanceof String) {
                    neverBuildRegions.add((String) regionObj);
                    System.out.println("Never build region: " + regionObj);
                }
            }
        }


        if (cfg.isConfigurationSection("map.regions")) {
            for (String regionId : cfg.getConfigurationSection("map.regions").getKeys(false)) {
                String path = "map.regions." + regionId;
                String type = cfg.getString(path + ".type");
                if (type == null) continue;

                String minStr = cfg.getString(path + ".min");
                String maxStr = cfg.getString(path + ".max");
                if (minStr == null || maxStr == null) continue;

                if (type.equals("cuboid")) {
                    String[] min = minStr.split(",");
                    String[] max = maxStr.split(",");
                    regions.put(regionId, new RegionEntity(regionId, "cuboid",
                            Double.parseDouble(min[0].trim()), Double.parseDouble(min[1].trim()), Double.parseDouble(min[2].trim()),
                            Double.parseDouble(max[0].trim()), Double.parseDouble(max[1].trim()), Double.parseDouble(max[2].trim())
                    ));
                    System.out.println("Región cuboid cargada: " + regionId);
                }
            }
        }


        int regionCounter = 0;
        for (Map<?, ?> child : cfg.getMapList("map.regions.regions.children")) {
            String id = (String) child.get("id");
            String type = (String) child.get("type");
            if (type == null) continue;

            String minStr = (String) child.get("min");
            String maxStr = (String) child.get("max");
            if (minStr == null || maxStr == null) continue;

            if (id == null) id = "region_" + regionCounter++;

            RegionEntity region = null;

            if (type.equals("cuboid")) {
                String[] min = minStr.split(",");
                String[] max = maxStr.split(",");
                region = new RegionEntity(id, "cuboid",
                        Double.parseDouble(min[0].trim()), Double.parseDouble(min[1].trim()), Double.parseDouble(min[2].trim()),
                        Double.parseDouble(max[0].trim()), Double.parseDouble(max[1].trim()), Double.parseDouble(max[2].trim())
                );
            } else if (type.equals("rectangle")) {
                String[] min = minStr.split(",");
                String[] max = maxStr.split(",");
                region = new RegionEntity(id, "rectangle",
                        Double.parseDouble(min[0].trim()), 0, Double.parseDouble(min[1].trim()),
                        Double.parseDouble(max[0].trim()), 0, Double.parseDouble(max[1].trim())
                );
            }

            if (region != null) {
                buildRegions.add(region);
                if (child.containsKey("id")) regions.put(id, region);
                System.out.println("BuildRegion cargada: " + id);
            }
        }


        for (Map<?, ?> subtract : cfg.getMapList("map.regions.build-region.subtract")) {
            String id = (String) subtract.get("id");
            String type = (String) subtract.get("type");
            if (type == null) continue;

            String minStr = (String) subtract.get("min");
            String maxStr = (String) subtract.get("max");
            if (minStr == null || maxStr == null) continue;

            if (id == null) id = "exclude_" + excludeRegions.size();

            String[] min = minStr.split(",");
            String[] max = maxStr.split(",");
            RegionEntity region = new RegionEntity(id, "cuboid",
                    Double.parseDouble(min[0].trim()), Double.parseDouble(min[1].trim()), Double.parseDouble(min[2].trim()),
                    Double.parseDouble(max[0].trim()), Double.parseDouble(max[1].trim()), Double.parseDouble(max[2].trim())
            );
            excludeRegions.add(region);
            System.out.println("ExcludeRegion cargada: " + id);
        }

        minBuildHeight = cfg.getInt("map.regions.minBuildHeight", 0);
        maxBuildHeight = cfg.getInt("map.maxbuildheight", 0);
    }


    // Getters


    /** @return spawn del espectador */
    public Location getSpectatorSpawn() { return spectatorSpawn; }

    /** @return entidad con la información general del mapa */
    public MapaEntity getMapaEntity() { return mapaEntity; }

    /**
     * Devuelve la lista de spawns del equipo indicado.
     *
     * @param teamId ID del equipo
     * @return lista de locations de spawn, vacía si el equipo no existe
     */
    public List<Location> getTeamSpawns(String teamId) {
        return teamSpawns.getOrDefault(teamId.toLowerCase(), new ArrayList<>());
    }

    /** @return kit base de items del spawn */
    public List<ItemStack> getSpawnKit() { return spawnKit; }

    /** @return ítems de recompensa por kill */
    public List<ItemStack> getKillRewards() { return killRewards; }

    /** @return drops personalizados de bloques */
    public List<BlockDropEntity> getBlockDrops() { return blockDrops; }

    /** @return materiales que se eliminan del inventario */
    public List<Material> getItemRemove() { return itemRemove; }

    /** @return mapa de regiones indexadas por ID */
    public Map<String, RegionEntity> getRegions() { return regions; }

    /** @return IDs de regiones con construcción siempre prohibida */
    public List<String> getNeverBuildRegions() { return neverBuildRegions; }

    /** @return altura máxima de construcción */
    public int getMaxBuildHeight() { return maxBuildHeight; }

    /** @return altura mínima de construcción */
    public int getMinBuildHeight() { return minBuildHeight; }

    /** @return lista de lanas (objetivos) del mapa */
    public List<WoolEntity> getWools() { return wools; }

    /** @return regiones excluidas del área de construcción */
    public List<RegionEntity> getExcludeRegions() { return excludeRegions; }

    /** @return regiones donde se permite construir */
    public List<RegionEntity> getBuildRegions() { return buildRegions; }

    /** @return nombre del mapa */
    public String getMapName() { return mapName; }

    /** @return materiales que se eliminan al iniciar la partida */
    public List<Material> getDeleteOnStart() { return deleteOnStart; }

    /** @return armadura base del spawn */
    public List<ItemStack> getSpawnArmor() { return spawnArmor; }

    /**
     * Devuelve la armadura del kit indicado. Si el kit no tiene armadura propia,
     * devuelve la armadura base del spawn.
     *
     * @param kitId ID del kit de equipo
     * @return lista de 4 piezas de armadura
     */
    public List<ItemStack> getTeamArmor(String kitId) {
        return teamArmor.getOrDefault(kitId.toLowerCase(), spawnArmor);
    }
}