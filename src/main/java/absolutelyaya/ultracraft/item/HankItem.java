package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.client.rendering.item.HankItemRenderer;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.BlockItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HankItem extends BlockItem implements GeoItem
{
	private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
	private final Supplier<Object> provider = GeoItem.makeRenderer(this);
	static final RawAnimation POSE = RawAnimation.begin().thenLoop("pray");
	
	public HankItem(Block block, Settings settings)
	{
		super(block, settings);
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider()
		{
			HankItemRenderer renderer;
			@Override
			public BuiltinModelItemRenderer getCustomRenderer()
			{
				if(renderer == null)
					renderer = new HankItemRenderer();
				return renderer;
			}
		});
	}
	
	@Override
	public Supplier<Object> getRenderProvider()
	{
		return provider;
	}
	
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, i -> {
			i.setAnimation(POSE);
			return PlayState.CONTINUE;
		}));
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
}
