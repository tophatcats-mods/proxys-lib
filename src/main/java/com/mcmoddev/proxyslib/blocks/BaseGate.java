package com.mcmoddev.proxyslib.blocks;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;

public class BaseGate extends BlockFenceGate {

    public BaseGate() {
        super(BlockPlanks.EnumType.OAK);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setResistance(5.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}
