package cat.tophat.proxyslib.api;

import net.minecraft.util.math.BlockPos;

/**
 * An interface to add mist and poisonous fog to your biomes.
 */
public interface IMistyBiome {

    /**
     * The colour the mist is.
     */
    public int mistColor = -1;

    /**
     * The density/thickness of the mist.
     */
    public float mistDensity = 1.0F;

    /**
     * Is the mist poisonous to players and mobs.
     *
     * @return boolean
     */
    default boolean isMistPoisonous() {
        return false;
    }

    /**
     * @param playerPos The position of the player in the world.
     * @return Set to 1.0F to have no mist at all.
     */
    default float getMistDensity(BlockPos playerPos) {
        return mistDensity;
    }

    /**
     * @param playerPos The position of the player in the world.
     * @return -1 for no color.
     */
    default int getMistColor(BlockPos playerPos) {
        return mistColor;
    }
}
