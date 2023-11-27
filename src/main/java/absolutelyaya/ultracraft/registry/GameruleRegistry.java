package absolutelyaya.ultracraft.registry;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

public class GameruleRegistry
{
	static final GameRules.Category ULTRACATEGORY = ClassTinkerers.getEnum(GameRules.Category.class, "ULTRACRAFT");
	static final GameRules.Category ULTRA_WORLD_CATEGORY = ClassTinkerers.getEnum(GameRules.Category.class, "ULTRACRAFT_WORLD");
	
	//public static final GameRules.Key<EnumRule<ProjectileBoostSetting>> PROJ_BOOST =
	//		GameRuleRegistry.register("ultra-projBoost", ULTRACATEGORY, GameRuleFactory.createEnumRule(ProjectileBoostSetting.LIMITED,
	//			(server, rule) -> {
	//				switch(rule.get())
	//				{
	//					case ALLOW_ALL -> sendAdminMessage(server, Text.translatable("message.ultracraft.server.projboost-all"));
	//					case LIMITED -> sendAdminMessage(server, Text.translatable("message.ultracraft.server.projboost-limited"));
	//					case ENTITY_TAG -> sendAdminMessage(server, Text.translatable("message.ultracraft.server.projboost-tag"));
	//					case DISALLOW -> sendAdminMessage(server, Text.translatable("message.ultracraft.server.projboost-disable"));
	//				};
	//			}));
	//public static final GameRules.Key<EnumRule<Setting>> HIVEL_MODE =
	//		GameRuleRegistry.register("ultra-hiVelMode", ULTRACATEGORY, GameRuleFactory.createEnumRule(Setting.FREE,
	//				(server, rule) -> {
	//					server.getPlayerManager().getPlayerList().forEach(p -> {
	//						IWingDataComponent wings = UltraComponents.WING_DATA.get(p);
	//						if(!rule.get().equals(Setting.FREE))
	//						{
	//							wings.setVisible(rule.get().equals(Setting.FORCE_ON));
	//							wings.sync();
	//						}
	//					});
	//				}));
	//public static final GameRules.Key<EnumRule<Setting>> TIME_STOP =
	//		GameRuleRegistry.register("ultra-timeStopEffect", ULTRACATEGORY,
	//			GameRuleFactory.createEnumRule(Setting.FORCE_OFF, new Setting[] { Setting.FORCE_ON, Setting.FORCE_OFF }));
	//public static final GameRules.Key<GameRules.BooleanRule> DISABLE_HANDSWAP =
	//		GameRuleRegistry.register("ultra-disableHandSwap", ULTRACATEGORY,
	//			GameRuleFactory.createBooleanRule(false));
	//public static final GameRules.Key<GameRules.IntRule> HIVEL_JUMP_BOOST =
	//		GameRuleRegistry.register("ultra-hivelJumpBoost", ULTRACATEGORY,
	//				GameRuleFactory.createIntRule(2));
	//public static final GameRules.Key<GameRules.BooleanRule> SLAM_STORAGE =
	//		GameRuleRegistry.register("ultra-allowSlamStorage", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(true));
	//public static final GameRules.Key<GameRules.BooleanRule> HIVEL_FALLDAMAGE =
	//		GameRuleRegistry.register("ultra-hivelFallDamage", ULTRACATEGORY,
	//		GameRuleFactory.createBooleanRule(false));
	//public static final GameRules.Key<GameRules.BooleanRule> HIVEL_DROWNING =
	//		GameRuleRegistry.register("ultra-hivelDrowning", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(false));
	//public static final GameRules.Key<EnumRule<RegenSetting>> BLOODHEAL =
	//		GameRuleRegistry.register("ultra-bloodHealing", ULTRACATEGORY,
	//				GameRuleFactory.createEnumRule(RegenSetting.ALWAYS));
	//public static final GameRules.Key<GameRules.IntRule> HIVEL_SPEED =
	//		GameRuleRegistry.register("ultra-speed", ULTRACATEGORY,
	//				GameRuleFactory.createIntRule(2,
	//						(server, rule) -> {
	//					server.getPlayerManager().getPlayerList().forEach(p -> {
	//						((WingedPlayerEntity)p).updateSpeedConfig();
	//					});
	//				}));
	//public static final GameRules.Key<GameRules.IntRule> HIVEL_SLOWFALL =
	//		GameRuleRegistry.register("ultra-gravityReduction", ULTRACATEGORY,
	//				GameRuleFactory.createIntRule(4, 0, 10));
	//public static final GameRules.Key<GameRules.BooleanRule> EFFECTIVELY_VIOLENT =
	//		GameRuleRegistry.register("ultra-effectivelyViolent", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(false));
	//public static final GameRules.Key<GameRules.BooleanRule> EXPLOSION_DAMAGE =
	//		GameRuleRegistry.register("ultra-explosionBlockBreaking", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(true));
	//public static final GameRules.Key<GameRules.BooleanRule> SM_SAFE_LEDGES =
	//		GameRuleRegistry.register("ultra-swordsmachineSafeLedges", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(false));
	//public static final GameRules.Key<GameRules.BooleanRule> PARRY_CHAINING =
	//		GameRuleRegistry.register("ultra-parryChaining", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(false));
	//public static final GameRules.Key<GameRules.BooleanRule> TNT_PRIMING =
	//		GameRuleRegistry.register("ultra-tntPriming", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(true));
	//public static final GameRules.Key<GameRules.IntRule> REVOLVER_DAMAGE =
	//		GameRuleRegistry.register("ultra-revolverDamage", ULTRACATEGORY,
	//				GameRuleFactory.createIntRule(1, 1, 20));
	//public static final GameRules.Key<GameRules.IntRule> INVINCIBILITY =
	//		GameRuleRegistry.register("ultra-iFrames", ULTRACATEGORY,
	//				GameRuleFactory.createIntRule(4, 0, 20));
	//public static final GameRules.Key<GameRules.BooleanRule> TERMINAL_PROT =
	//		GameRuleRegistry.register("ultra-terminalProtection", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(true));
	//public static final GameRules.Key<EnumRule<GraffitiSetting>> GRAFFITI =
	//		GameRuleRegistry.register("ultra-graffiti", ULTRACATEGORY,
	//				GameRuleFactory.createEnumRule(GraffitiSetting.ALLOW_ALL));
	//public static final GameRules.Key<GameRules.BooleanRule> FLAMETHROWER_GRIEF =
	//		GameRuleRegistry.register("ultra-flamethrowerGrief", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(false));
	//public static final GameRules.Key<GameRules.IntRule> SHOTGUN_DAMAGE =
	//		GameRuleRegistry.register("ultra-shotgunDamage", ULTRACATEGORY,
	//				GameRuleFactory.createIntRule(1, 1, 20));
	//public static final GameRules.Key<GameRules.IntRule> NAILGUN_DAMAGE =
	//		GameRuleRegistry.register("ultra-nailgunDamage", ULTRACATEGORY,
	//				GameRuleFactory.createIntRule(1, 1, 20));
	//public static final GameRules.Key<GameRules.IntRule> HELL_OBSERVER_INTERVAL =
	//		GameRuleRegistry.register("ultra-hellObserverInterval", ULTRACATEGORY,
	//				GameRuleFactory.createIntRule(5, 1, 10));
	public static final GameRules.Key<GameRules.BooleanRule> START_WITH_PIERCER =
			GameRuleRegistry.register("ultra-startWithPiercer", ULTRA_WORLD_CATEGORY,
					GameRuleFactory.createBooleanRule(true));
	//public static final GameRules.Key<GameRules.BooleanRule> BLOOD_SATURATION =
	//		GameRuleRegistry.register("ultra-bloodSaturation", ULTRACATEGORY,
	//				GameRuleFactory.createBooleanRule(true));
	
	static void sendAdminMessage(MinecraftServer server, Text message)
	{
		server.getPlayerManager().getPlayerList().forEach(p -> {
			if(p.hasPermissionLevel(2))
				p.sendMessage(message);
		});
	}
	
	public static void register()
	{
	
	}
}
