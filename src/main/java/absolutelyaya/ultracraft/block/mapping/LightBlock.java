package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LightBlock extends AbstractMappingBlock
{
	public static final IntProperty LEVEL_15 = Properties.LEVEL_15;
	public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
	
	public LightBlock(Settings settings)
	{
		super(settings);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder.add(LEVEL_15).add(ACTIVE));
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new LightBlockEntity(pos, state);
	}
	
	public static int getLightLevel(BlockState state)
	{
		return state.get(ACTIVE) ? state.get(LEVEL_15) : 0;
	}
}
