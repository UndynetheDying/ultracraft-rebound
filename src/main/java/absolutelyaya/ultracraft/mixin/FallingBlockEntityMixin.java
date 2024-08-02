package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.block.IStateChangingFallingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity
{
	@Shadow private BlockState block;
	
	public FallingBlockEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	@Shadow public abstract void setDestroyedOnLanding();
	
	@Shadow public abstract BlockPos getFallingBlockPos();
	
	@Inject(method = "handleFallDamage", at = @At("TAIL"))
	void onHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir)
	{
		if(block.getBlock() instanceof IStateChangingFallingBlock stateChanger)
		{
			BlockState nextState = stateChanger.getLandingState(getWorld(), fallDistance, block, getFallingBlockPos());
			if(nextState == null)
				setDestroyedOnLanding();
			else
				block = nextState;
		}
	}
}
