package com.mcmoddev.proxyslib.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class MistyBiome extends Biome {

    public int fogColor = -1;
    public float fogDensity = 1.0F;

    public MistyBiome(BiomeProperties properties) {
        super(properties);
    }

    public int getFogColor(BlockPos pos) {
        return fogColor;
    }

    public float getFogDensity(BlockPos pos) {
        return fogDensity;
    }
}