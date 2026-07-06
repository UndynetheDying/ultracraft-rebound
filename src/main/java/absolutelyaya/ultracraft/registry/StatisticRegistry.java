package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class StatisticRegistry
{
	public static final Identifier COLLECT_SOUL_ORB = Ultracraft.identifier("collect_soul_orb");
	public static final Identifier COLLECT_BLOOD_ORB = Ultracraft.identifier("collect_blood_orb");
	public static final Identifier DASH = Ultracraft.identifier("dash");
	public static final Identifier SLIDE = Ultracraft.identifier("slide");
	public static final Identifier SLAM = Ultracraft.identifier("slam");
	public static final Identifier COIN_PUNCH = Ultracraft.identifier("coin_punch");
	public static final Identifier PARRY = Ultracraft.identifier("parry");
	
	static void register(Identifier id, StatFormatter formatter)
	{
		Registry.register(Registries.CUSTOM_STAT, id.getPath(), id);
		Stats.CUSTOM.getOrCreateStat(id, formatter);
	}
	
	public static void register()
	{
		register(COLLECT_SOUL_ORB, StatFormatter.DEFAULT);
		register(COLLECT_BLOOD_ORB, StatFormatter.DEFAULT);
		register(DASH, StatFormatter.DEFAULT);
		register(SLIDE, StatFormatter.DISTANCE);
		register(SLAM, StatFormatter.DEFAULT);
		register(COIN_PUNCH, StatFormatter.DEFAULT);
		register(PARRY, StatFormatter.DEFAULT);
	}
}
