package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.client.rendering.item.AbstractPlushieRenderer;
import absolutelyaya.ultracraft.client.rendering.item.BlahajRenderer;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animatable.instance.SingletonAnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlahajItem extends SpecialItem implements GeoItem
{
	AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
	private static final RawAnimation SQUEEZE = RawAnimation.begin().thenPlay("squeeze");
	
	public BlahajItem(Settings settings)
	{
		super(settings);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider()
		{
			private BlahajRenderer renderer;
			@Override
			public BuiltinModelItemRenderer getCustomRenderer()
			{
				if(renderer == null)
					renderer = new BlahajRenderer();
				return renderer;
			}
		});
	}
	
	@Override
	public Supplier<Object> getRenderProvider()
	{
		return renderProvider;
	}
	
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "main", state -> PlayState.CONTINUE)
										.triggerableAnim("squeeze", SQUEEZE));
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		user.playSound(SoundRegistry.BLAHAJ_USE, 1, 0.9f + user.getRandom().nextFloat() * 0.2f);
		if(!world.isClient)
			triggerAnim(user, GeoItem.getOrAssignId(user.getStackInHand(hand), (ServerWorld)world), "main", "squeeze");
		user.getItemCooldownManager().set(this, 12);
		return super.use(world, user, hand);
	}
	
	public boolean isRare()
	{
		ItemStack stack = ((BlahajRenderer)((RenderProvider)getRenderProvider().get()).getCustomRenderer()).getCurrentItemStack();
		return isRare(stack);
	}
	
	public boolean isRare(ItemStack stack)
	{
		if(!stack.hasNbt())
			return false;
		NbtCompound nbt = stack.getNbt();
		return nbt != null && nbt.contains("rare") && nbt.getBoolean("rare");
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		if(isRare(stack) && context.isAdvanced())
			tooltip.add(Text.translatable("item.ultracraft.blahaj.hiddenlore"));
	}
}
