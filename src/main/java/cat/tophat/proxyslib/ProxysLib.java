package cat.tophat.proxyslib;

import cat.tophat.proxyslib.api.IMistyBiome;
import cat.tophat.proxyslib.util.ProxysLibConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main class for this mod.
 */
@Mod(
        name = ProxysLib.NAME,
        modid = ProxysLib.MODID,
        version = ProxysLib.VERSION,
        updateJSON = "https://tophat.cat/proxys-lib/update.json",
        acceptedMinecraftVersions = "[1.11, 1.11.2]")
@Mod.EventBusSubscriber
public class ProxysLib {

    /**
     * The mods string ID for use in some minor things like the logger.
     */
    public static final String NAME = "Proxy's Lib";

    /**
     * The mods ID for Forge to use
     */
    public static final String MODID = "proxyslib";

    /**
     * The mods version number
     */
    public static final String VERSION = "1.4.0";

    /**
     * Create a logger for the mod.
     */
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    /**
     * Add a new damage source for poisonous mist.
     */
    public static final DamageSource DAMAGE_SOURCE_MIST = new DamageSource("poisonous_mist");

    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;
        Biome biome = world.getBiome(new BlockPos((entity).posX, (entity).posY, (entity).posZ));
        if (ProxysLibConfig.isPoisonousMistEnabled
                && entity instanceof EntityPlayer
                && !((EntityPlayer) entity).isCreative()
                && (entity).ticksExisted % ProxysLibConfig.timeBetweenPoisonDamageInTicks == 0
                && ((biome) instanceof IMistyBiome)) {

            if (((IMistyBiome) biome).isMistPoisonous()) {
                if (world.getLightFor(EnumSkyBlock.BLOCK, entity.getPosition()) <= 7 || world.getLightFor(EnumSkyBlock.SKY, entity.getPosition()) > 10) {
                    entity.attackEntityFrom(ProxysLib.DAMAGE_SOURCE_MIST, ProxysLibConfig.poisonDamageLevel);
                }
            }
        }
    }
}
