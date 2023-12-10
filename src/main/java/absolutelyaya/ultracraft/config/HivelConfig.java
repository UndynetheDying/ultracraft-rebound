package absolutelyaya.ultracraft.config;

import net.minecraft.server.MinecraftServer;

public class HivelConfig extends Config
{
	public static HivelConfig INSTANCE;
	
	public final IntegerEntry jumpBoost = new IntegerEntry("JumpBoost", 2);
	public final BooleanEntry fallDamage = new BooleanEntry("FallDamage", false);
	public final BooleanEntry drowning = new BooleanEntry("Drowning", false);
	public final BooleanEntry storage = new BooleanEntry("SlamStorage", true);
	public final FloatEntry speed = new FloatEntry("Speed", 1.4f);
	public final FloatEntry gravity = (FloatEntry)new FloatEntry("Gravity", 0.5f).setRange(0f, 1f);
	public final IntegerEntry iFrames = new IntegerEntry("IFrames", 2);
	public final FloatEntry drag = new FloatEntry("MoveTechSlowdown", 1f);
	public final IntegerEntry dashTicks = new IntegerEntry("DashTicks", 3);
	public final FloatEntry staminaRegen = new FloatEntry("StaminaRegeneration", 1.5f);
	public final FloatEntry slamDamageMargin = new FloatEntry("SlamDamageMargin", 0.25f);
	public final FloatEntry strongSlamImpactMargin = new FloatEntry("StrongSlamImpactMargin", 3f);
	public final FloatEntry strongSlamImpactVelocity = new FloatEntry("StrongSlamImpactVelocity", 1f);
	
	public HivelConfig(MinecraftServer server)
	{
		super(server);
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(new Comment("         High Velocity Mode"));
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(jumpBoost);
		entries.add(new Comment("1.2 == 120% speed in hivel"));
		entries.add(speed);
		entries.add(new Comment("0.8 == 80% gravity in hivel (range: 0.0-1.0)"));
		entries.add(gravity);
		entries.add(fallDamage);
		entries.add(drowning);
		entries.add(storage);
		entries.add(iFrames);
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(new Comment("         Advanced Config"));
		entries.add(new Comment(""));
		entries.add(new Comment("If you mess these values up, that's your fault"));
		entries.add(new Comment("Delete the file and all default values will be restored"));
		entries.add(new Comment("Most Value Names are self explanatory;"));
		entries.add(new Comment("If you don't know what a value does, play around with it, but don't complain if you break something."));
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(new Comment("Internally known as \"drag\". legacy value: 0.925"));
		entries.add(drag);
		entries.add(dashTicks);
		entries.add(staminaRegen);
		entries.add(slamDamageMargin);
		entries.add(strongSlamImpactMargin);
		entries.add(strongSlamImpactVelocity);
		//TODO
		INSTANCE = this;
	}
	
	@Override
	protected String getExportPath()
	{
		return "ultracraft/";
	}
	
	@Override
	protected String getFileName()
	{
		return "hivel.properties";
	}
}
