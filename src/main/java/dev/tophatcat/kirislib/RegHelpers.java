package dev.tophatcat.kirislib;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Methods to assist in the registering of blocks, items and sounds without having lines full of duplicated code.
 *
 * @author KiriCattus
 */
public class RegHelpers {

    public static <T extends Block> Supplier<T> createBlock(
        ResourceLocation identifier, Supplier<T> block, Map<ResourceLocation, Supplier<T>> blockMap) {
        var wrapped = Suppliers.memoize(block::get);
        blockMap.put(identifier, wrapped);
        return wrapped;
    }

    public static <T extends Block> Supplier<T> createBlockWithItem(
        ResourceLocation identifier, CreativeModeTab group, Supplier<T> block, Map<ResourceLocation, Supplier<T>> blockMap,
        Map<ResourceLocation, Supplier<Item>> itemMap) {
        var wrapped = Suppliers.memoize(block::get);
        blockMap.put(identifier, wrapped);
        createBasicItem(identifier, () -> new BlockItem(wrapped.get(),
            new Item.Properties().tab(group)), itemMap);
        return wrapped;
    }

    public static <T extends Item> Supplier<T> createBasicItem(
        ResourceLocation identifier, Supplier<T> item, Map<ResourceLocation, Supplier<T>> itemMap) {
        var wrapped = Suppliers.memoize(item::get);
        itemMap.put(identifier, wrapped);
        return wrapped;
    }

    public static SoundEvent createSound(ResourceLocation identifier, Map<SoundEvent, ResourceLocation> soundMap) {
        var soundEvent = new SoundEvent(identifier);
        soundMap.put(soundEvent, identifier);
        return soundEvent;
    }
}
