package cat.tophat.proxyslib.util;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import cat.tophat.proxyslib.ProxysLib;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

/**
 * A registry utilities class to save time and not duplicate code in multiple mods.
 */
public class RegistryUtils {
	
    /**
     * Creates a block property similar to those of wooden blocks.
     * 
     * @param properties The current block property
     * @return Added properties similar to those of wooden blocks
     */
    public static AbstractBlock.Properties giveWoodenProperties(AbstractBlock.Properties properties) {
    	return properties.harvestLevel(0).harvestTool(ToolType.AXE).sound(SoundType.WOOD);
    }
    
    /**
     * Creates a supplier of a rotated pillar block. Can be used for creating logs.
     * 
     * @param <V> A generic that extends {@link RotatedPillarBlock}
     * @param block A function that takes in the current properties and returns a block
     * @param material The material of the block
     * @param endsColor The ends color of the block. Used for maps
     * @param sideColor The side color of the block. Used for maps
     * @return A supplier of a rotated pillar block.
     */
    public static <V extends RotatedPillarBlock> Supplier<V> createRotatedPillarBlock(Function<AbstractBlock.Properties, V> block, Material material, MaterialColor endsColor, MaterialColor sideColor) {
    	return () -> block.apply(AbstractBlock.Properties.create(material, state -> state.get(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? endsColor : sideColor));
    }
    
    /**
     * Registers a block within a {@link DeferredRegister}. If the provided item register
     * is present along with the item function, then it will also add the item for the block
     * as well.
     * 
     * @param <V> A generic that extends {@link Block}
     * @param blockRegister The block register
     * @param name The name of the block
     * @param blockSupplier The block instance supplier
     * @param itemRegister The item register
     * @param blockItem The function that takes a block and creates an item supplier from it
     * @return A registry object of a block
     */
    public static <V extends Block> RegistryObject<V> registerBlock(final DeferredRegister<Block> blockRegister, final String name, final Supplier<V> blockSupplier, @Nullable DeferredRegister<Item> itemRegister, @Nullable Function<Supplier<V>, Supplier<? extends Item>> blockItem) {
    	RegistryObject<V> block = blockRegister.register(name, blockSupplier);
    	if(itemRegister != null && blockItem != null) itemRegister.register(name, blockItem.apply(block));
    	return block;
    }
    
    /**
     * Registers a block within a {@link DeferredRegister} with fire information. If the provided
     * item register is present along with the item function, then it will also add the item
     * for the block as well.
     * 
     * @param <V> A generic that extends {@link Block}
     * @param blockRegister The block register
     * @param name The name of the block
     * @param blockSupplier The block instance supplier
     * @param encouragement How fast a fire should spread to this block
     * @param flammability How likely it is for this block to catch on fire
     * @param itemRegister The item register
     * @param blockItem The function that takes a block and creates an item supplier from it
     * @return A registry object of a block
     */
    public static <V extends Block> RegistryObject<V> registerBlockWithFlammability(final DeferredRegister<Block> blockRegister, final String name, final Supplier<V> blockSupplier, final int encouragement, final int flammability, @Nullable DeferredRegister<Item> itemRegister, @Nullable Function<Supplier<V>, Supplier<? extends Item>> blockItem) {
    	RegistryObject<V> block = registerBlock(blockRegister, name, blockSupplier, itemRegister, blockItem);
    	ProxysLib.setFireInfo(block, encouragement, flammability);
    	return block;
    }
    
    /**
     * Registers a simple item with an {@link ItemGroup} within a {@link DeferredRegister}.
     * 
     * @param <V> A generic that extends {@link Item}
     * @param register The item register
     * @param name The name of the item
     * @param itemSupplier A function that takes in the current properties and returns an item
     * @param group The group or creative tab the item belongs to
     * @return A registry object of an item
     */
    public static <V extends Item> RegistryObject<V> registerSimpleItem(final DeferredRegister<Item> register, final String name, final Function<Item.Properties, V> itemSupplier, final ItemGroup group) {
    	return register.register(name, () -> itemSupplier.apply(new Item.Properties().group(group)));
    }
}
