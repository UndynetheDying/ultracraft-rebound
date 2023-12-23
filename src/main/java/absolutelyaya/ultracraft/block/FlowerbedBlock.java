package absolutelyaya.ultracraft.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class FlowerbedBlock extends Block
{
	public static final IntProperty LAYERS = IntProperty.of("layers", 1, 4);
	public static final BooleanProperty NORTH = Properties.NORTH;
	public static final BooleanProperty EAST = Properties.EAST;
	public static final BooleanProperty SOUTH = Properties.SOUTH;
	public static final BooleanProperty WEST = Properties.WEST;
	
	public FlowerbedBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(LAYERS, 1)
								.with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
	}
	
	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
	{
		return type.equals(NavigationType.LAND);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return Block.createCuboidShape(0, 0, 0, 16, 1 + state.get(LAYERS), 16);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		if(context instanceof EntityShapeContext entityContext && entityContext.getEntity() != null && entityContext.getEntity().fallDistance > 0.51f)
			return VoxelShapes.empty();
		else
			return Block.createCuboidShape(0, 0, 0, 16, state.get(LAYERS), 16);
	}
	
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if(world.isClient && entity.getVelocity().length() > 0.15f)
		{
			Random rand = world.getRandom();
			Vec3d p = entity.getPos().addRandom(rand, entity.getWidth());
			world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), p.x, pos.getY() + rand.nextFloat() * (0.1 * state.get(LAYERS) + 0.1), p.z,
					0f, 0f, 0f);
		}
	}
	
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
	{
		return super.getStateForNeighborUpdate(getSideState(world, pos, state), direction, neighborState, world, pos, neighborPos);
	}
	
	BlockState getSideState(WorldAccess world, BlockPos pos, BlockState state)
	{
		if(world.getBlockState(pos.down()).isSideSolid(world, pos, Direction.UP, SideShapeType.FULL))
			return state.with(NORTH, world.isAir(pos.north().down())).with(EAST, world.isAir(pos.east().down()))
					.with(SOUTH, world.isAir(pos.south().down())).with(WEST, world.isAir(pos.west().down()));
		else
			return state;
	}
	
	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
		return 1f;
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos)
	{
		return state.get(LAYERS);
	}
	
	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		int layers = state.get(LAYERS);
		if (context.getStack().isOf(asItem()) && layers < 4)
		{
			if (context.canReplaceExisting())
				return context.getSide().equals(Direction.UP);
			return true;
		}
		return true;
	}
	
	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (state.isOf(this))
			return state.with(LAYERS, Math.min(4, state.get(LAYERS) + 1));
		else
			state = getDefaultState();
		state = getSideState(ctx.getWorld(), ctx.getBlockPos(), state);
		return state;
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(LAYERS, NORTH, EAST, SOUTH, WEST);
	}
}
