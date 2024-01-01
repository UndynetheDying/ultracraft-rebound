package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.registry.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin
{
	@Inject(method = "cannotConnect", at = @At("HEAD"), cancellable = true)
	private static void onCannotConnect(BlockState state, CallbackInfoReturnable<Boolean> cir)
	{
		if(state.isIn(TagRegistry.CANNOT_CONNECT))
			cir.setReturnValue(true);
	}
}
