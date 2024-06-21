package absolutelyaya.ultracraft.mixin.compat.carryon;

import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.carry.PickupHandler;

import java.util.function.BiFunction;

@Mixin(PickupHandler.class)
public class PickupHandlerMixin
{
	@Inject(method = "tryPickUpBlock", at = @At("HEAD"), cancellable = true)
	private static void onTryPickupBlock(ServerPlayerEntity player, BlockPos pos, World world, BiFunction<BlockState, BlockPos, Boolean> pickupCallback, CallbackInfoReturnable<Boolean> cir)
	{
		if (player != null && UltraComponents.DIMENSION_DATA.get(world).isPosNotModifiable(player, pos))
			cir.setReturnValue(false);
		else if(world.getBlockEntity(pos) instanceof AbstractMappingBlockEntity)
			cir.setReturnValue(false);
	}
}
