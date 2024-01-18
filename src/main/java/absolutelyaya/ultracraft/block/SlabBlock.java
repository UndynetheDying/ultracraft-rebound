package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SlabBlock extends Block implements IPunchableBlock
{
	public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
	public static final IntProperty POWERED = IntProperty.of("powered", 0, 15);
	public static final IntProperty NUMBER = IntProperty.of("number", 1, 10);
	
	public SlabBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(ACTIVE, false).with(POWERED, 0).with(NUMBER, 1));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(ACTIVE).add(POWERED).add(NUMBER);
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
	
	@Override
	public boolean onPunch(PlayerEntity puncher, BlockPos pos, boolean mainHand)
	{
		World world = puncher.getWorld();
		onUse(world.getBlockState(pos), world, pos, puncher, mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND, null);
		return true;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (hit == null || !(player.getStackInHand(hand).isOf(Items.DEBUG_STICK) || player.isSneaking()))
		{
			world.setBlockState(pos, state.cycle(ACTIVE));
			world.playSound(null, pos, state.get(ACTIVE) ? SoundRegistry.SLAB_DEACTIVATE : SoundRegistry.SLAB_ACTIVATE, SoundCategory.BLOCKS);
			world.updateNeighbor(pos, IceBlock.getMeltedState().getBlock(), pos);
			return ActionResult.SUCCESS;
		}
		return ActionResult.FAIL;
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
	{
		if (world.isClient)
			return;
		int strength = world.getReceivedRedstonePower(pos);
		if ((state.get(POWERED) != strength))
		{
			if (strength > state.get(POWERED))
			{
				if(strength == 15)
					state = state.cycle(ACTIVE);
				else if(!state.get(ACTIVE) && strength > 0)
					state = state.with(NUMBER, Math.min(strength, 10));
			}
			state = state.with(POWERED, strength);
		}
		world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
		world.updateNeighbors(pos, this);
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
		return false;
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		return state.get(ACTIVE) ? state.get(NUMBER) : 0;
	}
}
