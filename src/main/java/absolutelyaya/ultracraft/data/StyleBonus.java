package absolutelyaya.ultracraft.data;

import absolutelyaya.ultracraft.Ultracraft;
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
	boolean useStaleness = false, impossible = false;
	
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
	
	public void setDamageType(String type)
	{
		this.damageType = type;
	}
	
	public void setUseStaleness(boolean b)
	{
		useStaleness = b;
	}
	
	public void setImpossible()
	{
		impossible = true;
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
			{
				DamageType t = world.getDamageSources().registry.get(Identifier.tryParse(damageType));
				if(t != null)
					return t.equals(type);
				else
				{
					Ultracraft.LOGGER.error("Damagetype not found! " + id);
					return false;
				}
			}
		}
	}
	
	public boolean check(World world, EntityType<?> entityType, DamageType damageType)
	{
		if(impossible)
			return false;
		return checkEntityType(entityType) && checkDamageType(world, damageType);
	}
}
