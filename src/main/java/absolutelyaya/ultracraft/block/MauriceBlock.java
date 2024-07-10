package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.ServerHitscanHandler;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.registry.BlockRegistry;
import absolutelyaya.ultracraft.registry.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MauriceBlock extends FallingBlock implements IStateChangingFallingBlock
{
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty USED = BooleanProperty.of("used");
	
	public MauriceBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(USED, false));
	}
	
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
	}
	
	public BlockState rotate(BlockState state, BlockRotation rotation)
	{
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}
	
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, USED);
	}
	
	public DamageSource getDamageSource(Entity attacker)
	{
		return DamageSources.get(attacker.getWorld(), DamageSources.MAURICE, attacker);
	}
	
	protected void configureFallingBlockEntity(FallingBlockEntity entity)
	{
		entity.setHurtEntities(100f, 100);
	}
	
	@Override
	public BlockState getLandingState(World world, float fallDistance, BlockState state, BlockPos pos)
	{
		if(state.isOf(BlockRegistry.MAURICE) && fallDistance > 3f)
		{
			world.playSound(null, pos, SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.BLOCKS, 1f, 1f);
			return BlockRegistry.CRACKED_MAURICE.getDefaultState().with(FACING, state.get(FACING));
		}
		return state;
	}
	
	@Override
	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity)
	{
		super.onLanding(world, pos, fallingBlockState, currentStateInPos, fallingBlockEntity);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
	{
		if(world.isReceivingRedstonePower(pos) && !state.get(USED) && !world.isClient)
		{
			EndermiteEntity tmp = new EndermiteEntity(EntityType.ENDERMITE, world);
			Direction forwards = state.get(FACING).getOpposite();
			new ServerHitscanHandler.Hitscan(tmp, pos.add(forwards.getVector()).toCenterPos(),
					pos.add(forwards.getVector()).toCenterPos().subtract(forwards.getOffsetX() * 0.5f, 0f, forwards.getOffsetZ() * 0.5f),
					pos.add(forwards.getVector().multiply(16)).toCenterPos(), ServerHitscanHandler.MALICIOUS, 0, DamageSources.GUN)
					.explosion(new ServerHitscanHandler.HitscanExplosionData(2.75f, 5f, 0f, false)).perform();
			tmp.remove(Entity.RemovalReason.DISCARDED);
			world.scheduleBlockTick(pos, this, 100);
			world.setBlockState(pos, state.with(USED, true));
		}
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
		return true;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(state.get(USED) && player.getStackInHand(hand).isIn(TagRegistry.MAURICE_FOOD))
		{
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 1f, 0.6f);
			if(!player.isCreative())
				player.getStackInHand(hand).decrement(1);
			world.setBlockState(pos, state.with(USED, false));
			return ActionResult.SUCCESS;
		}
		return super.onUse(state, world, pos, player, hand, hit);
	}
}
