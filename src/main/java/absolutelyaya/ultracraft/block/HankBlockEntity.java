package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import absolutelyaya.ultracraft.registry.TagRegistry;
import mod.azure.azurelib.animatable.GeoBlockEntity;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animatable.instance.InstancedAnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class HankBlockEntity extends AbstractPedestalEntity implements GeoBlockEntity
{
	final AnimatableInstanceCache cache = new InstancedAnimatableInstanceCache(this);
	static final RawAnimation PRAY = RawAnimation.begin().thenLoop("pray");
	
	public HankBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.HANK, pos, state);
	}
	
	@Override
	public boolean onPunch(PlayerEntity player, boolean mainHand)
	{
		ItemStack item = mainHand ? player.getMainHandStack() : player.getOffHandStack();
		if(!getKey().isEmpty())
		{
			if(!(getKey().getItem().equals(item.getItem()) || item.isEmpty()))
				return false;
			return super.onPunch(player, mainHand);
		}
		if(!(item.isIn(TagRegistry.HANKABLE) || item.isEmpty()))
			return false;
		return super.onPunch(player, mainHand);
	}
	
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, i -> {
			i.setAnimation(PRAY);
			return PlayState.CONTINUE;
		}));
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
	
	@Override
	public Text getDisplayName()
	{
		return Text.translatable("screen.ultracraft.hank.title");
	}
}
