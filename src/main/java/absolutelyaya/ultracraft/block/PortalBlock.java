package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.client.gui.screen.TravelScreen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalBlock extends Block
{
	public PortalBlock(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if(world.isClient)
			MinecraftClient.getInstance().setScreen(new TravelScreen(false));
		return ActionResult.PASS;
	}
}
