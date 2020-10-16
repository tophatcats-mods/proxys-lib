package cat.tophat.proxyslib.items;

import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;

public class ItemBaseSlab extends ItemSlab {

    public ItemBaseSlab(BlockSlab slab, BlockSlab doubleSlab) {
        super(slab, slab, doubleSlab);
        setRegistryName(slab.getRegistryName());
    }
}
