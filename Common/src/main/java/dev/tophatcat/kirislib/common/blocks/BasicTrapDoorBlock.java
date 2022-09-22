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
package dev.tophatcat.kirislib.common.blocks;

import net.minecraft.world.level.block.TrapDoorBlock;

/**
 * This ONLY exists to be able to create trap door blocks as Vanilla's
 * TrapDoorBlock constructor has protected access.
 * I'd rather not Access Transform/Widen every time.
 */
public class BasicTrapDoorBlock extends TrapDoorBlock {

    /**
     * @param properties The blocks properties.
     */
    protected BasicTrapDoorBlock(final Properties properties) {
        super(properties);
    }
}
