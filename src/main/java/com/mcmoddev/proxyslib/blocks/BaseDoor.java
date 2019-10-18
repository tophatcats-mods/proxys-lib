package com.mcmoddev.proxyslib.blocks;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BaseDoor extends BlockDoor {

    private Item doorItem;

    public BaseDoor(Material material) {
        super(material);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setResistance(5.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(getDoorItem());
    }

    private Item getDoorItem() {
        if (doorItem == null) {
            doorItem = Item.REGISTRY.getObject(new ResourceLocation(getRegistryName().getNamespace()));
        }
        return doorItem;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack getItem(World world, BlockPos blockPos, IBlockState state) {
        return new ItemStack(getDoorItem());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random random, int fortune) {
        return (state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) ? null : getDoorItem();
    }
}