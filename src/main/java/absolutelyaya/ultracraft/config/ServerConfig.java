package absolutelyaya.ultracraft.config;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerConfig extends Config
{
	public static ServerConfig INSTANCE;
	
	public final EnumEntry<ProjectileBoostSetting> projboost = new EnumEntry<>("ProjectileBoost", ProjectileBoostSetting.LIMITED);
	public final EnumEntry<Setting> hivel = new EnumEntry<>("HiVelMode", Setting.FREE);
	public final EnumEntry<Setting> timestop = new EnumEntry<>("TimeStop", Setting.FORCE_OFF).setValidOptions(new Setting[] {Setting.FORCE_ON, Setting.FORCE_OFF});
	public final EnumEntry<RegenSetting> bloodHeal = new EnumEntry<>("BloodHeal", RegenSetting.ALWAYS);
	public final EnumEntry<GraffitiSetting> graffiti = new EnumEntry<>("Graffiti", GraffitiSetting.ALLOW_ALL);
	public final BooleanEntry disableHandswap = new BooleanEntry("DisableHandswap", false);
	public final BooleanEntry effectivelyViolent = new BooleanEntry("EffectivelyViolent", false);
	public final BooleanEntry explosionBlockBreaking = new BooleanEntry("Explosion-BlockBreaking", true);
	public final BooleanEntry tntPriming = new BooleanEntry("Explosion-TntPriming", true);
	public final BooleanEntry smSafeLedges = new BooleanEntry("Swordsmachine-SafeLedges", false);
	public final BooleanEntry parryChaining = new BooleanEntry("ParryChaining", false);
	public final BooleanEntry terminalProtection = new BooleanEntry("TerminalProtection", true);
	public final BooleanEntry flamethrowerGrief = new BooleanEntry("FlamethrowerGrief", false);
	public final IntegerEntry hellObserverInterval = new IntegerEntry("HellObserverInterval", 5);
	public final BooleanEntry bloodSaturation = new BooleanEntry("BloodSaturation", false);
	//Weapon Damage
	public final FloatEntry revolverDamage = (FloatEntry)new FloatEntry("RevolverDamage", 1f).setRange(0f, Float.MAX_VALUE);
	public final FloatEntry shotgunDamage = (FloatEntry)new FloatEntry("ShotgunDamage", 1f).setRange(0f, Float.MAX_VALUE);
	public final FloatEntry nailgunDamage = (FloatEntry)new FloatEntry("NailgunDamage", 1f).setRange(0f, Float.MAX_VALUE);
	
	public ServerConfig(MinecraftServer server)
	{
		super(server, "server");
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(new Comment("     Welcome to Config Zone"));
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(projboost);
		entries.add(hivel);
		entries.add(timestop);
		entries.add(bloodHeal);
		entries.add(graffiti);
		entries.add(disableHandswap);
		entries.add(effectivelyViolent);
		entries.add(explosionBlockBreaking);
		entries.add(tntPriming);
		entries.add(smSafeLedges);
		entries.add(parryChaining);
		entries.add(terminalProtection);
		entries.add(flamethrowerGrief);
		entries.add(hellObserverInterval);
		entries.add(bloodSaturation);
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(new Comment("      Weapon Damage Multipliers"));
		entries.add(new Comment(" ## ############################# ##  #"));
		entries.add(revolverDamage);
		entries.add(shotgunDamage);
		entries.add(nailgunDamage);
		
		load(server);
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
		return "server.properties";
	}
	
	@Override
	public void load(MinecraftServer server)
	{
		super.load(server);
		Ultracraft.LOGGER.info("Ultracraft Server Config Loaded.");
	}
}
