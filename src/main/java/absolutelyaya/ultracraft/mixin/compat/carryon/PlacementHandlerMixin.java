package absolutelyaya.ultracraft.mixin.compat.carryon;

import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.carry.PlacementHandler;

import java.util.function.BiFunction;

@Mixin(PlacementHandler.class)
public class PlacementHandlerMixin
{
	@Inject(method = "tryPlaceBlock", at = @At("HEAD"), cancellable = true)
	private static void onTryPlaceBlock(ServerPlayerEntity player, BlockPos pos, Direction facing, BiFunction<BlockPos, BlockState, Boolean> placementCallback, CallbackInfoReturnable<Boolean> cir)
	{
		if (player != null && UltraComponents.DIMENSION_DATA.get(player.getWorld()).isPosNotModifiable(player, pos))
			cir.setReturnValue(false);
	}
}
