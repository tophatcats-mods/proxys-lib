package dev.tophatcat.kirisutils;

import net.fabricmc.api.ModInitializer;

public class KirisUtils implements ModInitializer {

    @Override
    public void onInitialize() {
        KirisUtilsCommon.init();
    }
}
