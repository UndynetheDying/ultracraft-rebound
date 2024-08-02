package absolutelyaya.ultracraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IStateChangingFallingBlock
{
	BlockState getLandingState(World world, float fallDistance, BlockState state, BlockPos pos);
}
