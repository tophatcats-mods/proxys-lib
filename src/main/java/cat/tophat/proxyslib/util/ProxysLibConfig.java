package cat.tophat.proxyslib.util;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;

public class ProxysLibConfig {

	public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPECIFICATION;
    
    static {
        Pair<ServerConfig, ForgeConfigSpec> specificationPair =
                new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPECIFICATION = specificationPair.getRight();
        SERVER = specificationPair.getLeft();
    }
    
    public static class ServerConfig {
    	
    	public final ForgeConfigSpec.BooleanValue isPoisonousMistEnabled;
    	public final ForgeConfigSpec.ConfigValue<Float> poisonDamageLevel;
    	public final ForgeConfigSpec.IntValue timeBetweenPoisonDamageInTicks;
    	
    	ServerConfig(ForgeConfigSpec.Builder builder) {
    		this.isPoisonousMistEnabled = builder.comment("Is the poisonous effect of the mist enabled. (Is only enabled in some biomes by default)")
    				.define("isPoisonousMistEnabled", true);
    		this.poisonDamageLevel = builder.comment("The level of damage the player should take per tick.")
    				.define("poisonDamageLevel", 1F);
    		this.timeBetweenPoisonDamageInTicks = builder.comment("How many ticks before the player takes damage from the poisonous mist?",
    				"Ticks: 600 = 30 seconds,  20 ticks is 1 second")
    				.defineInRange("timeBetweenPoisonDamageInTicks", 75, 10, Integer.MAX_VALUE);
    	}
    }
}
