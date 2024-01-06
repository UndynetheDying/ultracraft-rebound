package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.FishPacket;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractFishItem extends Item
{
	protected int wasSelected;
	
	public AbstractFishItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		super.inventoryTick(stack, world, entity, slot, selected);
		if(slot < 0 || slot > 9)
			return;
		if(world.isClient && wasSelected <= 0 && selected)
			onSelect();
		if(world.isClient && wasSelected == 2 && !selected)
			onUnselect();
		if(selected)
			wasSelected = 12;
		else if(wasSelected > 0)
			wasSelected--;
	}
	
	protected void onSelect()
	{
	
	}
	
	protected void onUnselect()
	{
	
	}
	
	protected void sendFishPacket(FishPacket data)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(data.ordinal());
		ClientPlayNetworking.send(PacketRegistry.FISH_PACKET_ID, buf);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		if(context.isAdvanced())
			tooltip.add(Text.translatable(stack.getTranslationKey() + "-hiddenlore"));
	}
}
