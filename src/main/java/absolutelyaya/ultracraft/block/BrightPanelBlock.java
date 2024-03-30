package absolutelyaya.ultracraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class BrightPanelBlock extends Block
{
	public static final IntProperty TYPE = IntProperty.of("type", 0, 2);
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	
	public BrightPanelBlock(Settings settings)
	{
		super(settings);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(TYPE, FACING);
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		BlockState state = getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing()).with(TYPE, 0);
		return getStateForPos(state, ctx.getBlockPos(), null, ctx.getWorld());
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		return getStateForPos(state, pos, direction.getAxis().equals(Direction.Axis.Y) ? null : direction, world);
	}
	
	BlockState getStateForPos(BlockState state, BlockPos pos, Direction dir, WorldAccess world)
	{
		int t = 0;
		for (int i = 0; i < 4; i++)
		{
			Direction d = Direction.fromHorizontal(i + state.get(FACING).getHorizontal() + (state.get(TYPE) == 0 ? 2 : 1));
			t = getTypeForPos(pos, d, world);
			BlockState neighbor = world.getBlockState(pos.add(d.getVector()));
			if(t != 0)
			{
				if(neighbor.isOf(this) && !neighbor.get(TYPE).equals(0))
					state = state.with(FACING, neighbor.get(FACING));
				else
					state = state.with(FACING, d.rotateYClockwise());
				break;
			}
		}
		return state.with(TYPE, t);
	}
	
	int getTypeForPos(BlockPos pos, Direction checkDir, WorldAccess world)
	{
		int val = 0;
		BlockState neighbor = world.getBlockState(pos.add(checkDir.getVector()));
		if(!neighbor.isOf(this))
			return val;
		int neighborType = neighbor.get(TYPE);
		if(neighborType == 0 || neighbor.get(FACING).equals(checkDir.rotateYClockwise()))
			val = 2;
		if(neighborType == 0 || neighbor.get(FACING).equals(checkDir.rotateYCounterclockwise()))
			val = 2;
		if(neighborType == 2 && neighbor.get(FACING).equals(checkDir.rotateYClockwise()))
			val = 1;
		if(neighborType == 1 && neighbor.get(FACING).equals(checkDir.rotateYClockwise()))
			val = 2;
		if(neighborType == 1 && neighbor.get(FACING).equals(checkDir.rotateYCounterclockwise()))
			val = 2;
		if(neighborType == 2 && neighbor.get(FACING).equals(checkDir.rotateYCounterclockwise()))
			val = 1;
		return val;
	}
}
