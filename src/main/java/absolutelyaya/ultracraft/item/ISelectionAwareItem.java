package absolutelyaya.ultracraft.item;

import net.minecraft.entity.player.PlayerEntity;

public interface ISelectionAwareItem
{
	void onSelect(PlayerEntity player);
	
	void onUnselect(PlayerEntity player);
}
