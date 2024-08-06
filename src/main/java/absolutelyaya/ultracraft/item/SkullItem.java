package absolutelyaya.ultracraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkullItem extends Item implements Equipment
{
	public SkullItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public EquipmentSlot getSlotType()
	{
		return EquipmentSlot.HEAD;
	}
	
	public ItemStack getFragileStack()
	{
		ItemStack stack = new ItemStack(this);
		stack.getOrCreateNbt().putBoolean("fragile", true);
		return stack;
	}
	
	public static boolean isFragile(ItemStack stack)
	{
		if(!stack.hasNbt())
			return false;
		NbtCompound nbt = stack.getNbt();
		if(nbt.contains("fragile", NbtElement.BYTE_TYPE))
			return nbt.getBoolean("fragile");
		return false;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		if(isFragile(stack))
			tooltip.add(Text.translatable("item.ultracraft.skull.fragile-lore"));
	}
}
