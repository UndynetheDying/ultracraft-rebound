package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class GlobalRedstoneListenerBlock extends AbstractMappingBlock
{
	public GlobalRedstoneListenerBlock(Settings settings)
	{
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new GlobalRedstoneListenerBlockEntity(pos, state);
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
		return true;
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		return getStrongRedstonePower(state, world, pos, direction);
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		if(world.getBlockEntity(pos) instanceof GlobalRedstoneListenerBlockEntity redstone && redstone.isActive())
			return 15;
		return super.getStrongRedstonePower(state, world, pos, direction);
	}
}
