package cat.tophat.proxyslib.blocks;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.List;
import java.util.Random;

public class BaseSapling extends BlockSapling {

    private WorldGenerator tree;

    public BaseSapling(WorldGenerator tree) {
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
        setSoundType(SoundType.PLANT);
        Blocks.FIRE.setFireInfo(this, 5, 50);
        this.tree = tree;
    }

    private void growTree(World world, BlockPos pos, IBlockState state, Random random) {
        if (!TerrainGen.saplingGrowTree(world, random, pos)) {
            return;
        }

        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 1);
        if (!tree.generate(world, random, pos)) {
            world.setBlockState(pos, getDefaultState(), 4);
        }
    }

    @Override
    public void grow(World world, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(STAGE) == 0) {
            world.setBlockState(pos, state.cycleProperty(STAGE), 4);
        } else {
            growTree(world, pos, state, rand);
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }
}
