package absolutelyaya.ultracraft.accessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ProjectileEntityAccessor extends IParriable
{
	Mutable<PlayerEntity> knockbackExplosionCauser = new Mutable<>()
	{
		PlayerEntity causer;
		
		@Override
		public PlayerEntity getValue()
		{
			return causer;
		}
		
		@Override
		public void setValue(PlayerEntity value)
		{
			causer = value;
		}
	};
	
	void onParriedCollision(HitResult hitResult);
	
	boolean isHitscanHittable(byte type);
	
	boolean isBoostable();
	
	default void setOnParried(Consumer<Integer> consumer) {}
	
	default void setIsParriable(Supplier<Boolean> supplier) {}
	
	default void onKnockedBackbyExplosion(Entity exploder)
	{
		if(exploder instanceof PlayerEntity player)
			knockbackExplosionCauser.setValue(player);
	}
	
	default PlayerEntity getKnockbackExplosionCauser()
	{
		return knockbackExplosionCauser.getValue();
	}
}
