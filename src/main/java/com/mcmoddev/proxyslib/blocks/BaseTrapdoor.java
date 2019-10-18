package com.mcmoddev.proxyslib.blocks;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class BaseTrapdoor extends BlockTrapDoor {

    public BaseTrapdoor(Material material) {
        super(material);
        setSoundType(SoundType.WOOD);
        setHarvestLevel("axe", 0);
        setHardness(3F);
        setResistance(60F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}