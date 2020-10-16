package cat.tophat.proxyslib.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

/**
 * A registry utilities class to save time and not duplicate code in multiple mods.
 */
public class RegistryUtils {

    /**
     * @param block The block being registered.
     * @param modid The modid of the mod the block is being added to.
     * @param name  The name of the block in string form. e.g. "example_block" (Must be the lower case form of it's object holder entry!)
     * @param tab   The creative tab the block should be added to.
     * @return e.g. nameBlock(new ExampleBlock(), MyMod.MODID, "example_block", MyMod.CREATIVE_TAB)
     */
    public static Block nameBlock(Block block, String modid, String name, CreativeTabs tab) {
        block
                .setRegistryName(name)
                .setTranslationKey(modid + "." + name)
                .setCreativeTab(tab);
        return block;
    }

    /**
     * @param block The block being registered usually called from it's object holder class
     * @return e.g. nameBlockItem(BlockObjectHolderExample.BIRCH_PLANKS)
     */
    public static ItemBlock nameBlockItem(Block block) {
        ItemBlock item = new ItemBlock(block);
        ResourceLocation name = block.getRegistryName();
        item.setRegistryName(name);
        return item;
    }

    /**
     * @param item  The item being registered.
     * @param modid The modid of the mod the item is being added to.
     * @param name  The name of the item in string form. e.g. "example_block" (Must be the lower case form of it's object holder entry!)
     * @param tab   The creative tab the item should be added to.
     * @return e.g. nameItem(new ItemExample(), MyMod.MODID, "example_item", MyMod.CREATIVE_TAB)
     */
    public static Item nameItem(Item item, String modid, String name, CreativeTabs tab) {
        item
                .setRegistryName(name)
                .setTranslationKey(modid + "." + name)
                .setCreativeTab(tab);
        return item;
    }

    /**
     * @param item The item being rendered.
     * @param meta The metadata of the item. (In case others wish to use metadata but I do not)
     *             e.g. registerItemModel(Item.getItemFromBlock(BlockObjectHolderExamples.EXAMPLE_BLOCK), 0);
     */
    public static void registerItemModel(Item item, int meta) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
