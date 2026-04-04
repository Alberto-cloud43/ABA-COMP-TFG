package org.example.entity.mapa;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Representa la configuración de drop personalizado para un bloque concreto.
 * Define qué ítems suelta el bloque al romperse y con qué herramientas aplica.
 */
public class BlockDropEntity {

    /** Material del bloque al que se aplica este drop personalizado. */
    private final Material material;

    /** Ítems que se generan al romper el bloque. */
    private final List<ItemStack> drops;

    /**
     * Si es {@code true}, el drop solo se aplica cuando se usa una herramienta incorrecta.
     * Si es {@code false}, el drop se aplica siempre.
     */
    private final boolean wrongTool;

    /** Herramientas específicas que activan o desactivan el drop según {@code wrongTool}. */
    private final List<Material> tools;

    /**
     * Crea una nueva configuración de drop para un bloque.
     *
     * @param material  material del bloque afectado
     * @param drops     ítems que suelta el bloque al romperse
     * @param wrongTool si el drop se aplica solo con herramienta incorrecta
     * @param tools     lista de herramientas que determinan si la herramienta es correcta o no
     */
    public BlockDropEntity(Material material, List<ItemStack> drops, boolean wrongTool, List<Material> tools) {
        this.material = material;
        this.drops = drops;
        this.wrongTool = wrongTool;
        this.tools = tools;
    }

    /** @return material del bloque afectado */
    public Material getMaterial() { return material; }

    /** @return ítems que suelta el bloque al romperse */
    public List<ItemStack> getDrops() { return drops; }

    /** @return {@code true} si el drop solo aplica con herramienta incorrecta */
    public boolean isWrongTool() { return wrongTool; }

    /** @return herramientas configuradas para este drop */
    public List<Material> getTools() { return tools; }
}