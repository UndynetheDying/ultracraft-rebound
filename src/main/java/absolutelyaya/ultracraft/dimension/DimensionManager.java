package absolutelyaya.ultracraft.dimension;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface DimensionManager
{
	void tick();
	
	ActionResult onBlockInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit);
	
	ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction);
	
	ServerWorld getWorld();
	
	void onWorldLoad();
}
