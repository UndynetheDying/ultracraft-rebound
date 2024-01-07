package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.components.player.IEditorComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LevelBlock extends AbstractMappingBlock
{
	
	public LevelBlock(Settings settings)
	{
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new LevelBlockEntity(pos, state);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		if(editor.isActive())
		{
			editor.setEditFocus("level", pos);
			return ActionResult.SUCCESS;
		}
		else
			return ActionResult.PASS;
	}
}
