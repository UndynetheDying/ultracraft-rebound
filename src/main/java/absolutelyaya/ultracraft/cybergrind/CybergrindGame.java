package absolutelyaya.ultracraft.cybergrind;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.config.CybergrindConfig;
import absolutelyaya.ultracraft.config.IntegerEntry;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.joml.Vector4i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CybergrindGame
{
	final Map<EntityType<? extends HostileEntity>, Integer> spawnCosts;
	final MinecraftServer server;
	final CybergrindConfig config;
	final Random rand;
	final List<HostileEntity> enemies = new ArrayList<>();
	final List<PlayerEntity> participants = new ArrayList<>();
	ServerWorld world;
	BlockPos center;
	final int arenaRadius;
	final boolean arenaSolid;
	int delay, waves, currentWave, budget, duration;
	boolean initialized, over, win, curWaveDirty, enemiesDirty;
	PlayerEntity owner;
	
	public CybergrindGame(MinecraftServer server, CybergrindConfig config, Random rand)
	{
		this.server = server;
		this.config = config;
		this.rand = rand;
		ImmutableMap.Builder<EntityType<? extends HostileEntity>, Integer> costBuilder = ImmutableMap.builder();
		Layer curLayer = Layer.fromRegistryKey(MinecraftClient.getInstance().world.getRegistryKey());
		for (Map.Entry<EntityType<? extends HostileEntity>, IntegerEntry> entry : config.getCosts(curLayer).entrySet())
			costBuilder.put(entry.getKey(), entry.getValue().getValue());
		spawnCosts = costBuilder.build();
		arenaRadius = config.arenaRadius.getValue();
		arenaSolid = config.arenaBorderSolid.getValue();
		delay = /*Math.max(config.startDelay.getValue(), 1)*/ 1; //TODO: uncomment
		
		//TODO: on death or dimension change remove participant
		//TODO: add participants that enter the area for the first time
		//TODO: add border around arena that participants can't cross
		//TODO: render the border as well
	}
	
	void addParticipant(PlayerEntity player)
	{
		if(participants.contains(player))
			return;
		UltraComponents.WINGED.get(player).setCybergrindData(asData());
		participants.add(player);
	}
	
	void removeParticipant(PlayerEntity player)
	{
		if(!participants.contains(player))
			return;
		UltraComponents.WINGED.get(player).setCybergrindData(null);
		participants.remove(player);
	}
	
	public void tick()
	{
		if (isOver())
			return;
		duration++;
		if (delay > 0)
		{
			delay--;
			if (delay == 0 && !initialized)
			{
				delay += 600;
				init();
			}
			return;
		}
		if (budget > 0)
		{
			if (!spawnRandomEnemy())
			{
				Ultracraft.LOGGER.warn("Couldn't afford any enemy with the remaining wave Budget; Spawning " + budget + " Filths instead :D");
				while (budget-- > 0)
					spawn(EntityRegistry.FILTH); //Safeguard in case there's a remainder that can't be spent on any of the candidates.
			}
			return;
		}
		if (enemies.size() == 0)
		{
			if (currentWave < waves && budget <= 0)
				startWave();
			else if (currentWave >= waves)
				end();
		}
		else if(duration % 5 == 0)
		{
			int count = enemies.size();
			enemies.removeIf(e -> e.isDead() || e.isRemoved());
			if(count != enemies.size())
				enemiesDirty = true;
		}
		if(shouldSync())
			syncCybergrind();
	}
	
	void init()
	{
		List<ServerPlayerEntity> candidates = CybergrindManager.getPlayersInUltracraftDimensions(server);
		if(candidates.size() == 0)
		{
			server.getPlayerManager().broadcast(Text.translatable("message.ultracraft.cybergrind.announce-cancel"), false);
			initialized = true;
			end();
			return;
		}
		ServerPlayerEntity winner = candidates.get(rand.nextInt(candidates.size()));
		this.world = winner.getServerWorld();
		this.center = winner.getBlockPos();
		this.owner = winner;
		int difficulty = world.getDifficulty().getId();
		waves = config.wavesPerDifficultyBonus.getValue() * difficulty;
		for (int i = 0; i < difficulty; i++)
			waves += rand.nextBetween(config.wavesPerDifficultyLow.getValue(), config.wavesPerDifficultyHigh.getValue());
		initialized = true;
		addParticipant(owner);
		announceTarget();
		System.out.println("waves: " + waves);
	}
	
	void announceTarget()
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeText(participants.get(0).getDisplayName());
		server.getPlayerManager().getPlayerList().forEach(p -> ServerPlayNetworking.send(p, PacketRegistry.ANNOUNCE_CYBERGRIND, buf));
	}
	
	boolean shouldSync()
	{
		return curWaveDirty || enemiesDirty;
	}
	
	void syncCybergrind()
	{
		NbtCompound nbt = new NbtCompound();
		if (!over)
		{
			if (curWaveDirty)
				nbt.putInt("currentWave", currentWave);
			if (enemiesDirty)
				nbt.putInt("enemies", enemies.size());
			curWaveDirty = false;
			enemiesDirty = false;
		}
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeByte(over ? CybergrindData.DESTROY_SYNC : CybergrindData.PARTIAL_SYNC);
		if (!over)
			buf.writeNbt(nbt);
		server.getPlayerManager().getPlayerList().forEach(p -> ServerPlayNetworking.send(p, PacketRegistry.SYNC_CYBERGRIND, buf));
	}
	
	void startWave()
	{
		curWaveDirty = true;
		currentWave++;
		if (currentWave > waves)
		{
			end();
			return;
		}
		System.out.println("starting wave " + currentWave + " / " + waves);
		calculateBudget();
	}
	
	boolean spawnRandomEnemy()
	{
		List<EntityType<? extends HostileEntity>> candidates = new ArrayList<>();
		for (Map.Entry<EntityType<? extends HostileEntity>, Integer> entry : spawnCosts.entrySet())
			if (entry.getValue() <= budget)
				candidates.add(entry.getKey());
		if (candidates.size() == 0)
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
		enemies.add(type.spawn(world, center, SpawnReason.SPAWNER));
		enemiesDirty = true;
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
	
	void end()
	{
		participants.forEach(p -> UltraComponents.WINGED.get(p).setCybergrindData(null));
		over = true;
		CybergrindManager.Instance.startCooldown();
		System.out.println("Cybergrind ended.");
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
	
	/**
	 * @return [minX, minY, maxX, maxY]
	 */
	public Vector4i getArenaBounds()
	{
		if(center == null)
			return null;
		int x = center.getX(), z = center.getZ();
		return new Vector4i(x - arenaRadius, z - arenaRadius, x + arenaRadius, z + arenaRadius);
	}
	
	public CybergrindData asData()
	{
		CybergrindData data = new CybergrindData(waves, getArenaBounds());
		data.setEnemies(enemies.size());
		data.setCurrentWave(currentWave);
		data.setSolidBounds(arenaSolid);
		return data;
	}
}
