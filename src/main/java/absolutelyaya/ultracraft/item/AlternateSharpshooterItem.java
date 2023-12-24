package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.client.rendering.item.AlternateSharpshooterRevolverRenderer;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class AlternateSharpshooterItem extends SharpshooterRevolverItem
{
	public AlternateSharpshooterItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider() {
			private AlternateSharpshooterRevolverRenderer renderer;
			
			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.renderer == null)
					this.renderer = new AlternateSharpshooterRevolverRenderer();
				
				return renderer;
			}
		});
	}
	
	@Override
	protected boolean isAlternate()
	{
		return true;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		super.inventoryTick(stack, world, entity, slot, selected);
		if(!(entity instanceof PlayerEntity user))
			return;
		if(isCanFirePrimary(user) && getNbt(stack, getHammerId()) == 0)
			setNbt(stack, getHammerId(), 1);
	}
	
	@Override
	int getSwitchCooldown(ItemStack stack)
	{
		if(getNbt(stack, getHammerId()) == 1)
			return super.getSwitchCooldown(stack);
		else
			return 14;
	}
	
	@Override
	protected void onSwitch(PlayerEntity user, World world)
	{
		super.onSwitch(user, world);
		if(!world.isClient)
		{
			ItemStack stack = user.getMainHandStack();
			if(getNbt(stack, getHammerId()) != 1)
				triggerAnim(user, GeoItem.getOrAssignId(stack, (ServerWorld)world), getControllerName(), "hammerpull" + (b ? "2" : ""));
			b = !b;
		}
	}
	
	@Override
	protected String getHammerId()
	{
		return "hammer3";
	}
}
