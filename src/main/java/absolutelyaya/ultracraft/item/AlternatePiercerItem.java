package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.client.rendering.item.AlternatePierceRevolverRenderer;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class AlternatePiercerItem extends PierceRevolverItem
{
	public AlternatePiercerItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider() {
			private AlternatePierceRevolverRenderer renderer;
			
			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.renderer == null)
					this.renderer = new AlternatePierceRevolverRenderer();
				
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
		int hammer = getNbt(stack, getHammerId());
		if(isCanFirePrimary(user) && hammer != 1)
		{
			if(!world.isClient && hammer == 0)
			{
				setNbt(stack, getHammerId(), 2);
				hammerPull(user, stack, (ServerWorld)world);
			}
			if(hammer == 2)
				setNbt(stack, getHammerId(), 1);
		}
	}
	
	@Override
	int getSwitchCooldown(ItemStack stack)
	{
		if(getNbt(stack, getHammerId()) == 1)
			return super.getSwitchCooldown(stack);
		else
			return 0;
	}
	
	@Override
	protected void onSwitch(PlayerEntity user, World world)
	{
		super.onSwitch(user, world);
		ItemStack stack = user.getMainHandStack();
		if(getNbt(stack, getHammerId()) == 2)
			setNbt(stack, getHammerId(), 0);
	}
	
	@Override
	protected String getHammerId()
	{
		return "hammer1";
	}
}
