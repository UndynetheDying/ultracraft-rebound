package absolutelyaya.ultracraft.config;

import net.minecraft.server.MinecraftServer;

public class HivelConfig extends Config
{
	public static HivelConfig INSTANCE;
	
	public final IntegerEntry hivelJumpBoost = new IntegerEntry("HiVel-JumpBoost", 2);
	public final BooleanEntry hivelFallDamage = new BooleanEntry("HiVel-FallDamage", false);
	public final BooleanEntry hivelDrowning = new BooleanEntry("HiVel-Drowning", false);
	public final BooleanEntry slamStorage = new BooleanEntry("SlamStorage", true);
	public final FloatEntry hivelSpeed = new FloatEntry("HiVel-Speed", 1.4f);
	public final FloatEntry hivelGravity = (FloatEntry)new FloatEntry("HiVel-Gravity", 0.5f).setRange(0f, 1f);
	public final IntegerEntry iFrames = new IntegerEntry("HiVel-IFrames", 2);
	
	public HivelConfig(MinecraftServer server)
	{
		super(server);
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(new Comment("         High Velocity Mode"));
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(hivelJumpBoost);
		entries.add(new Comment("1.2 == 120% speed in hivel"));
		entries.add(hivelSpeed);
		entries.add(new Comment("0.8 == 80% gravity in hivel"));
		entries.add(hivelGravity);
		entries.add(hivelFallDamage);
		entries.add(hivelDrowning);
		entries.add(slamStorage);
		entries.add(iFrames);
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(new Comment("         Advanced Config"));
		entries.add(new Comment(" ## ############################# ##  #"));
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
