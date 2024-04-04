package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.dimension.UltraDimensions;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin
{
	@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
	void onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir)
	{
		if((!context.getWorld().isClient && UltraDimensions.Instance.onBlockPlace(context)) ||
				   (context.getWorld().isClient && context.getWorld().getRegistryKey().equals(LevelManager.WORLD_KEY)))
		{
			cir.setReturnValue(ActionResult.FAIL);
			if(context.getPlayer() instanceof ServerPlayerEntity serverPlayer)
			{
				if(context.getHand().equals(Hand.MAIN_HAND))
					serverPlayer.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(context.getPlayer().getInventory().selectedSlot));
				else
					serverPlayer.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(45));
			}
		}
	}
}
