package absolutelyaya.ultracraft.util;

import com.google.common.primitives.ImmutableIntArray;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryUtil
{
	public static boolean containsItem(DefaultedList<ItemStack> inv, Item item, int count)
	{
		int found = 0;
		for (ItemStack stack : inv)
		{
			if(stack.isOf(item))
			{
				found += stack.getCount();
				if(found >= count)
					return true;
			}
		}
		return false;
	}
	
	public static boolean containsItem(DefaultedList<ItemStack> inv, Item item)
	{
		return containsItem(inv, item, 1);
	}
	
	public static int getFirstSlotWithItem(DefaultedList<ItemStack> inv, Item item)
	{
		for (int i = 0; i < inv.size(); i++)
		{
			if(inv.get(i).isOf(item))
				return i;
		}
		return -1;
	}
	
	public static int[] getAllSlowsWithItem(DefaultedList<ItemStack> inv, Item item)
	{
		ImmutableIntArray.Builder builder = ImmutableIntArray.builder();
		for (int i = 0; i < inv.size(); i++)
		{
			if(inv.get(i).isOf(item))
				builder.add(i);
		}
		return builder.build().stream().toArray();
	}
}
