package dev.tophatcat.kirisutils;

import dev.tophatcat.kirisutils.platform.Services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KirisUtilsCommon {

    public static final String MOD_ID = "kirisutils";
    public static final String MOD_NAME = "Kiri's Utils";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        LOG.debug("We are currently loaded via the {} mod loader in a {} environment!",
            Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
    }
}
