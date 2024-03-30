package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.accessor.ThrownEntityAccessor;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThrownEntity.class)
public class ThrownEntityMixin implements ThrownEntityAccessor
{
	@ModifyConstant(method = "tick", constant = @Constant(floatValue = 0.99f))
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
