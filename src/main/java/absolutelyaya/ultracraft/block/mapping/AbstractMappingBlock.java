package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IEditorComponent;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMappingBlock extends BlockWithEntity
{
	public AbstractMappingBlock(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
	{
		super.onPlaced(world, pos, state, placer, itemStack);
		if(placer == null)
			return;
		IEditorComponent editor = UltraComponents.EDITOR.get(placer);
		if(this instanceof RoomBlock && !editor.isAllowRecursiveRooms())
			return;
		if(!(world.getBlockEntity(pos) instanceof AbstractMappingBlockEntity block))
			return;
		BlockPos roomPos = editor.getEditFocus("room");
		if(roomPos != null && world.getBlockEntity(roomPos) instanceof RoomBlockEntity room)
		{
			room.registerChild(pos, block);
			block.setParent(roomPos);
		}
		else if(!(this instanceof RoomBlock))
		{
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			placer.sendMessage(Text.of("No Room Focused"));
		}
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		if(context instanceof EntityShapeContext e && e.getEntity() instanceof PlayerEntity player && player.getWorld().isClient)
			if(UltraComponents.EDITOR.get(player).isActive())
				return VoxelShapes.fullCube();
		return VoxelShapes.empty();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.INVISIBLE;
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(!(world.getBlockEntity(pos) instanceof AbstractMappingBlockEntity mappingBlock) || !hand.equals(Hand.MAIN_HAND))
			return ActionResult.PASS;
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		if(!editor.isActive())
			return ActionResult.PASS;
		if(editor.getEditAreaStep() > 0 && !world.isClient)
		{
			if(editor.getEditFocus().isEmpty())
				editor.setEditAreaStep(0);
			return ActionResult.SUCCESS;
		}
		AbstractMappingBlockEntity entity = null;
		if(world.getBlockEntity(pos) instanceof AbstractMappingBlockEntity b)
			entity = b;
		String key = mappingBlock.getFocusKey();
		BlockPos previous = editor.getEditFocus(key);
		if(!key.equals("room") && entity != null)
		{
			if((entity.getParent() == null || !(world.getBlockEntity(entity.getParent()) instanceof AbstractMappingBlockEntity)))
			{
				editor.setRebindingParent(pos);
				if(!world.isClient)
					player.sendMessage(Text.of("Invalid Parent; Right Click a Room Block to reparent."));
				return ActionResult.SUCCESS;
			}
			BlockPos topRoom = editor.getEditFocus("room");
			if(topRoom != null )
			{
				if(world.getBlockEntity(topRoom) instanceof RoomBlockEntity room)
					topRoom = room.getTopLevelParent();
				if(topRoom != null && !topRoom.equals(entity.getTopLevelParent()))
				{
					if(!world.isClient)
						player.sendMessage(Text.of("This belongs to a different Room"));
					return ActionResult.SUCCESS;
				}
			}
		}
		BlockPos rebind = editor.getRebindingParent();
		if(rebind != null)
		{
			if(entity instanceof RoomBlockEntity room)
			{
				if(world.getBlockEntity(rebind) instanceof AbstractMappingBlockEntity rebindTarget)
				{
					rebindTarget.setParent(pos);
					room.registerChild(rebind, rebindTarget);
					if(!world.isClient)
						player.sendMessage(Text.of("Rebound Child Block to '" + entity.getID() + "'"));
				}
				else if(!world.isClient)
					player.sendMessage(Text.of("The Rebind Target is no longer valid; Rebinding cancelled"));
			}
			else if(!world.isClient)
				player.sendMessage(Text.of("This Block cannot have child Blocks; Rebinding cancelled"));
			editor.setRebindingParent(null);
			return ActionResult.SUCCESS;
		}
		if(previous != null && previous.equals(pos))
		{
			editor.clearEditFocus(key);
			return ActionResult.SUCCESS;
		}
		if(editor.isActive())
		{
			if(key.equals("room"))
				editor.clearEditFocus();
			editor.setEditFocus(key, pos.equals(editor.getEditFocus(key)) ? null : pos);
			return ActionResult.SUCCESS;
		}
		else
			return ActionResult.PASS;
	}
}
