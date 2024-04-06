package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
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
		if(UltraComponents.DIMENSION_DATA.get(context.getWorld()).isPosNotModifiable(context.getPlayer(), context.getBlockPos()))
			cir.setReturnValue(ActionResult.FAIL);
	}
}
