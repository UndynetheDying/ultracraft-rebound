package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin extends Item
{
	public FlintAndSteelItemMixin(Settings settings)
	{
		super(settings);
	}
	
	@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
	void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
	{
		if(UltraComponents.DIMENSION_DATA.get(context.getWorld()).isPosNotModifiable(context.getPlayer(), context.getBlockPos()))
			cir.setReturnValue(ActionResult.FAIL);
	}
}
