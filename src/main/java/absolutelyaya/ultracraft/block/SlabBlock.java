package absolutelyaya.ultracraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class SlabBlock extends RedstoneLampBlock
{
	public static final IntProperty NUMBER = IntProperty.of("number", 1, 10);
	
	public SlabBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(this.getDefaultState().with(LIT, false).with(NUMBER, 1));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(LIT).add(NUMBER);
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		ItemStack stack = super.getPickStack(world, pos, state);
		NbtCompound nbt = stack.getOrCreateNbt();
		NbtCompound stateNbt = stack.getNbt().getCompound("BlockStateTag");
		if(stateNbt == null)
			stateNbt = new NbtCompound();
		stateNbt.putString("number", String.valueOf(state.get(NUMBER)));
		nbt.put("BlockStateTag", stateNbt);
		stack.setNbt(nbt);
		return stack;
	}
}
