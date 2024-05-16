package absolutelyaya.ultracraft.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpecialItem extends Item
{
	List<String> lore = new ArrayList<>();
	List<String> hiddenLore = new ArrayList<>();
	boolean trinket;
	
	public SpecialItem(Settings settings)
	{
		super(settings);
	}
	
	public SpecialItem putLore(boolean hidden, String[] lines)
	{
		if(hidden)
			hiddenLore.addAll(List.of(lines));
		else
			lore.addAll(List.of(lines));
		return this;
	}
	
	public SpecialItem putLore(String[] lines, String[] hiddenLines)
	{
		putLore(false, lines);
		putLore(true, hiddenLines);
		return this;
	}
	
	public SpecialItem markTrinket()
	{
		trinket = true;
		return this;
	}
	
	public boolean isTrinket()
	{
		return trinket;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		if(!lore.isEmpty())
			lore.forEach(s -> tooltip.add(Text.translatable(s)));
		if(!hiddenLore.isEmpty() && context.isAdvanced())
			hiddenLore.forEach(s -> tooltip.add(Text.translatable(s)));
	}
}
