package dev.tophatcat.kirisutils;

import com.google.common.base.Suppliers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ContentHelpers {

    public static final LinkedHashMap<EntityType<?>, ResourceLocation> ENTITIES = new LinkedHashMap<>();
    public static final Map<ResourceLocation, Supplier<Block>> BLOCKS = new LinkedHashMap<>();
    public static final Map<ResourceLocation, Supplier<Item>> ITEMS = new LinkedHashMap<>();

    public static ResourceKey<Biome> registerBiome(ResourceLocation id) {
        return ResourceKey.create(Registries.BIOME, id);
    }

    public static <T extends Entity> EntityType<T> makeEntity(ResourceLocation id, EntityType<T> type) {
        ENTITIES.put(type, id);
        return type;
    }

    public static <T extends Block> Supplier<T> createBlock(
        ResourceLocation resourceLocation, Supplier<T> block, Map<ResourceLocation, Supplier<T>> blockMap) {
        var wrapped = Suppliers.memoize(block::get);
        blockMap.put(resourceLocation, wrapped);
        return wrapped;
    }

    public static <T extends Block> Supplier<T> createBlockWithItem(
        ResourceLocation resourceLocation, Supplier<T> block, Map<ResourceLocation, Supplier<T>> blockMap,
        Map<ResourceLocation, Supplier<Item>> itemMap) {
        var wrapped = Suppliers.memoize(block::get);
        blockMap.put(resourceLocation, wrapped);
        createBasicItem(resourceLocation, () -> new BlockItem(wrapped.get(),
            new Item.Properties()), itemMap);
        return wrapped;
    }

    public static <T extends Item> Supplier<T> createBasicItem(
        ResourceLocation identifier, Supplier<T> item, Map<ResourceLocation, Supplier<T>> itemMap) {
        var wrapped = Suppliers.memoize(item::get);
        itemMap.put(identifier, wrapped);
        return wrapped;
    }

    public static SoundEvent createSound(ResourceLocation resourceLocation, Map<SoundEvent, ResourceLocation> soundMap) {
        var soundEvent = SoundEvent.createVariableRangeEvent(resourceLocation);
        soundMap.put(soundEvent, resourceLocation);
        return soundEvent;
    }

    public static void makeBlockFamily(String modid, String familyName) {
        makePillarBlock(new ResourceLocation(modid, familyName + "_log"));
        makePillarBlock(new ResourceLocation(modid, familyName + "_stripped_log"));
        makePlanksBlock(new ResourceLocation(modid, familyName + "_planks"));
        makeLeavesBlock(new ResourceLocation(modid, familyName + "_leaves"));
        makeSlabBlock(new ResourceLocation(modid, familyName + "_slab"));
        makeFenceBlock(new ResourceLocation(modid, familyName + "_fence"));
        makeGateBlock(new ResourceLocation(modid, familyName + "_gate"));
        makeButtonBlock(new ResourceLocation(modid, familyName + "_button"));
        makePressurePlateBlock(new ResourceLocation(modid, familyName + "_pressure_plate"));
        makeTrapdoorBlock(new ResourceLocation(modid, familyName + "_trapdoor"));
        makeDoorBlock(new ResourceLocation(modid, familyName + "_door"));
    }

    public static Supplier<Block> makePillarBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new RotatedPillarBlock(
            Block.Properties.copy(Blocks.OAK_LOG)), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makePlanksBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new Block(
            Block.Properties.copy(Blocks.OAK_PLANKS)), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeLeavesBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new LeavesBlock(
            Block.Properties.copy(Blocks.OAK_LEAVES)), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeSlabBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new SlabBlock(
            Block.Properties.copy(Blocks.OAK_SLAB)), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeFenceBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new FenceBlock(
            Block.Properties.copy(Blocks.OAK_FENCE)), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeGateBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new FenceGateBlock(
            Block.Properties.copy(Blocks.OAK_FENCE_GATE), WoodType.OAK), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeButtonBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new ButtonBlock(
            Block.Properties.copy(Blocks.OAK_BUTTON), BlockSetType.OAK, 30, true), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makePressurePlateBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                Block.Properties.copy(Blocks.OAK_PRESSURE_PLATE), BlockSetType.OAK), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeTrapdoorBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new TrapDoorBlock(
            Block.Properties.copy(Blocks.OAK_TRAPDOOR), BlockSetType.OAK), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeDoorBlock(ResourceLocation id) {
        return createBlockWithItem(id, () -> new DoorBlock(
            Block.Properties.copy(Blocks.OAK_DOOR).strength(3.0F).noOcclusion().ignitedByLava(),
                BlockSetType.OAK), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeSaplingBlock(ResourceLocation id, AbstractTreeGrower generator) {
        return createBlockWithItem(id, () -> new SaplingBlock(generator,
                Block.Properties.copy(Blocks.OAK_SAPLING)), BLOCKS, ITEMS);
    }

    public static Supplier<Block> makeStairsBlock(ResourceLocation id, BlockState blockState) {
        return createBlockWithItem(id, () -> new StairBlock(blockState,
                Block.Properties.copy(Blocks.OAK_STAIRS)), BLOCKS, ITEMS);
    }

    public static Supplier<Item> makeSignItem(ResourceLocation id, Block signBlock, Block wallSignBlock) {
        return createBasicItem(id, () -> new SignItem(new Item.Properties()
            .stacksTo(16), signBlock, wallSignBlock), ITEMS);
    }

    public static Supplier<Block> makeFloorSignBlock(ResourceLocation id, WoodType signType) {
        return createBlock(id, () -> new StandingSignBlock(
            Block.Properties.copy(Blocks.OAK_SIGN), signType), BLOCKS);
    }

    public static Supplier<Block> makeWallSignBlock(ResourceLocation id, WoodType woodType) {
        return createBlock(id, () -> new WallSignBlock(
            Block.Properties.copy(Blocks.OAK_WALL_SIGN), woodType), BLOCKS);
    }

    public static Supplier<Item> makeHangingSignItem(ResourceLocation id,
                                                     Block hangingSignBlock, Block wallHangingSignBlock) {
        return createBasicItem(id, () -> new HangingSignItem(hangingSignBlock,
            wallHangingSignBlock, new Item.Properties().stacksTo(16)), ITEMS);
    }

    public static Supplier<Block> makeHangingSignBlock(ResourceLocation id, WoodType signType) {
        return createBlock(id, () -> new CeilingHangingSignBlock(
            Block.Properties.copy(Blocks.OAK_HANGING_SIGN), signType), BLOCKS);
    }

    public static Supplier<Block> makeWallHangingSignBlock(ResourceLocation id, WoodType signType) {
        return createBlock(id, () -> new WallHangingSignBlock(
            Block.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN), signType), BLOCKS);
    }
}
