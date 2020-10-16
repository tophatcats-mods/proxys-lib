package cat.tophat.proxyslib.world.biome;

import cat.tophat.proxyslib.api.IMistyBiome;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

/**
 * An extended vanilla biome but with mist properties added in.
 * This WILL be removed soon, use {@link IMistyBiome} instead.
 */
@Deprecated
public class MistyBiome extends Biome {

    /**
     * The color of the mist, -1 for no mist at all.
     */
    public int mistColor = -1;

    /**
     * The density and how close the mist is, 1.0F for stock vanilla feel.
     */
    public float mistDensity = 1.0F;

    /**
     * @param properties The biomes properties.
     */
    public MistyBiome(BiomeProperties properties) {
        super(properties);
    }

    /**
     * @param pos The position of the player.
     * @return The color of the mist in hex format.
     */
    public int getMistColor(BlockPos pos) {
        return mistColor;
    }

    /**
     * @param pos The position of the player.
     * @return The density of the mist.
     */
    public float getMistDensity(BlockPos pos) {
        return mistDensity;
    }
}