package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RedstoneReceiverBlock extends AbstractMappingBlock
{
	public RedstoneReceiverBlock(Settings settings)
	{
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new RedstoneReceiverBlockEntity(pos, state);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
	{
		if (world.isClient)
			return;
		int strength = world.getReceivedRedstonePower(pos);
		if(world.getBlockEntity(pos) instanceof RedstoneReceiverBlockEntity receiver)
			receiver.active = strength > 0;
		world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
		world.updateNeighbors(pos, this);
	}
}
