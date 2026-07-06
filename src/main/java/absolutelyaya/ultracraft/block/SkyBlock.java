package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.item.SkyBlockItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SkyBlock extends BlockWithEntity
{
	public static final IntProperty STYLE = IntProperty.of("style", 0, 2);
	public static final BooleanProperty COLLISION = BooleanProperty.of("collision");
	
	public SkyBlock(Settings settings)
	{
		super(settings);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		super.appendProperties(builder);
		builder.add(STYLE, COLLISION);
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}
	
	@Override
	public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos)
	{
		return 1f;
	}
	
	@Override
	public int getOpacity(BlockState state, BlockView world, BlockPos pos)
	{
		return 0;
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new SkyBlockEntity(pos, state);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
	{
		super.onPlaced(world, pos, state, placer, itemStack);
		if(world.getBlockEntity(pos) instanceof SkyBlockEntity sky && itemStack.hasNbt())
		{
			NbtCompound nbt = itemStack.getNbt();
			if(nbt.contains("type", NbtElement.STRING_TYPE))
				sky.type = SkyBlockEntity.SkyType.valueOf(nbt.getString("type").toUpperCase());
		}
	}
	
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		if(world.getBlockEntity(pos) instanceof SkyBlockEntity sky)
			return SkyBlockItem.getStack(sky.type);
		return super.getPickStack(world, pos, state);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		if(state.get(COLLISION))
			return super.getCollisionShape(state, world, pos, context);
		else
			return VoxelShapes.empty();
	}
}
