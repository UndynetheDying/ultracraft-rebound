package absolutelyaya.ultracraft.style;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;

public class StyleBonus
{
	EntityType<?> entityType;
	DamageType damageType;
	String translationKey;
	int score;
	
	public StyleBonus(String translationKey, int score)
	{
		this.translationKey = translationKey;
		this.score = score;
	}
	
	public StyleBonus setEntityType(EntityType<?> type)
	{
		this.entityType = type;
		return this;
	}
	
	public StyleBonus setDamageType(DamageType type)
	{
		this.damageType = type;
		return this;
	}
	
	public boolean checkEntityType(EntityType<?> type)
	{
		if(this.entityType == null)
			return true;
		else
			return this.entityType.equals(type);
	}
	
	public boolean checkDamageType(DamageType type)
	{
		if(this.damageType == null)
			return true;
		else
			return this.damageType.equals(type);
	}
	
	public boolean check(EntityType<?> entityType, DamageType damageType)
	{
		return checkEntityType(entityType) && checkDamageType(damageType);
	}
}
