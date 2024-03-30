package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.client.rendering.item.AlternateSharpshooterRevolverRenderer;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import mod.azure.azurelib.animatable.client.RenderProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Vector2i;

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
	public void onSwitch(World world, PlayerEntity user, int newSlot)
	{
		super.onSwitch(world, user, newSlot);
		ItemStack stack = user.getMainHandStack();
		if(getNbt(stack, "charges") > 1)
			setNbt(stack, "charges", 1);
		if(getNbt(stack, getHammerId()) == 2)
			setNbt(stack, getHammerId(), 0);
	}
	
	@Override
	protected String getHammerId()
	{
		return "hammer3";
	}
	
	@Override
	public Vector2i getHUDTexture()
	{
		return new Vector2i(6, 0);
	}
	
	@Override
	public Identifier getProgressionEntry()
	{
		return ItemRegistry.SHARPSHOOTER_REVOLVER.getProgressionEntry();
	}
}
