package absolutelyaya.ultracraft.mixin.client;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.accessor.LivingEntityAccessor;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.config.HivelConfig;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntity.class)
public abstract class ClientLivingEntityMixin implements LivingEntityAccessor
{
	@Shadow public abstract void swingHand(Hand hand);
	
	@ModifyExpressionValue(method = "tickMovement", at = @At(value = "CONSTANT", args = "floatValue=0.98f"))
	float modifySlowdown(float constant)
	{
		if(this instanceof WingedPlayerEntity winged && winged instanceof ClientPlayerEntity)
			return UltraComponents.HIVEL.get(winged).shouldIgnoreSlowdown() ? 1f : constant;
		else
			return constant;
	}
	
	@ModifyReturnValue(method = "getJumpVelocity", at = @At("RETURN"))
	float onGetJumpVelocity(float original)
	{
		if(this instanceof WingedPlayerEntity winged && UltraComponents.WING_DATA.get(winged).isActive())
			return original + 0.1f * Math.max(HivelConfig.INSTANCE.jumpBoost.getValue(), 0);
		return original;
	}
	
	@Override
	public boolean punch()
	{
		if((Object)this instanceof OtherClientPlayerEntity || MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson())
			swingHand(Hand.OFF_HAND);
		return false;
	}
	
	@Override
	public float getGravityModifier()
	{
		return HivelConfig.INSTANCE.gravity.getValue();
	}
}
