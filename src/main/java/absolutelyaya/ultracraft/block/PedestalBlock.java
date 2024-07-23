package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.registry.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PedestalBlock extends AbstractPedestalBlock
{
	public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);
	
	public PedestalBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(TYPE, Type.NONE).with(FACING, Direction.NORTH).with(FANCY, false)
								.with(LOCKED, false));
	}
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return super.getPlacementState(ctx).with(TYPE, Type.NONE);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(TYPE);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new PedestalBlockEntity(pos, state);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity instanceof AbstractPedestalEntity pedestal)
		{
			ItemStack key = pedestal.getKey();
			ItemStack held = pedestal.getHeld();
			if(held.isEmpty())
				return 0;
			if (key.isEmpty())
			{
				switch(state.get(TYPE))
				{
					case BLUE ->
					{
						if(held.isOf(ItemRegistry.BLUE_SKULL))
							return 15;
					}
					case RED ->
					{
						if(held.isOf(ItemRegistry.RED_SKULL))
							return 15;
					}
				}
			}
		}
		return super.getWeakRedstonePower(state, world, pos, direction);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(world.getBlockEntity(pos) instanceof AbstractPedestalEntity pedestal && pedestal.decorative)
			return ActionResult.FAIL;
		ItemStack stack = player.getStackInHand(hand);
		if(stack.isOf(Items.BLUE_DYE) && !state.get(TYPE).equals(Type.BLUE))
		{
			useDye(world, pos, state, player, stack, Type.BLUE);
			return ActionResult.CONSUME;
		}
		else if(stack.isOf(Items.RED_DYE) && !state.get(TYPE).equals(Type.RED))
		{
			useDye(world, pos, state, player, stack, Type.RED);
			return ActionResult.CONSUME;
		}
		else return super.onUse(state, world, pos, player, hand, hit);
	}
	
	@Override
	protected void cleanPedestal(World world, BlockPos pos, BlockState state)
	{
		world.setBlockState(pos, state.with(TYPE, Type.NONE).with(FANCY, false));
		super.cleanPedestal(world, pos, state);
	}
	
	@Override
	protected boolean canBeCleaned(BlockState state)
	{
		return super.canBeCleaned(state) || !state.get(TYPE).equals(Type.NONE);
	}
	
	void useDye(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack dye, Type newType)
	{
		world.setBlockState(pos, state.with(TYPE, newType));
		if(!player.isCreative())
			dye.decrement(1);
		world.playSound(null, pos, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER, SoundCategory.PLAYERS, 1f, 1f);
		world.addBlockBreakParticles(pos, state.with(TYPE, newType));
		updateNeighbors(world, pos);
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options)
	{
		super.appendTooltip(stack, world, tooltip, options);
		tooltip.add(Text.translatable("block.ultracraft.pedestal.lore2"));
	}
}
