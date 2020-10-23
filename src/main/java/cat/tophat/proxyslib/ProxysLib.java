package cat.tophat.proxyslib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cat.tophat.proxyslib.util.ProxysLibConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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
		mod.addListener(this::setup);
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
     * A method used to store temporary fire info and then update the information in the method.
     * 
     * @param blockSupplier The current deferred block
     * @param encouragement The block's encouragement
     * @param flammability The block's flammability
     */
    public static synchronized final void setFireInfo(Supplier<? extends Block> blockSupplier, int encouragement, int flammability) {
    	fire_info.put(blockSupplier, Pair.of(encouragement, flammability));
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
    
    //TODO: Update
    private void onPlayerUpdate(final LivingEvent.LivingUpdateEvent event) {
        /*LivingEntity entity = event.getEntityLiving();
        World world = entity.world;
        Biome biome = world.getBiome(new BlockPos((entity).posX, (entity).posY, (entity).posZ));
        if (ProxysLibConfig.isPoisonousMistEnabled
                && entity instanceof PlayerEntity
                && !((PlayerEntity) entity).isCreative()
                && (entity).ticksExisted % ProxysLibConfig.timeBetweenPoisonDamageInTicks == 0
                && ((biome) instanceof IMistyBiome)) {

            if (((IMistyBiome) biome).isMistPoisonous()) {
                if (world.getLightFor(EnumSkyBlock.BLOCK, entity.getPosition()) <= 7 || world.getLightFor(EnumSkyBlock.SKY, entity.getPosition()) > 10) {
                    entity.attackEntityFrom(ProxysLib.DAMAGE_SOURCE_MIST, ProxysLibConfig.poisonDamageLevel);
                }
            }
        }*/
    }
}
