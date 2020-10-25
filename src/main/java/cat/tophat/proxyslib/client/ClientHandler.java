package cat.tophat.proxyslib.client;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import cat.tophat.proxyslib.ProxysLib;
import cat.tophat.proxyslib.api.IMistyBiome;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Handles all events client side.
 */
public class ClientHandler {

	private static double mistX, mistZ;
    private static boolean mistInit;
    private static float mistFarPlaneDistance;
    private static Minecraft mc;
	
	public static void handle(IEventBus mod, IEventBus forge) {
		mod.addListener(ClientHandler::setup);
		forge.addListener(ClientHandler::onGetFogColor);
		forge.addListener(ClientHandler::onRenderFog);
	}
	
	private static void setup(final FMLClientSetupEvent event) {
		mc = event.getMinecraftSupplier().get();
	}
	
    private static void onGetFogColor(EntityViewRenderEvent.FogColors event) {
            PlayerEntity player = mc.player;
            World world = player.world;
            int x = MathHelper.floor(player.getPosX());
            int y = MathHelper.floor(player.getPosY());
            int z = MathHelper.floor(player.getPosZ());
            BlockState blockStateAtEyes = event.getInfo().getBlockAtCamera();
            if (blockStateAtEyes.getMaterial() == Material.LAVA) {
                return;
            }
            Vector3d mixedColor;
            if (blockStateAtEyes.getMaterial() == Material.WATER) {
                mixedColor = getFogBlendColorWater(world, player, x, y, z, event.getRenderPartialTicks());
            } else {
                mixedColor = getFogBlendColor(world, player, x, y, z, event.getRed(), event.getGreen(), event.getBlue(), event.getRenderPartialTicks());
            }
            if (world.getDimensionKey() == World.THE_NETHER) {
                event.setRed((float) mixedColor.x * 20.5F);
                event.setGreen((float) mixedColor.y * 20.5F);
                event.setBlue((float) mixedColor.z * 20.5F);
            } else {
                event.setRed((float) mixedColor.x);
                event.setGreen((float) mixedColor.y);
                event.setBlue((float) mixedColor.z);
            }
    }

    private static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        PlayerEntity entity = mc.player;
        World world = entity.world;
        int playerX = MathHelper.floor(entity.getPosX());
        int playerZ = MathHelper.floor(entity.getPosZ());

        if (playerX == mistX && playerZ == mistZ && mistInit) {
            renderFog(event.getType(), mistFarPlaneDistance, 0.50f);
            return;
        }

        mistInit = true;
        int distance = 20;
        float fpDistanceBiomeFog = 0F;
        float weightBiomeFog = 0;
        BlockPos.Mutable pos = new BlockPos.Mutable(0, 0, 0);

        for (int x = -distance; x <= distance; ++x) {
            for (int z = -distance; z <= distance; ++z) {
                pos.setPos(playerX + x, 0, playerZ + z);
                @Nullable IMistyBiome biome = ProxysLib.getMistyBiome(mc.getConnection().func_239165_n_().getRegistry(Registry.BIOME_KEY).getKey(world.getBiome(pos)));
                if (biome != null) {
                    float distancePart = biome.getMistDensity(pos);
                    float weightPart = 1;

                    if (x == -distance) {
                        double xDiff = 1 - (entity.getPosX() - playerX);
                        distancePart *= xDiff;
                        weightPart *= xDiff;
                    } else {
                        if (x == distance) {
                            double xDiff = (entity.getPosX() - playerX);
                            distancePart *= xDiff;
                            weightPart *= xDiff;
                        }
                    }

                    if (z == -distance) {
                        double zDiff = 1 - (entity.getPosZ() - playerZ);
                        distancePart *= zDiff;
                        weightPart *= zDiff;
                    } else {
                        if (z == distance) {
                            double zDiff = (entity.getPosZ() - playerZ);
                            distancePart *= zDiff;
                            weightPart *= zDiff;
                        }
                    }

                    fpDistanceBiomeFog += distancePart;
                    weightBiomeFog += weightPart;
                }
            }
        }

