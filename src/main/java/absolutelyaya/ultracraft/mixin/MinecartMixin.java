package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.accessor.MinecartAccessor;
import absolutelyaya.ultracraft.damage.DamageSources;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class MinecartMixin extends Entity implements MinecartAccessor
{
	PlayerEntity parrier;
	int cooldown;
	
	public MinecartMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	@Inject(method = "tick", at = @At("HEAD"))
	void onTick(CallbackInfo ci)
	{
		if(parrier != null && getVelocity().length() < 1f)
			parrier = null;
		if(parrier != null && cooldown == 0)
		{
			Vec3d vel = getVelocity();
			getWorld().getOtherEntities(this, getBoundingBox().expand(vel.x, vel.y, vel.z).offset(vel), i -> i instanceof LivingEntity).forEach(i -> {
				i.setVelocity(getVelocity().multiply(0.75f).add(0f, 0.5f, 0f));
				i.damage(DamageSources.get(getWorld(), DamageSources.MINECART, this, parrier), (float)(getVelocity().horizontalLength() * 3f));
				setVelocity(getVelocity().multiply(0.8f));
				cooldown = 1;
			});
		}
		if(cooldown > 0)
			cooldown--;
	}
	
	@ModifyReturnValue(method = "isPushable", at = @At("RETURN"))
	boolean onIsPushable(boolean original)
	{
		if(parrier != null)
			return false;
		return original;
	}
	
	@ModifyReturnValue(method = "getMaxSpeed", at = @At("RETURN"))
	double onGetMaxSpeed(double original)
	{
		if(parrier != null)
			return 2.5;
		return original;
	}
	
	@Override
	public void parry(PlayerEntity parrier)
	{
		addVelocity(getPos().subtract(parrier.getPos()).normalize().multiply(2.5));
		this.parrier = parrier;
	}
}
