package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class DimensionManager
{
	abstract void tick();
	
	protected ActionResult onBlockInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit)
	{
		if(!player.isSneaking() && world.getBlockState(hit.getBlockPos()).onUse(world, player, hand, hit).isAccepted())
			return ActionResult.SUCCESS;
		if(player.getStackInHand(hand).getItem() instanceof BlockItem && isPosNotModifiable(player, hit.getBlockPos().add(hit.getSide().getVector())))
		{
			player.sendMessage(getModifyFailText(), true);
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}
	
	protected ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction)
	{
		if(isPosNotModifiable(player, pos))
		{
			player.sendMessage(getModifyFailText(), true);
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}
	
	protected boolean isPosNotModifiable(PlayerEntity player, BlockPos pos)
	{
		if(player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isCreativeLevelTwoOp())
			return false;
		if(UltraComponents.EDITOR.get(player).isActive())
			return false;
		World world = player.getWorld();
		return !world.canPlayerModifyAt(player, pos);
	}
	
	abstract ServerWorld getWorld();
	
	abstract void onWorldLoad();
	
	abstract Text getModifyFailText();
}