        float weightMixed = (distance * 2) * (distance * 2);
        float weightDefault = weightMixed - weightBiomeFog;
        float fpDistanceBiomeFogAvg = (weightBiomeFog == 0) ? 0 : fpDistanceBiomeFog / weightBiomeFog;
        float farPlaneDistance = (fpDistanceBiomeFog * 240 + event.getFarPlaneDistance() * weightDefault) / weightMixed;
        float farPlaneDistanceScaleBiome = (0.1f * (1 - fpDistanceBiomeFogAvg) + 0.75f * fpDistanceBiomeFogAvg);
        float farPlaneDistanceScale = (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75f * weightDefault) / weightMixed;
        mistX = entity.getPosX();
        mistZ = entity.getPosZ();
        mistFarPlaneDistance = Math.min(farPlaneDistance, event.getFarPlaneDistance());
        renderFog(event.getType(), mistFarPlaneDistance, farPlaneDistanceScale);
    }

    private static void renderFog(FogType type, float farPlaneDistance, float farPlaneDistanceScale) {
        if (type == FogRenderer.FogType.FOG_SKY) {
            GL11.glFogf(GL11.GL_FOG_START, 0.0F);
            GL11.glFogf(GL11.GL_FOG_END, farPlaneDistance);
        } else {
            GL11.glFogf(GL11.GL_FOG_START, farPlaneDistance * farPlaneDistanceScale);
            GL11.glFogf(GL11.GL_FOG_END, farPlaneDistance);
        }
    }

    private static Vector3d postProcessColor(World world, LivingEntity player, double r, double g, double b, double renderPartialTicks) {
        double darkScale = (player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * renderPartialTicks) * 0.03125; //Hardcoded the void render fog as it doesn't exist anymore
        if (player.isPotionActive(Effects.BLINDNESS)) {
            int duration = player.getActivePotionEffect(Effects.BLINDNESS).getDuration();
            darkScale *= (duration < 20) ? (1 - duration / 20f) : 0;
        }

        if (darkScale < 1) {
            darkScale = (darkScale < 0) ? 0 : darkScale * darkScale;
            r *= darkScale;
            g *= darkScale;
            b *= darkScale;
        }

        if (player.isPotionActive(Effects.NIGHT_VISION)) {
            int duration = player.getActivePotionEffect(Effects.NIGHT_VISION).getDuration();
            float brightness = (duration > 200) ? 1 : 0.7f + MathHelper.sin((float) ((duration - renderPartialTicks) * Math.PI * 0.2f)) * 0.3f;
            double scale = 1 / r;
            scale = Math.min(scale, 1 / g);
            scale = Math.min(scale, 1 / b);
            r = r * (1 - brightness) + r * scale * brightness;
            g = g * (1 - brightness) + g * scale * brightness;
            b = b * (1 - brightness) + b * scale * brightness;
        }

        return new Vector3d(r, g, b);
    }

    private static Vector3d getFogBlendColor(World world, LivingEntity playerEntity, int playerX, int playerY, int playerZ, float defR, float defG, float defB, double renderPartialTicks) {
        GameSettings settings = mc.gameSettings;
        int[] ranges = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 };
        int distance = 6;
        if (Minecraft.isFancyGraphicsEnabled() && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.length) {
            distance = ranges[settings.renderDistanceChunks];
        }

        double rBiomeFog = 0;
        double gBiomeFog = 0;
        double bBiomeFog = 0;
        double weightBiomeFog = 0;
        BlockPos.Mutable pos = new BlockPos.Mutable(0, 0, 0);

        for (int x = -distance; x <= distance; ++x) {
            for (int z = -distance; z <= distance; ++z) {
                pos.setPos(playerX + x, 0, playerZ + z);
                @Nullable IMistyBiome biome = ProxysLib.getMistyBiome(mc.getConnection().func_239165_n_().getRegistry(Registry.BIOME_KEY).getKey(world.getBiome(pos)));

                if (biome != null) {
                    int mistColour = biome.getMistColor(pos);

                    if (mistColour >= 0) {
                        double rPart = (mistColour & 0xFF0000) >> 16;
                        double gPart = (mistColour & 0x00FF00) >> 8;
                        double bPart = mistColour & 0x0000FF;
                        float weightPart = 1;

                        if (x == -distance) {
                            double xDiff = 1 - (playerEntity.getPosX() - playerX);
                            rPart *= xDiff;
                            gPart *= xDiff;
                            bPart *= xDiff;
                            weightPart *= xDiff;
                        } else {
                            if (x == distance) {
                                double xDiff = playerEntity.getPosX() - playerX;
                                rPart *= xDiff;
                                gPart *= xDiff;
                                bPart *= xDiff;
                                weightPart *= xDiff;
                            }
                        }

                        if (z == -distance) {
                            double zDiff = 1 - (playerEntity.getPosZ() - playerZ);
                            rPart *= zDiff;
                            gPart *= zDiff;
                            bPart *= zDiff;
                            weightPart *= zDiff;
                        } else {
                            if (z == distance) {
                                double zDiff = playerEntity.getPosZ() - playerZ;
                                rPart *= zDiff;
                                gPart *= zDiff;
                                bPart *= zDiff;
                                weightPart *= zDiff;
                            }
                        }

                        rBiomeFog += rPart;
                        gBiomeFog += gPart;
                        bBiomeFog += bPart;
                        weightBiomeFog += weightPart;
                    }
                }
            }
        }

        if (weightBiomeFog == 0 || distance == 0) {
            return new Vector3d(defR, defG, defB);
        }

        rBiomeFog /= 255f;
        gBiomeFog /= 255f;
        bBiomeFog /= 255f;

        float celestialAngle = world.getCelestialAngleRadians((float) renderPartialTicks);
        float baseScale = MathHelper.clamp(MathHelper.cos(celestialAngle) * 2.0F + 0.5F, 0, 1);
        double rScale = baseScale * 0.94F + 0.06F;
        double gScale = baseScale * 0.94F + 0.06F;
        double bScale = baseScale * 0.91F + 0.09F;

        float rainStrength = world.getRainStrength((float) renderPartialTicks);
        if (rainStrength > 0) {
            rScale *= 1 - rainStrength * 0.5f;
            gScale *= 1 - rainStrength * 0.5f;
            bScale *= 1 - rainStrength * 0.4f;
        }

        float thunderStrength = world.getThunderStrength((float) renderPartialTicks);
        if (thunderStrength > 0) {
            rScale *= 1 - thunderStrength * 0.5f;
            gScale *= 1 - thunderStrength * 0.5f;
            bScale *= 1 - thunderStrength * 0.5f;
        }

        rBiomeFog *= rScale / weightBiomeFog;
        gBiomeFog *= gScale / weightBiomeFog;
        bBiomeFog *= bScale / weightBiomeFog;
        Vector3d processedColor = postProcessColor(world, playerEntity, rBiomeFog, gBiomeFog, bBiomeFog, renderPartialTicks);
        rBiomeFog = processedColor.x;
        gBiomeFog = processedColor.y;
        bBiomeFog = processedColor.z;

        double weightMixed = (distance * 2) * (distance * 2);
        double weightDefault = weightMixed - weightBiomeFog;
        double rFinal = (rBiomeFog * weightBiomeFog + defR * weightDefault) / weightMixed;
        double gFinal = (gBiomeFog * weightBiomeFog + defG * weightDefault) / weightMixed;
        double bFinal = (bBiomeFog * weightBiomeFog + defB * weightDefault) / weightMixed;

        return new Vector3d(rFinal, gFinal, bFinal);
    }

    private static Vector3d getFogBlendColorWater(World world, LivingEntity playerEntity, int playerX, int playerY, int playerZ, double renderPartialTicks) {
        byte distance = 2;
        float rBiomeFog = 0.0F;
        float gBiomeFog = 0.0F;
        float bBiomeFog = 0.0F;

        float bMixed;
        for (int weight = -distance; weight <= distance; ++weight) {
            for (int respirationLevel = -distance; respirationLevel <= distance; ++respirationLevel) {
                Biome rMixed = world.getBiome(new BlockPos(playerX + weight, playerY + weight, playerZ + respirationLevel));
                int gMixed = rMixed.getWaterColor(); //Color is stored as is now and not as a multiplier, gonna leave as is for now
                bMixed = (float) ((gMixed & 16711680) >> 16);
                float gPart = (float) ((gMixed & '\uff00') >> 8);
                float bPart = (float) (gMixed & 255);
                double zDiff;
                if (weight == -distance) {
                    zDiff = 1.0D - (playerEntity.getPosX() - (double) playerX);
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                } else if (weight == distance) {
                    zDiff = playerEntity.getPosX() - (double) playerX;
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                }

                if (respirationLevel == -distance) {
                    zDiff = 1.0D - (playerEntity.getPosZ() - (double) playerZ);
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                } else if (respirationLevel == distance) {
                    zDiff = playerEntity.getPosZ() - (double) playerZ;
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                }

                rBiomeFog += bMixed;
                gBiomeFog += gPart;
                bBiomeFog += bPart;
            }
        }

        rBiomeFog /= 255.0F;
        gBiomeFog /= 255.0F;
        bBiomeFog /= 255.0F;
        float var20 = (float) (distance * 2 * distance * 2);
        float var21 = (float) EnchantmentHelper.getRespirationModifier(playerEntity) * 0.2F;
        float var22 = (rBiomeFog * 0.02F + var21) / var20;
        float var23 = (gBiomeFog * 0.02F + var21) / var20;
        bMixed = (bBiomeFog * 0.2F + var21) / var20;
        return postProcessColor(world, playerEntity, var22, var23, bMixed, renderPartialTicks);
    }
}
