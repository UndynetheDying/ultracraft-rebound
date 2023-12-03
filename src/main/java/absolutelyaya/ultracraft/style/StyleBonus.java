package absolutelyaya.ultracraft.style;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StyleBonus
{
	Identifier id;
	EntityType<?> entityType;
	String damageType;
	String translationKey;
	int score;
	boolean useStaleness = false;
	
	public StyleBonus(Identifier id, String translationKey, int score)
	{
		this.id = id;
		this.translationKey = translationKey;
		this.score = score;
	}
	
	public StyleBonus setEntityType(EntityType<?> type)
	{
		this.entityType = type;
		return this;
	}
	
	public StyleBonus setDamageType(String type)
	{
		this.damageType = type;
		return this;
	}
	
	public StyleBonus setUseStaleness(boolean b)
	{
		useStaleness = b;
		return this;
	}
	
	public boolean isUseStaleness()
	{
		return useStaleness;
	}
	
	public Identifier getId()
	{
		return id;
	}
	
	public String getTranslationKey()
	{
		return translationKey;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public boolean checkEntityType(EntityType<?> type)
	{
		if(this.entityType == null)
			return true;
		else
			return this.entityType.equals(type);
	}
	
	public boolean checkDamageType(World world, DamageType type)
	{
		if(this.damageType == null)
			return true;
		else
		{
			if(damageType.startsWith("#"))
			{
				RegistryEntry<DamageType> dmg = world.getDamageSources().registry.getEntry(type);
				if(dmg != null)
					return dmg.isIn(TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.tryParse(damageType.substring(1))));
				return false;
			}
			else
				return world.getDamageSources().registry.get(Identifier.tryParse(damageType)).equals(type);
		}
	}
	
	public boolean check(World world, EntityType<?> entityType, DamageType damageType)
	{
		return checkEntityType(entityType) && checkDamageType(world, damageType);
	}
}
