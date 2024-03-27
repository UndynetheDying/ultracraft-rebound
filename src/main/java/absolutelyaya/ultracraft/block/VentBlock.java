package absolutelyaya.ultracraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VentBlock extends FacingBlock
{
	public VentBlock(Settings settings)
	{
		super(settings);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder.add(FACING));
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return super.getPlacementState(ctx).with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
	{
			Vec3i dir = state.get(FACING).getVector();
			world.addParticle(ParticleTypes.CLOUD, pos.getX() + random.nextFloat(), pos.getY() + random.nextFloat(), pos.getZ() + random.nextFloat(),
					dir.getX() * random.nextFloat() * 0.25f, dir.getY() * random.nextFloat() * 0.25f, dir.getZ() * random.nextFloat() * 0.25f);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return switch(state.get(FACING))
		{
			case NORTH -> VoxelShapes.cuboid(0f, 0f, 14f / 16f, 1f, 1f, 1f);
			case EAST -> VoxelShapes.cuboid(0f, 0f, 0f, 2f / 16f, 1f, 1f);
			case SOUTH -> VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 2f / 16f);
			case WEST -> VoxelShapes.cuboid(14f / 16f, 0f, 0f, 1f, 1f, 1f);
			case UP -> VoxelShapes.cuboid(0f, 0f, 0f, 1f, 2f / 16f, 1f);
			case DOWN -> VoxelShapes.cuboid(0f, 14f / 16f, 0f, 1f, 1f, 1f);
		};
	}
	
	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation)
	{
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}
}
