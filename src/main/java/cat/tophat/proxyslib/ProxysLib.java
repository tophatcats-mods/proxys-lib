package cat.tophat.proxyslib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cat.tophat.proxyslib.api.IMistyBiome;
import cat.tophat.proxyslib.client.ClientHandler;
import cat.tophat.proxyslib.util.ProxysLibConfig;
import cat.tophat.proxyslib.util.ProxysLibConfig.ServerConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The main class for this mod.
 */
@Mod(ProxysLib.MODID)
public class ProxysLib {

	public ProxysLib() {
		IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus(),
				forge = MinecraftForge.EVENT_BUS;
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ProxysLibConfig.SERVER_SPECIFICATION);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.handle(mod, forge));
		mod.addListener(this::setup);
		mod.addListener(this::load);
		mod.addListener(this::reload);
		forge.addListener(this::onPlayerUpdate);
	}
	
    /**
     * The mods ID for Forge to use
     */
    public static final String MODID = "proxyslib";

    /**
     * Create a logger for the mod.
     */
    public static final Logger LOGGER = LogManager.getLogger("Proxy's Lib");

    /**
     * Add a new damage source for poisonous mist.
     */
    public static final DamageSource DAMAGE_SOURCE_MIST = new DamageSource("poisonous_mist");

    /**
     * Stores the temporary fire information until the actual fire info is updated. This is null after updating.
     */
    private static Map<Supplier<? extends Block>, Pair<Integer, Integer>> fire_info = new HashMap<>();
    
    /**
     * Stores any biome information if they are part of a misty biome
     */
    private static final Map<ResourceLocation, IMistyBiome> BIOME_INFO = new HashMap<>();
    
    /**
     * Stores if the poisonous mist is enabled.
     */
    private static boolean isPoisonousMistEnabled = true;
    
    /**
     * Stores the amount of damage the player should take per unit of time.
     */
    private static float poisonDamageLevel = 1F;
    
    /**
     * Stores the amount of time between posion damage.
     */
    private static int timeBetweenPoisonDamageInTicks = 75;
    
    /**
     * A method used to store temporary fire info and then update the information in the method.
     * 
     * @param blockSupplier The current deferred block
     * @param encouragement The block's encouragement
     * @param flammability The block's flammability
     */
    public static synchronized final void setFireInfo(Supplier<? extends Block> blockSupplier, int encouragement, int flammability) {
    	fire_info.put(blockSupplier, Pair.of(encouragement, flammability));
    }
    
    /**
     * Grabs the misty biome information if present.
     * Returns null otherwise.
     * 
     * @param location The name of the biome.
     * @return The associate misty biome information.
     */
    @Nullable
    public static IMistyBiome getMistyBiome(ResourceLocation location) {
    	return BIOME_INFO.get(location);
    }
    
    private void setup(final FMLCommonSetupEvent event) {
    	event.enqueueWork(() -> {
    		FireBlock fire = (FireBlock) Blocks.FIRE;
    		Method setFireInfo = ObfuscationReflectionHelper.findMethod(FireBlock.class, "func_180686_a", Block.class, Integer.class, Integer.class);
    		fire_info.forEach((supplier, pair) -> {
    			try {
					setFireInfo.invoke(fire, supplier.get(), pair.getLeft(), pair.getRight());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.error("Something went wrong trying to set the fire info for {} with encouragement {} and flammability {}!", supplier.get().getRegistryName(), pair.getLeft(), pair.getRight());
					e.printStackTrace();
				}
    		});
        	fire_info = null;
    	});
    }
    
    private void onPlayerUpdate(final LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (isPoisonousMistEnabled
                && entity instanceof PlayerEntity
                && !((PlayerEntity) entity).isCreative()
                && (entity).ticksExisted % timeBetweenPoisonDamageInTicks == 0) {
        	World world = entity.world;
        	@Nullable IMistyBiome biome = getMistyBiome(world.getBiome(entity.getPosition()).getRegistryName());

            if (biome != null && biome.isMistPoisonous()) {
                if (world.getLightFor(LightType.BLOCK, entity.getPosition()) <= 7 || world.getLightFor(LightType.SKY, entity.getPosition()) > 10) {
                    entity.attackEntityFrom(ProxysLib.DAMAGE_SOURCE_MIST, poisonDamageLevel);
                }
            }
        }
    }
    
    private void load(final ModConfig.Loading event) {
    	revalidateCache();
    }
    
    private void reload(final ModConfig.Reloading event) {
    	revalidateCache();
    }
    
    private static void revalidateCache() {
    	ServerConfig config = ProxysLibConfig.SERVER;
    	isPoisonousMistEnabled = config.isPoisonousMistEnabled.get();
    	poisonDamageLevel = config.poisonDamageLevel.get();
    	timeBetweenPoisonDamageInTicks = config.timeBetweenPoisonDamageInTicks.get();
    }
}
