package absolutelyaya.ultracraft.entity;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.config.CybergrindConfig;
import absolutelyaya.ultracraft.config.IntegerEntry;
import absolutelyaya.ultracraft.dimension.UltraDimensions;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		
		if(activeGame != null)
		{
			if(activeGame.isOver())
				activeGame = null;
			else
				activeGame.tick();
		}
	}
	
	List<PlayerEntity> getPlayersInUltracraftDimensions()
	{
		List<PlayerEntity> list = new ArrayList<>();
		for (PlayerEntity player : server.getPlayerManager().getPlayerList())
		{
			if(UltraDimensions.Instance.isUltraDimension(player.getWorld()))
				list.add(player);
		}
		return list;
	}
	
	public void startCybergrind()
	{
		server.sendMessage(Text.translatable("message.ultracraft.cybergrind.announce"));
		List<PlayerEntity> candidates = getPlayersInUltracraftDimensions();
		PlayerEntity winner = candidates.get(rand.nextInt(candidates.size()));
		World world = winner.getWorld();
		activeGame = new CybergrindGame(world, config, rand, winner);
	}
	
	public void endCybergrind()
	{
		cooldown = config.cooldown.getValue();
	}
	
	static class CybergrindGame
	{
		final Map<EntityType<? extends HostileEntity>, Integer> spawnCosts;
		final World world;
		final CybergrindConfig config;
		final Random rand;
		final List<HostileEntity> enemies = new ArrayList<>();
		final List<PlayerEntity> participants = new ArrayList<>();
		final BlockPos center;
		final int arenaRadius;
		final boolean arenaSolid;
		int delay, waves, currentWave, budget;
		boolean targetAnnounced, over, win;
		
		public CybergrindGame(World world, CybergrindConfig config, Random rand, PlayerEntity owner)
		{
			this.world = world;
			this.config = config;
			this.rand = rand;
			center = owner.getBlockPos();
			ImmutableMap.Builder<EntityType<? extends HostileEntity>, Integer> costBuilder = ImmutableMap.builder();
			Layer curLayer = Layer.fromRegistryKey(MinecraftClient.getInstance().world.getRegistryKey());
			for(Map.Entry<EntityType<? extends HostileEntity>, IntegerEntry> entry : config.getCosts(curLayer).entrySet())
				costBuilder.put(entry.getKey(), entry.getValue().getValue());
			spawnCosts = costBuilder.build();
			arenaRadius = config.arenaRadius.getValue();
			arenaSolid = config.arenaBorderSolid.getValue();
			delay = /*Math.max(config.startDelay.getValue(), 1)*/ 1;
			
			int difficulty = world.getDifficulty().getId();
			waves = config.wavesPerDifficultyBonus.getValue() * difficulty;
			for (int i = 0; i < difficulty; i++)
				waves += rand.nextBetween(config.wavesPerDifficultyLow.getValue(), config.wavesPerDifficultyHigh.getValue());
			System.out.println("waves: " + waves);
			participants.add(owner);
			world.getServer().sendMessage(Text.translatable("message.ultracraft.cybergrind.chosen", owner.getDisplayName()));
			//TODO: on death remove participant
			//TODO: add participants that enter the area for the first time
			//TODO: add border around arena that participants can't cross
			//TODO: render the border as well
		}
		
		public void tick()
		{
			if(delay > 0)
			{
				delay--;
				if(delay == 0 && !targetAnnounced)
				{
					delay += 600;
					announceTarget();
				}
				return;
			}
			//System.out.println(currentWave + " / " + waves + " w | " + enemies.size() + " " + budget);
			if(currentWave < waves && enemies.size() == 0 && budget <= 0)
				startWave();
			if(budget > 0)
			{
				if(!spawnRandomEnemy())
				{
					Ultracraft.LOGGER.warn("Couldn't afford any enemy with the remaining wave Budget; Spawning " + budget + " Filths instead :D");
					while (budget-- > 0)
						spawn(EntityRegistry.FILTH); //Safeguard in case there's a remainder that can't be spent on any of the candidates.
				}
			}
		}
		
		void announceTarget()
		{
			targetAnnounced = true;
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeText(participants.get(0).getDisplayName());
			world.getServer().getPlayerManager().getPlayerList().forEach(p -> ServerPlayNetworking.send(p, PacketRegistry.ANNOUNCE_CYBERGRIND, buf));
		}
		
		void startWave()
		{
			currentWave++;
			System.out.println("starting wave " + currentWave + " / " + waves);
			calculateBudget();
		}
		
		boolean spawnRandomEnemy()
		{
			List<EntityType<? extends HostileEntity>> candidates = new ArrayList<>();
			for (Map.Entry<EntityType<? extends HostileEntity>, Integer> entry : spawnCosts.entrySet())
				if(entry.getValue() <= budget)
					candidates.add(entry.getKey());
			if(candidates.size() == 0)
				return false;
			EntityType<? extends HostileEntity> winner = candidates.get(rand.nextInt(candidates.size()));
			budget -= spawnCosts.get(winner);
			System.out.println(winner.toString() + " -> remaining budget: " + budget);
			spawn(winner);
			return true;
		}
		
		void spawn(EntityType<? extends HostileEntity> type)
		{
			//TODO: spawn at a random position within the arena
			//TODO: always spawn with {boss:0b}
		}
		
		void calculateBudget()
		{
			int difficulty = world.getDifficulty().getId();
			budget = config.baseBudget.getValue();
			for (int i = 0; i < currentWave; i++)
				budget += rand.nextBetween(config.minBudgetPerWave.getValue(), config.maxBudgetPerWave.getValue());
			budget += config.difficultyBudgetBonus.getValue() * difficulty;
			System.out.println("Wave Budget: " + budget);
		}
		
		public boolean isOver()
		{
			return over;
		}
		
		public boolean isBorderSolid()
		{
			return arenaSolid;
		}
		
		public int getArenaRadius()
		{
			return arenaRadius;
		}
	}
}
