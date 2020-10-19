package cat.tophat.proxyslib.util;

import cat.tophat.proxyslib.ProxysLib;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ProxysLib.MODID)
@Config.LangKey("proxys-lib.config")
public class ProxysLibConfig {

    @Config.Name("Is Poisonous Mist Enabled")
    @Config.Comment({"Is the poisonous effect of the mist enabled. (Is only enabled in some biomes by default)"})
    public static boolean isPoisonousMistEnabled = true;

    @Config.Name("Poisonous Mist Damage Level")
    @Config.Comment({"The level of damage the player should take per tick."})
    public static float poisonDamageLevel = 1.0F;

    @Config.Name("Time between poisonous mist damage.")
    @Config.Comment({"How many ticks before the player takes damage from the poisonous mist?", "Ticks: 600 = 30 seconds,"
            + " 20 ticks is 1 second"})
    public static int timeBetweenPoisonDamageInTicks = 75;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ProxysLib.MODID)) {
            ConfigManager.sync(ProxysLib.MODID, Config.Type.INSTANCE);
        }
    }
}
