package cat.tophat.proxyslib.util;

import cat.tophat.proxyslib.ProxysLib;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ProxysLibConfig {

    public static Configuration config;

    public static boolean isPoisonousMistEnabled = true;
    public static float poisonDamageLevel = 1.0F;
    public static int timeBetweenPoisonDamageInTicks = 75;

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);
        config.load();

        Property isPoisonousMistEnabled = config.get("general", "Is Poisonous Mist Enabled",
                "true",
                "Is the poisonous effect of the mist enabled. (Is only enabled in some biomes by default)");
        Property poisonDamageLevel = config.get("general", "Poisonous Mist Damage Level",
                1.0F, "The level of damage the player should take per tick.");
        Property timeBetweenPoisonDamageInTicks = config.get("general", "Time Between Mist Damage",
                75, "How many ticks before the player takes damage from the poisonous mist? "
                        + "Ticks: 600 = 30 seconds, 20 ticks is 1 second");
        config.save();
        MinecraftForge.EVENT_BUS.register(ConfigChangeListener.class);
    }

    public static class ConfigChangeListener {

        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if (eventArgs.getModID().equals(ProxysLib.MODID)) {
                if (ProxysLibConfig.config != null && ProxysLibConfig.config.hasChanged()) {
                    ProxysLibConfig.config.save();
                }
            }

        }
    }
}
