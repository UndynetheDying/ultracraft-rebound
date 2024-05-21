package absolutelyaya.ultracraft.block;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.HashMap;

public class CarpetBlock extends Block
{
	public static final BooleanProperty NORTH = Properties.NORTH;
	public static final BooleanProperty EAST = Properties.EAST;
	public static final BooleanProperty SOUTH = Properties.SOUTH;
	public static final BooleanProperty WEST = Properties.WEST;
	public static final BooleanProperty NE = BooleanProperty.of("ne");
	public static final BooleanProperty ES = BooleanProperty.of("es");
	public static final BooleanProperty SW = BooleanProperty.of("sw");
	public static final BooleanProperty WN = BooleanProperty.of("wn");
	public static final HashMap<Direction, BooleanProperty> FACING_PROPERTIES;
	
	public CarpetBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(NORTH, EAST, WEST, SOUTH, NE, ES, SW, WN);
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		if(!canPlaceAt(state, world, pos))
			world.breakBlock(pos, true);
		if (direction.getAxis().getType() == Direction.Type.HORIZONTAL)
			return applyDiagonals(state, world, pos).with(FACING_PROPERTIES.get(direction), neighborState.isOf(this));
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}
	
	BlockState applyDiagonals(BlockState state, WorldAccess world, BlockPos pos)
	{
		return state.with(NE, world.getBlockState(pos.north().east()).isOf(this)).with(ES, world.getBlockState(pos.east().south()).isOf(this))
				.with(SW, world.getBlockState(pos.south().west()).isOf(this)).with(WN, world.getBlockState(pos.west().north()).isOf(this));
	}
	
	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return Block.isFaceFullSquare(world.getBlockState(pos.down()).getCollisionShape(world, pos.down()), Direction.UP);
	}
	
	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
	{
		return type.equals(NavigationType.LAND);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		BlockState state = super.getPlacementState(ctx);
		return applyDiagonals(state, world, pos).with(NORTH, world.getBlockState(pos.north()).isOf(this))
					   .with(EAST, world.getBlockState(pos.east()).isOf(this))
					   .with(SOUTH, world.getBlockState(pos.south()).isOf(this))
					   .with(WEST, world.getBlockState(pos.west()).isOf(this));
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f / 16f, 1f);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return getCollisionShape(state, world, pos, context);
	}
	
	static {
		FACING_PROPERTIES = new HashMap<>() {
			{
				put(Direction.NORTH, NORTH);
				put(Direction.EAST, EAST);
				put(Direction.SOUTH, SOUTH);
				put(Direction.WEST, WEST);
			}
		};
	}
}
