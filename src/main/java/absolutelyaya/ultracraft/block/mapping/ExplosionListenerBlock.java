package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ExplosionListenerBlock extends AbstractMappingBlock
{
	public ExplosionListenerBlock(Settings settings)
	{
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new ExplosionListenerBlockEntity(pos, state);
	}
}
