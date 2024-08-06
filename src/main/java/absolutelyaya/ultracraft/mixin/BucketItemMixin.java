package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item
{
	@Shadow @Final private Fluid fluid;
	
	public BucketItemMixin(Settings settings)
	{
		super(settings);
	}
	
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir)
	{
		BlockHitResult bHit = raycast(world, user, fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
		if(bHit.getType().equals(HitResult.Type.MISS))
			return;
		BlockPos pos = bHit.getBlockPos();
		if(fluid != Fluids.EMPTY)
			pos.offset(bHit.getSide());
		if(UltraComponents.DIMENSION_DATA.get(world).isPosNotModifiable(user, bHit.getBlockPos()))
			cir.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
	}
}
