package absolutelyaya.ultracraft.dimension;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class UltraDimensions
{
	public static UltraDimensions Instance;
	
	final LimboManager LIMBO_MANAGER;
	final Map<Identifier, DimensionManager> managers = new HashMap<>();
	
	public UltraDimensions(MinecraftServer server)
	{
		Instance = this;
		
		managers.put(LimboManager.ID, LIMBO_MANAGER = new LimboManager(server.getWorld(LimboManager.WORLD_KEY)));
		
		ServerWorldEvents.LOAD.register(this::onWorldLoad);
	}
	
	public void tickManagers()
	{
		for (DimensionManager manager : managers.values())
		{
			if(manager.getWorld().getPlayers().size() > 0)
				manager.tick();
		}
	}
	
	public ActionResult onBlockInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit)
	{
		Identifier key = world.getRegistryKey().getValue();
		if(!managers.containsKey(key))
			return ActionResult.PASS;
		return managers.get(key).onBlockInteract(player, world, hand, hit);
	}
	
	public ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction)
	{
		Identifier key = world.getRegistryKey().getValue();
		if(!managers.containsKey(key))
			return ActionResult.PASS;
		return managers.get(key).onAttackBlock(player, world, hand, pos, direction);
	}
	
	public void onWorldLoad(MinecraftServer server, ServerWorld world)
	{
		Identifier key = world.getRegistryKey().getValue();
		if(!managers.containsKey(key))
			return;
		managers.get(key).onWorldLoad();
	}
}
