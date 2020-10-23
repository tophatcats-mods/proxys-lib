package cat.tophat.proxyslib.util;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.MixinEnvironment.Side;

import cat.tophat.proxyslib.ProxysLib;
import cat.tophat.proxyslib.api.IMistyBiome;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProxysLib.MODID, value = Side.CLIENT)
public class MistEvent {

    private static double mistX, mistZ;
    private static boolean mistInit;
    private static float mistFarPlaneDistance;

    @SubscribeEvent
    public static void onGetFogColor(EntityViewRenderEvent.FogColors event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            World world = player.world;
            int x = MathHelper.floor(player.posX);
            int y = MathHelper.floor(player.posY);
            int z = MathHelper.floor(player.posZ);
            IBlockState blockStateAtEyes = ActiveRenderInfo.getBlockStateAtEntityViewpoint(world, event.getEntity(), (float) event.getRenderPartialTicks());
            if (blockStateAtEyes.getMaterial() == Material.LAVA) {
                return;
            }
            Vec3d mixedColor;
            if (blockStateAtEyes.getMaterial() == Material.WATER) {
                mixedColor = getFogBlendColorWater(world, player, x, y, z, event.getRenderPartialTicks());
            } else {
                mixedColor = getFogBlendColor(world, player, x, y, z, event.getRed(), event.getGreen(), event.getBlue(), event.getRenderPartialTicks());
            }
            if (world.provider instanceof WorldProviderHell) {
                event.setRed((float) mixedColor.x * 20.5F);
                event.setGreen((float) mixedColor.y * 20.5F);
                event.setBlue((float) mixedColor.z * 20.5F);
            } else {
                event.setRed((float) mixedColor.x);
                event.setGreen((float) mixedColor.y);
                event.setBlue((float) mixedColor.z);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        Entity entity = event.getEntity();
        World world = entity.world;
        int playerX = MathHelper.floor(entity.posX);
        int playerY = MathHelper.floor(entity.posY);
        int playerZ = MathHelper.floor(entity.posZ);

        if (playerX == mistX && playerZ == mistZ && mistInit) {
            renderFog(event.getFogMode(), mistFarPlaneDistance, 0.50f);
            return;
        }

        mistInit = true;
        int distance = 20;
        float fpDistanceBiomeFog = 0F;
        float weightBiomeFog = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);

        for (int x = -distance; x <= distance; ++x) {
            for (int z = -distance; z <= distance; ++z) {
                pos.setPos(playerX + x, 0, playerZ + z);
                Biome biome = world.getBiome(pos);
                if (biome instanceof IMistyBiome) {
                    float distancePart = ((IMistyBiome) biome).getMistDensity(pos);
                    float weightPart = 1;

                    if (x == -distance) {
                        double xDiff = 1 - (entity.posX - playerX);
                        distancePart *= xDiff;
                        weightPart *= xDiff;
                    } else {
                        if (x == distance) {
                            double xDiff = (entity.posX - playerX);
                            distancePart *= xDiff;
                            weightPart *= xDiff;
                        }
                    }

                    if (z == -distance) {
                        double zDiff = 1 - (entity.posZ - playerZ);
                        distancePart *= zDiff;
                        weightPart *= zDiff;
                    } else {
                        if (z == distance) {
                            double zDiff = (entity.posZ - playerZ);
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
        mistX = entity.posX;
        mistZ = entity.posZ;
        mistFarPlaneDistance = Math.min(farPlaneDistance, event.getFarPlaneDistance());
        renderFog(event.getFogMode(), mistFarPlaneDistance, farPlaneDistanceScale);
    }

    private static void renderFog(int mistMode, float farPlaneDistance, float farPlaneDistanceScale) {
        if (mistMode < 0) {
            GL11.glFogf(GL11.GL_FOG_START, 0.0F);
            GL11.glFogf(GL11.GL_FOG_END, farPlaneDistance);
        } else {
            GL11.glFogf(GL11.GL_FOG_START, farPlaneDistance * farPlaneDistanceScale);
            GL11.glFogf(GL11.GL_FOG_END, farPlaneDistance);
        }
    }

    private static Vec3d postProcessColor(World world, EntityLivingBase player, double r, double g, double b, double renderPartialTicks) {
        double darkScale = (player.lastTickPosY + (player.posY - player.lastTickPosY) * renderPartialTicks) * world.provider.getVoidFogYFactor();
        if (player.isPotionActive(MobEffects.BLINDNESS)) {
            int duration = player.getActivePotionEffect(MobEffects.BLINDNESS).getDuration();
            darkScale *= (duration < 20) ? (1 - duration / 20f) : 0;
        }

        if (darkScale < 1) {
            darkScale = (darkScale < 0) ? 0 : darkScale * darkScale;
            r *= darkScale;
            g *= darkScale;
            b *= darkScale;
        }

        if (player.isPotionActive(MobEffects.NIGHT_VISION)) {
            int duration = player.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
            float brightness = (duration > 200) ? 1 : 0.7f + MathHelper.sin((float) ((duration - renderPartialTicks) * Math.PI * 0.2f)) * 0.3f;
            double scale = 1 / r;
            scale = Math.min(scale, 1 / g);
            scale = Math.min(scale, 1 / b);
            r = r * (1 - brightness) + r * scale * brightness;
            g = g * (1 - brightness) + g * scale * brightness;
            b = b * (1 - brightness) + b * scale * brightness;
        }

        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            double aR = (r * 30 + g * 59 + b * 11) / 100;
            double aG = (r * 30 + g * 70) / 100;
            double aB = (r * 30 + b * 70) / 100;

            r = aR;
            g = aG;
            b = aB;
        }

        return new Vec3d(r, g, b);
    }

    private static Vec3d getFogBlendColor(World world, EntityLivingBase playerEntity, int playerX, int playerY, int playerZ, float defR, float defG, float defB, double renderPartialTicks) {
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        int[] ranges = ForgeModContainer.blendRanges;
        int distance = 6;
        if (settings.fancyGraphics && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.length) {
            distance = ranges[settings.renderDistanceChunks];
        }

        double rBiomeFog = 0;
        double gBiomeFog = 0;
        double bBiomeFog = 0;
        double weightBiomeFog = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);

        for (int x = -distance; x <= distance; ++x) {
            for (int z = -distance; z <= distance; ++z) {
                pos.setPos(playerX + x, 0, playerZ + z);
                Biome biome = world.getBiome(pos);

                if (biome instanceof IMistyBiome) {
                    int mistColour = ((IMistyBiome) biome).getMistColor(pos);

                    if (mistColour >= 0) {
                        double rPart = (mistColour & 0xFF0000) >> 16;
                        double gPart = (mistColour & 0x00FF00) >> 8;
                        double bPart = mistColour & 0x0000FF;
                        float weightPart = 1;

                        if (x == -distance) {
                            double xDiff = 1 - (playerEntity.posX - playerX);
                            rPart *= xDiff;
                            gPart *= xDiff;
                            bPart *= xDiff;
                            weightPart *= xDiff;
                        } else {
                            if (x == distance) {
                                double xDiff = playerEntity.posX - playerX;
                                rPart *= xDiff;
                                gPart *= xDiff;
                                bPart *= xDiff;
                                weightPart *= xDiff;
                            }
                        }

                        if (z == -distance) {
                            double zDiff = 1 - (playerEntity.posZ - playerZ);
                            rPart *= zDiff;
                            gPart *= zDiff;
                            bPart *= zDiff;
                            weightPart *= zDiff;
                        } else {
                            if (z == distance) {
                                double zDiff = playerEntity.posZ - playerZ;
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
            return new Vec3d(defR, defG, defB);
        }

        rBiomeFog /= 255f;
        gBiomeFog /= 255f;
        bBiomeFog /= 255f;

        float celestialAngle = world.getCelestialAngle((float) renderPartialTicks);
        float baseScale = MathHelper.clamp(MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F, 0, 1);
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
        Vec3d processedColor = postProcessColor(world, playerEntity, rBiomeFog, gBiomeFog, bBiomeFog, renderPartialTicks);
        rBiomeFog = processedColor.x;
        gBiomeFog = processedColor.y;
        bBiomeFog = processedColor.z;

        double weightMixed = (distance * 2) * (distance * 2);
        double weightDefault = weightMixed - weightBiomeFog;
        double rFinal = (rBiomeFog * weightBiomeFog + defR * weightDefault) / weightMixed;
        double gFinal = (gBiomeFog * weightBiomeFog + defG * weightDefault) / weightMixed;
        double bFinal = (bBiomeFog * weightBiomeFog + defB * weightDefault) / weightMixed;

        return new Vec3d(rFinal, gFinal, bFinal);
    }

    private static Vec3d getFogBlendColorWater(World world, EntityLivingBase playerEntity, int playerX, int playerY, int playerZ, double renderPartialTicks) {
        byte distance = 2;
        float rBiomeFog = 0.0F;
        float gBiomeFog = 0.0F;
        float bBiomeFog = 0.0F;

        float bMixed;
        for (int weight = -distance; weight <= distance; ++weight) {
            for (int respirationLevel = -distance; respirationLevel <= distance; ++respirationLevel) {
                Biome rMixed = world.getBiomeForCoordsBody(new BlockPos(playerX + weight, playerY + weight, playerZ + respirationLevel));
                int gMixed = rMixed.getWaterColorMultiplier();
                bMixed = (float) ((gMixed & 16711680) >> 16);
                float gPart = (float) ((gMixed & '\uff00') >> 8);
                float bPart = (float) (gMixed & 255);
                double zDiff;
                if (weight == -distance) {
                    zDiff = 1.0D - (playerEntity.posX - (double) playerX);
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                } else if (weight == distance) {
                    zDiff = playerEntity.posX - (double) playerX;
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                }

                if (respirationLevel == -distance) {
                    zDiff = 1.0D - (playerEntity.posZ - (double) playerZ);
                    bMixed = (float) ((double) bMixed * zDiff);
                    gPart = (float) ((double) gPart * zDiff);
                    bPart = (float) ((double) bPart * zDiff);
                } else if (respirationLevel == distance) {
                    zDiff = playerEntity.posZ - (double) playerZ;
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
