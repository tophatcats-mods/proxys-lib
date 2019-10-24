package com.mcmoddev.proxyslib.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

/**
 * An extended vanilla biome but with mist properties added in.
 */
public class MistyBiome extends Biome {

    /**
     * The color of the mist
     */
    public int mistColor = -1;

    /**
     * The density and how close the mist is.
     */
    public float mistDensity = 1.0F;

    /**
     * @param properties The biomes properties.
     */
    public MistyBiome(BiomeProperties properties) {
        super(properties);
    }

    /**
     * @param pos The position.
     * @return The color of the mist.
     */
    public int getMistColor(BlockPos pos) {
        return mistColor;
    }

    /**
     * @param pos The position.
     * @return The density of the mist.
     */
    public float getMistDensity(BlockPos pos) {
        return mistDensity;
    }
}