/*
 * KirisLib - https://github.com/tophatcats-mods/kiris-lib
 * Copyright (C) 2013-2022 <KiriCattus>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * Specifically version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 * https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 */
package dev.tophatcat.kirislib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * @author KiriCattus
 * @version 5.0.0
 * @since 13/08/22
 */
public class RegHelpers {

    /**
     * @param name     The name of your block.
     * @param block    The block class/type, e.g. StairBlock or RotatedPillarBlock for logs.
     * @param modId    Your mods MOD_ID.
     * @param itemTab  Your mods Item Group/Creative Tab.
     * @param makeItem If we should make an item for this block.
     * @param blockMap Your block map to store your blocks ready for registration.
     * @param itemMap  Your item map to store your items ready for registration.
     * @param <T>      .
     * @return The block we are creating.
     */
    public static <T extends Block> T createBlock(
        String name, T block, String modId, CreativeModeTab itemTab, boolean makeItem,
        Map<Block, ResourceLocation> blockMap,
        Map<Item, ResourceLocation> itemMap) {
        var resourceLocation = new ResourceLocation(modId, name);
        blockMap.put(block, resourceLocation);
        if (makeItem) {
            itemMap.put(new BlockItem(block, new Item.Properties()
                .tab(itemTab)), blockMap.get(block));
        }
        return block;
    }

    /**
     * @param name    The items name.
     * @param item    The item class/Type, e.g. BigBanHammerCustomItem.
     * @param modId   Your mods MOD_ID.
     * @param itemMap Your mods itemMap to store your items ready for registration.
     * @param <T>     .
     * @return The item we are creating.
     */
    public static <T extends Item> T createItem(
        String name, T item, String modId, Map<Item, ResourceLocation> itemMap) {
        var resourceLocation = new ResourceLocation(modId, name);
        itemMap.put(item, resourceLocation);
        return item;
    }

    /**
     * @param name The sounds name.
     * @param modId The mods MOD_ID.
     * @param soundMap Your mods soundMap to store your sounds ready for registration.
     * @return The sound we are creating.
     */
    public static SoundEvent createSound(String name, String modId, Map<SoundEvent, ResourceLocation> soundMap) {
        var resourceLocation = new ResourceLocation(modId, name);
        var soundEvent = new SoundEvent(resourceLocation);
        soundMap.put(soundEvent, resourceLocation);
        return soundEvent;
    }
}
