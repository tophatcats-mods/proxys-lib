package com.mcmoddev.proxyslib.blocks;

import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class BaseFence extends BlockFence {

    public BaseFence(Material material, MapColor mapColor) {
        super(material, mapColor);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setResistance(5.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}