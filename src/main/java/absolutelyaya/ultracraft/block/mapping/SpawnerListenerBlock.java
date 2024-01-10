package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SpawnerListenerBlock extends AbstractListenerBlock
{
	public SpawnerListenerBlock(Settings settings)
	{
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new SpawnListenerBlockEntity(pos, state);
	}
}
