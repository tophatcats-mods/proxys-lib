/*
 * Spooky Biomes - https://tophatcat.dev/kiris-lib
 * Copyright (C) 2016-2022 <KiriCattus>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main class for this mod.
 */
@Mod(KirisLib.MODID)
public class KirisLib {

    public KirisLib() {
        IEventBus mod = FMLJavaModLoadingContext.get().getModEventBus(),
            forge = MinecraftForge.EVENT_BUS;
    }

    /**
     * The mods ID for Forge to use
     */
    public static final String MODID = "proxyslib";

    /**
     * Create a logger for the mod.
     */
    public static final Logger LOGGER = LogManager.getLogger("Kiri's Lib");
}
