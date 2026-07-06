package absolutelyaya.ultracraft.damage;

import absolutelyaya.ultracraft.ServerHitscanHandler;
import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class DamageSources
{
	public static final RegistryKey<DamageType> MAURICE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("maurice"));
	public static final RegistryKey<DamageType> GUN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("gun"));
	public static final RegistryKey<DamageType> SHOTGUN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("shotgun"));
	public static final RegistryKey<DamageType> SHOCKWAVE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("shockwave"));
	public static final RegistryKey<DamageType> SWORDSMACHINE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("swordsmachine"));
	public static final RegistryKey<DamageType> SLAM = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("groundpound"));
	public static final RegistryKey<DamageType> PARRY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("parry"));
	public static final RegistryKey<DamageType> PARRYAOE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("parry_collateral"));
	public static final RegistryKey<DamageType> INTERRUPT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("interrupt"));
	public static final RegistryKey<DamageType> PROJBOOST = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("projectile_boost"));
	public static final RegistryKey<DamageType> RICOCHET = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("ricochet"));
	public static final RegistryKey<DamageType> COIN_PUNCH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("coin_punch"));
	public static final RegistryKey<DamageType> CHARGEBACK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("chargeback"));
	public static final RegistryKey<DamageType> OVERCHARGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("overcharge"));
	public static final RegistryKey<DamageType> OVERCHARGE_SELF = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("overcharge_self"));
	public static final RegistryKey<DamageType> FLAMETHROWER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("flamethrower"));
	public static final RegistryKey<DamageType> SHORT_CIRCUIT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("short_circuit"));
	public static final RegistryKey<DamageType> BACK_TANK = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("back_tank"));
	public static final RegistryKey<DamageType> HARPOON = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("harpoon"));
	public static final RegistryKey<DamageType> HARPOON_RIP = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("harpoon_rip"));
	public static final RegistryKey<DamageType> SOAP = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("soap"));
	public static final RegistryKey<DamageType> NAIL = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("nail"));
	public static final RegistryKey<DamageType> MAGNET = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("magnet"));
	public static final RegistryKey<DamageType> SHARPSHOOTER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("sharpshooter"));
	public static final RegistryKey<DamageType> RETALIATION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("retaliation"));
	public static final RegistryKey<DamageType> KNUCKLE_BLAST = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("knuckle_blast"));
	public static final RegistryKey<DamageType> PUNCH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("punch"));
	public static final RegistryKey<DamageType> KNUCKLE_PUNCH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("knuckle_punch"));
	public static final RegistryKey<DamageType> CANCER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("cancer"));
	public static final RegistryKey<DamageType> PIERCER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("piercer"));
	public static final RegistryKey<DamageType> CORE_EJECT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("core_eject"));
	public static final RegistryKey<DamageType> MINECART = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("minecart"));
	public static final RegistryKey<DamageType> OBLITERATION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("obliteration"));
	public static final RegistryKey<DamageType> DEVOURED = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("devoured"));
	public static final RegistryKey<DamageType> GOOFED = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("goofed"));
	public static final RegistryKey<DamageType> SKILL_ISSUE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("skill_issue"));
	public static final RegistryKey<DamageType> HELL_BULLET = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("hell_bullet"));
	public static final RegistryKey<DamageType> EXPLOSION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("explosion"));
	public static final RegistryKey<DamageType> SAW = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("saw"));
	public static final RegistryKey<DamageType> SAW_MELEE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("saw_melee"));
	public static final RegistryKey<DamageType> JUMPSTART = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("jumpstart"));
	
	public static DamageSource get(World world, RegistryKey<DamageType> type)
	{
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type));
	}
	
	public static DamageSource get(World world, RegistryKey<DamageType> type, Entity attacker)
	{
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type), attacker);
	}
	
	public static HitscanDamageSource getHitscan(World world, RegistryKey<DamageType> type, Entity attacker, ServerHitscanHandler.Hitscan hitscan)
	{
		return new HitscanDamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type), attacker, hitscan);
	}
	
	public static DamageSource get(World world, RegistryKey<DamageType> type, Entity source, Entity attacker)
	{
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type), source, attacker);
	}
	
	public static HitscanDamageSource getHitscan(World world, RegistryKey<DamageType> type, Entity source, Entity attacker, ServerHitscanHandler.Hitscan hitscan)
	{
		return new HitscanDamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(type), source, attacker, hitscan);
	}
}
