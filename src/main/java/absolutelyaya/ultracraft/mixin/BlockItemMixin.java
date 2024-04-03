package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.dimension.UltraDimensions;
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
		if(UltraDimensions.Instance.onBlockPlace(context))
			cir.setReturnValue(ActionResult.FAIL);
	}
}
