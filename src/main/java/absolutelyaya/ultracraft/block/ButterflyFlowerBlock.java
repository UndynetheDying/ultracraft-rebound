package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.registry.ParticleRegistry;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ButterflyFlowerBlock extends Block
{
	public static BooleanProperty INVISIBLE = BooleanProperty.of("invisible");
	
	public ButterflyFlowerBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(INVISIBLE, false));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(INVISIBLE);
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
	{
		super.randomDisplayTick(state, world, pos, random);
		if(random.nextFloat() < 0.05f)
		{
			Vec3d pos2 = pos.up().toCenterPos().addRandom(random, 3);
			world.addParticle(ParticleRegistry.BUTTERFLY, pos2.x, pos2.y, pos2.z, 0f, 0f, 0f);
		}
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		if(state.get(INVISIBLE) && context instanceof EntityShapeContext e && e.getEntity() instanceof PlayerEntity player && !player.isCreative())
			return VoxelShapes.empty();
		Vec3d vec3d = state.getModelOffset(world, pos);
		return VoxelShapes.cuboid(5f / 16f, 0f, 5f / 16f, 11f / 16f, 13f / 16f, 11f / 16f).offset(vec3d.x, vec3d.y, vec3d.z);
	}
	
	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
	{
		return type == NavigationType.AIR && !collidable ;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options)
	{
		super.appendTooltip(stack, world, tooltip, options);
		tooltip.add(Text.translatable("block.ultracraft.butterfly_emitter.lore"));
	}
}
