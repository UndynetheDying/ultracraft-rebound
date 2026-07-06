package absolutelyaya.ultracraft.compat;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;

public class TrinketUtil
{
	public static boolean isHasTrinketEquipped(LivingEntity living, Item item)
	{
		return TrinketsApi.getTrinketComponent(living).get().isEquipped(item);
	}
}
