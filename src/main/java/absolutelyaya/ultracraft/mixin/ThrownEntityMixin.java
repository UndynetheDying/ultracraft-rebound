package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.accessor.ThrownEntityAccessor;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEntity.class)
public class ThrownEntityMixin implements ThrownEntityAccessor
{
	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "floatValue=0.99f"))
	float setSlowdown(float constant)
	{
		return useSlowdown() ? constant : 1f;
	}
	
	@Override
	public boolean useSlowdown()
	{
		return true;
	}
}
