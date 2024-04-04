package absolutelyaya.ultracraft.dimension;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class UltraDimensions
{
	public static UltraDimensions Instance;
	
	final LimboManager LIMBO_MANAGER;
	final LevelManager LEVEL_MANAGER;
	final Map<Identifier, DimensionManager> managers = new HashMap<>();
	
	public UltraDimensions(MinecraftServer server)
	{
		Instance = this;
		
		managers.put(LimboManager.ID, LIMBO_MANAGER = new LimboManager(server.getWorld(LimboManager.WORLD_KEY)));
		managers.put(LevelManager.ID, LEVEL_MANAGER = LevelManager.Instance);
		LevelManager.Instance.init(server.getWorld(LevelManager.WORLD_KEY));
		
		ServerWorldEvents.LOAD.register(this::onWorldLoad);
	}
	
	public void tickManagers()
	{
		for (DimensionManager manager : managers.values())
		{
			if(manager.getWorld() != null && manager.getWorld().getPlayers().size() > 0)
				manager.tick();
		}
	}
	
	public void onSuppressedModification(PlayerEntity player)
	{
		Identifier key = player.getWorld().getRegistryKey().getValue();
		if(!managers.containsKey(key))
			return;
		managers.get(key).onSuppressedModification(player);
	}
	
	public void onWorldLoad(MinecraftServer server, ServerWorld world)
	{
		Identifier key = world.getRegistryKey().getValue();
		if(!managers.containsKey(key))
			return;
		managers.get(key).onWorldLoad();
	}
	
	public boolean isUltraDimension(World world)
	{
		RegistryKey<DimensionType> key = world.getDimensionKey();
		return LIMBO_MANAGER.world.getDimensionKey().equals(key);
	}
}
