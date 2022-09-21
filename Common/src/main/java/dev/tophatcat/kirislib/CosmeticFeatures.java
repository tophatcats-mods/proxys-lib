/*
 * KirisLib - https://github.com/tophatcats-mods/kiris-lib
 * Copyright (C) 2013-2022 <KiriCattus>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * Specifically version 2.1 of the License.
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

import net.minecraft.DefaultUncaughtExceptionHandler;

import java.io.IOException;
import java.net.URL;

//TODO Feel free to edit this as you see fit, it was an attempt to make things a little easier but failed of course xD
public class CosmeticFeatures {

    private static boolean threadStarted = false;

    public static void initCosmetics() {
        if (!threadStarted) {
            var thread = new Thread(CosmeticFeatures::gatherUsers);
            thread.setName(KirisLibCommon.MOD_NAME + " Cosmetics Thread");
            thread.setDaemon(true);
            thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(KirisLibCommon.LOGGER));
            thread.start();

            threadStarted = true;
        }
    }

    private static void gatherUsers() {
        try {
            //TODO Debate if I should point this here for long long long term use or use the domain the same as
            //the rest of the json stuff I have for mods.
            //https://tophatcat.dev/mods/cos.json
            var url = new URL("https://raw.githubusercontent.com/tophatcats-mods/kiris-lib/dev/cos.json");
        } catch (IOException exception) {
            KirisLibCommon.LOGGER.warn("Couldn't load cosmetic list. Are you offline? "
                + "Is the server down? (This won't affect gameplay features, only visual additions)");
        }
    }


}
