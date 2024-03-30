package absolutelyaya.ultracraft.cybergrind;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.config.CybergrindConfig;
import absolutelyaya.ultracraft.dimension.UltraDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class CybergrindManager
{
	public static CybergrindManager Instance;
	final CybergrindConfig config;
	final MinecraftServer server;
	final Random rand;
	CybergrindGame activeGame;
	boolean checkedTonight = true; //Prevent potentially starting Cybergrind immediately upon starting the Server
	int cooldown;
	
	public CybergrindManager(MinecraftServer server)
	{
		Instance = this;
		this.server = server;
		this.config = CybergrindConfig.INSTANCE;
		rand = Random.create();
	}
	
	public void tick()
	{
		if(activeGame == null)
		{
			boolean day = server.getOverworld().isDay();
			if(!day && !checkedTonight)
			{
				if(cooldown <= 0 && server.getOverworld().random.nextFloat() < config.startChance.getValue())
					startCybergrind();
				checkedTonight = true;
			}
			if(day && checkedTonight)
			{
				if(cooldown > 0)
					cooldown--;
				checkedTonight = false;
			}
		}
		
		if(activeGame != null)
		{
			if(activeGame.isOver())
				activeGame = null;
			else
				activeGame.tick();
		}
	}
	
	public static List<ServerPlayerEntity> getPlayersInUltracraftDimensions(MinecraftServer server)
	{
		List<ServerPlayerEntity> list = new ArrayList<>();
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
			if(UltraDimensions.Instance.isUltraDimension(player.getWorld()))
				list.add(player);
		return list;
	}
	
	public CybergrindGame startCybergrind(ServerPlayerEntity player, int waves)
	{
		server.getPlayerManager().broadcast(Text.translatable("message.ultracraft.cybergrind.announce"), false);
		activeGame = new CybergrindGame(server, config, rand, player, waves);
		return activeGame;
	}
	
	public void startCybergrind()
	{
		if(activeGame != null)
			endCybergrind();
		List<ServerPlayerEntity> candidates = getPlayersInUltracraftDimensions(server);
		if(candidates.size() == 0)
		{
			Ultracraft.LOGGER.info("Tried to start Cybergrind, but no Players were found in Ultracraft Dimensions...");
			return;
		}
		server.getPlayerManager().broadcast(Text.translatable("message.ultracraft.cybergrind.announce"), false);
		activeGame = new CybergrindGame(server, config, rand);
	}
	
	public CybergrindGame startCybergrindAt(BlockPos center, int waves)
	{
		if(activeGame != null)
			endCybergrind();
		server.getPlayerManager().broadcast(Text.translatable("message.ultracraft.cybergrind.announce-pos",
				center.getX(), center.getY(), center.getZ()), false);
		return activeGame = new CybergrindGame(server, config, rand, waves, center);
	}
	
	public void endCybergrind()
	{
		activeGame.end();
	}
	
	public void startCooldown()
	{
		cooldown = config.cooldown.getValue();
	}
	
	public void syncFullActiveGame(ServerPlayerEntity player)
	{
		if(activeGame != null)
			UltraComponents.WINGED.get(player).setCybergrindData(activeGame.asData());
		else
			UltraComponents.WINGED.get(player).setCybergrindData(null);
	}
	
	public CybergrindGame getActiveGame()
	{
		return activeGame;
	}
}
