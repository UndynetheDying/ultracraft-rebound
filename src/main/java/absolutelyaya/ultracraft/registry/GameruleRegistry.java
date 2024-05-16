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
	
	public static final GameRules.Key<GameRules.BooleanRule> START_AS_V1 =
			GameRuleRegistry.register("ultra-startAsV1", ULTRACATEGORY,
					GameRuleFactory.createBooleanRule(true));
	
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
