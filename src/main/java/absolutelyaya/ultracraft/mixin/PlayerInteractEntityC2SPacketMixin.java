package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.accessor.ServerWorldAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInteractEntityC2SPacket.class)
public class PlayerInteractEntityC2SPacketMixin
{
	@Shadow @Final private int entityId;
	
	@ModifyReturnValue(method = "getEntity", at = @At("RETURN"))
	Entity onGetEntity(Entity original, ServerWorld world)
	{
		if(original == null)
			return ((ServerWorldAccessor)world).getHideousParts().get(entityId);
		return original;
	}
}
