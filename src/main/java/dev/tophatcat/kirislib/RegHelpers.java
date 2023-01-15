package dev.tophatcat.kirislib;

import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Methods to assist in the registering of blocks, items and sounds without having lines full of duplicated code.
 *
 * @author KiriCattus
 */
public class RegHelpers {

    public static <T extends Block> Supplier<T> createBlock(
        Identifier identifier, Supplier<T> block, Map<Identifier, Supplier<T>> blockMap) {
        var wrapped = Suppliers.memoize(block::get);
        blockMap.put(identifier, wrapped);
        return wrapped;
    }

    public static <T extends Block> Supplier<T> createBlockWithItem(
        Identifier identifier, ItemGroup group, Supplier<T> block, Map<Identifier, Supplier<T>> blockMap,
        Map<Identifier, Supplier<Item>> itemMap) {
        var wrapped = Suppliers.memoize(block::get);
        blockMap.put(identifier, wrapped);
        createBasicItem(identifier, () -> new BlockItem(wrapped.get(),
            new Item.Settings().group(group)), itemMap);
        return wrapped;
    }

    public static <T extends Item> Supplier<T> createBasicItem(
        Identifier identifier, Supplier<T> item, Map<Identifier, Supplier<T>> itemMap) {
        var wrapped = Suppliers.memoize(item::get);
        itemMap.put(identifier, wrapped);
        return wrapped;
    }

    public static SoundEvent createSound(Identifier identifier, Map<SoundEvent, Identifier> soundMap) {
        var soundEvent = new SoundEvent(identifier);
        soundMap.put(soundEvent, identifier);
        return soundEvent;
    }
}
