package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlobalRedstoneReceiverBlock extends AbstractMappingBlock
{
	public GlobalRedstoneReceiverBlock(Settings settings)
	{
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new GlobalRedstoneReceiverBlockEntity(pos, state);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
	{
		if (world.isClient)
			return;
		int strength = world.getReceivedRedstonePower(pos);
		if(world.getBlockEntity(pos) instanceof GlobalRedstoneReceiverBlockEntity receiver)
			receiver.active = strength > 0;
		world.setBlockState(pos, state, 0);
		world.updateNeighbors(pos, this);
		world.getBlockEntity(pos).markDirty();
		world.updateListeners(pos, state, state, 0);
	}
}
