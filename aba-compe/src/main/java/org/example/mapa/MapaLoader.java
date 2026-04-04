package org.example.mapa;

import org.bukkit.configuration.file.YamlConfiguration;
import org.example.entity.mapa.EquipoEntity;
import org.example.entity.mapa.MapaEntity;

import java.io.File;

/**
 * Utilidad estática para cargar un {@link MapaEntity} desde un archivo YAML.
 */
public class MapaLoader {

    /**
     * Lee un archivo YAML y construye un {@link MapaEntity} con sus equipos.
     * Si el archivo no contiene la sección "teams", devuelve el mapa sin equipos.
     *
     * @param file archivo YAML del mapa
     * @return {@link MapaEntity} con los equipos cargados, o vacío si no hay equipos definidos
     */
    public static MapaEntity cargar(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        MapaEntity mapa = new MapaEntity(
                config.getInt("id"),
                config.getString("name")
        );

        if (config.getConfigurationSection("teams") == null) {
            return mapa;
        }

        for (String key : config.getConfigurationSection("teams").getKeys(false)) {
            String color = config.getString("teams." + key + ".color");
            int max = config.getInt("teams." + key + ".max", 10);
            mapa.addEquipo(new EquipoEntity(key, color, max));
        }

        return mapa;
    }
}