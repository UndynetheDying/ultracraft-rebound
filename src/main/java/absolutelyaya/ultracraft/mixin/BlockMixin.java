package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.registry.TagRegistry;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public class BlockMixin
{
	@ModifyReturnValue(method = "cannotConnect", at = @At("RETURN"))
	private static boolean onCannotConnect(boolean original, @Local BlockState state)
	{
		return original || state.isIn(TagRegistry.CANNOT_CONNECT);
	}
}
