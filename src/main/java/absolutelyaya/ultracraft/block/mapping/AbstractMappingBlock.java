package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.UltraComponents;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract class AbstractMappingBlock extends BlockWithEntity
{
	public AbstractMappingBlock(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		if(context instanceof EntityShapeContext e && e.getEntity() instanceof PlayerEntity player && player.getWorld().isClient)
			if(UltraComponents.EDITOR.get(player).isActive())
				return VoxelShapes.fullCube();
		return VoxelShapes.empty();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.INVISIBLE;
	}
}
