package absolutelyaya.ultracraft.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public interface IParriable
{
	default Entity getParriableOwner()
	{
		return null;
	}
	
	void setParried(boolean val, PlayerEntity parrier);
	
	boolean isParried();
	
	boolean isParriable();
	
	PlayerEntity getParrier();
	
	void setParrier(PlayerEntity p);
}
