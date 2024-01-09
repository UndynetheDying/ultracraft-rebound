package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class RedstoneListenerBlock extends AbstractListenerBlock
{
	public RedstoneListenerBlock(Settings settings)
	{
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new RedstoneListenerBlockEntity(pos, state);
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
		return true;
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		if(world.getBlockEntity(pos) instanceof RedstoneListenerBlockEntity redstone && redstone.isActive())
			return 15;
		return super.getWeakRedstonePower(state, world, pos, direction);
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		if(world.getBlockEntity(pos) instanceof RedstoneListenerBlockEntity redstone && redstone.isActive())
			return 15;
		return super.getStrongRedstonePower(state, world, pos, direction);
	}
}
