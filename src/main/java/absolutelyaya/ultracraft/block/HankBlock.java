package absolutelyaya.ultracraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HankBlock extends AbstractPedestalBlock
{
	public HankBlock(AbstractBlock.Settings sounds)
	{
		super(sounds);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new HankBlockEntity(pos, state);
	}
	
	@Override
	protected void cleanPedestal(World world, BlockPos pos, BlockState state)
	{
		world.setBlockState(pos, state.with(FANCY, false));
		super.cleanPedestal(world, pos, state);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.cuboid(0.2f, 0f, 0.2f, 0.8f, 1f, 0.8f);
	}
}
