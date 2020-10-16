package cat.tophat.proxyslib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;

public class BaseStairs extends BlockStairs {

    public BaseStairs(Block state) {
        super(state.getDefaultState());
        setLightOpacity(0);
        setHarvestLevel("axe", 0);
        setHardness(2.0f);
        setResistance(5.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}
