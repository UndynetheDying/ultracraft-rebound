package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.registry.BlockRegistry;
import absolutelyaya.ultracraft.registry.TagRegistry;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin
{
	@Shadow @Final public static IntProperty LEVEL;
	
	@Shadow @Final protected FlowableFluid fluid;
	
	@Shadow @Final public static ImmutableList<Direction> FLOW_DIRECTIONS;
	
	@Shadow protected abstract void playExtinguishSound(WorldAccess world, BlockPos pos);
	
	@ModifyReturnValue(method = "getCollisionShape", at = @At("RETURN"))
	VoxelShape onCollisionShape(VoxelShape original, @Local BlockState state, @Local BlockView world, @Local BlockPos pos, @Local ShapeContext context)
	{
		if(state.getFluidState().isIn(TagRegistry.UNSKIMMABLE_FLUIDS))
			return original;
		VoxelShape collisionShape = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.5, 16.0);
		return context.isAbove(collisionShape, pos, true) && state.get(LEVEL) == 0 &&
					   context.canWalkOnFluid(world.getFluidState(pos.up()), state.getFluidState()) ? collisionShape : VoxelShapes.empty();
	}
	
	@ModifyReturnValue(method = "receiveNeighborFluids", at = @At("RETURN"))
	boolean triggerFluidReactions(boolean original, @Local World world, @Local BlockPos pos)
	{
		MutableBoolean reaction = new MutableBoolean();
		FluidState self = world.getFluidState(pos);
		if(self.isIn(FluidTags.LAVA))
		{
			FLOW_DIRECTIONS.forEach(dir -> {
				BlockPos pos1 = pos.offset(dir);
				if(world.getFluidState(pos1).isIn(TagRegistry.BLOOD_FLUID))
					bloodReaction(world, pos1, BlockRegistry.FLESH, reaction);
			});
		}
		else if(self.isIn(FluidTags.WATER) && !self.isIn(TagRegistry.BLOOD_FLUID))
		{
			FLOW_DIRECTIONS.forEach(dir -> {
				BlockPos pos1 = pos.offset(dir);
				if(world.getFluidState(pos1).isIn(TagRegistry.BLOOD_FLUID))
					bloodReaction(world, pos1, Blocks.NETHERRACK, reaction);
			});
		}
		else if (self.isIn(TagRegistry.BLOOD_FLUID))
		{
			BlockPos pos1 = pos.down();
			FluidState fluid = world.getFluidState(pos1);
			if(fluid.isIn(FluidTags.LAVA))
				bloodReaction(world, pos1, BlockRegistry.FLESH, reaction);
			else if(fluid.isIn(FluidTags.WATER) && !fluid.isIn(TagRegistry.BLOOD_FLUID))
				bloodReaction(world, pos1, Blocks.NETHERRACK, reaction);
		}
		return original && !reaction.booleanValue();
	}
	
	void bloodReaction(World world, BlockPos pos1, Block block, MutableBoolean val)
	{
		world.setBlockState(pos1, block.getDefaultState());
		playExtinguishSound(world, pos1);
		val.setTrue();
	}
}
