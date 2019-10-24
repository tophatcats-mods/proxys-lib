package com.mcmoddev.proxyslib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main class for this mod.
 */
@Mod(
        name = ProxysLib.NAME,
        modid = ProxysLib.MODID,
        version = ProxysLib.VERSION,
        updateJSON = "https://raw.githubusercontent.com/MinecraftModDevelopmentMods/proxys-lib/master/update.json",
        certificateFingerprint = "@FINGERPRINT@",
        acceptedMinecraftVersions = "[1.12, 1.12.2]")
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
    public static final String VERSION = "1.2.0";

    /**
     * Create a logger for the mod.
     */
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    /**
     * @param event The event fired when the jars fingerprint is incorrect/missing.
     */
    @Mod.EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOGGER.error("Invalid fingerprint detected! The file " + event.getSource().getName() +
                " may have been tampered with. This version will NOT be supported! Please download the mod from " +
                "CurseForge for a supported and signed version of the mod.");
    }
}
