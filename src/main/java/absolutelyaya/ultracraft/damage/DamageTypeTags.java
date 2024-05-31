package absolutelyaya.ultracraft.damage;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class DamageTypeTags
{
	public static final TagKey<DamageType> HITSCAN = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("hitscan"));
	public static final TagKey<DamageType> ULTRACRAFT = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("all"));
	public static final TagKey<DamageType> IS_PER_TICK = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("is_per_tick"));
	public static final TagKey<DamageType> UNDODGEABLE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("undodgeable"));
	public static final TagKey<DamageType> UNBOOSTED = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("unboosted"));
	public static final TagKey<DamageType> NO_BLEEDING = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("no_bleeding"));
	public static final TagKey<DamageType> MELEE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("melee"));
	public static final TagKey<DamageType> EXPLODE_PLUSHIE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("explode_plushie"));
	public static final TagKey<DamageType> BREAK_MAGNET = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("break_magnet"));
	public static final TagKey<DamageType> PUNCH = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("punch"));
	public static final TagKey<DamageType> REDUCED_KNOCKBACK = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("reduced_knockback"));
	public static final TagKey<DamageType> V2_BYPASS_INTRO = TagKey.of(RegistryKeys.DAMAGE_TYPE, Ultracraft.identifier("v2_bypass_intro"));
}
