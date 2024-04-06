package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.block.PortalBlock;
import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.world.IDimensionDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class DimensionManager
{
	abstract void tick();
	
	public void onSuppressedModification(PlayerEntity player)
	{
		player.sendMessage(getModifyFailText(), true);
	}
	
	abstract ServerWorld getWorld();
	
	abstract void onWorldLoad();
	
	abstract Text getModifyFailText();
}
