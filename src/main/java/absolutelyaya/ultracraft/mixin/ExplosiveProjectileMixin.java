package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.accessor.ProjectileEntityAccessor;
import absolutelyaya.ultracraft.damage.DamageSources;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExplosiveProjectileEntity.class)
public abstract class ExplosiveProjectileMixin implements ProjectileEntityAccessor
{
	@Shadow public abstract boolean damage(DamageSource source, float amount);
	
	@Override
	public void setParried(boolean val, PlayerEntity parrier)
	{
		damage(DamageSources.get(parrier.getWorld(), DamageSources.PARRY, parrier), 0);
	}
}
