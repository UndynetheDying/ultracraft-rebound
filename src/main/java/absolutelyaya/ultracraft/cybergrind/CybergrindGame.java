package absolutelyaya.ultracraft.cybergrind;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.config.CybergrindConfig;
import absolutelyaya.ultracraft.config.IntegerEntry;
import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import absolutelyaya.ultracraft.entity.machine.DroneEntity;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import org.joml.Vector4i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CybergrindGame
{
	static final Multimap<EntityAttribute, EntityAttributeModifier> weitsichtModifier = HashMultimap.create();
	final Map<EntityType<? extends HostileEntity>, Integer> spawnCosts = new HashMap<>();
	final MinecraftServer server;
	final CybergrindConfig config;
	final Random rand;
	final List<HostileEntity> enemies = new ArrayList<>();
	final List<PlayerEntity> participants = new ArrayList<>();
	final int arenaRadius;
	final boolean arenaSolid;
	ServerWorld world;
	BlockPos center;
	int delay, waves, currentWave, budget, duration;
	boolean initialized, over, win, curWaveDirty, enemiesDirty, verbose, skipTargetAnnounce;
	ServerPlayerEntity owner;
	
	public CybergrindGame(MinecraftServer server, CybergrindConfig config, Random rand, ServerPlayerEntity owner, int waves)
	{
		this.server = server;
		this.config = config;
		this.rand = rand;
		arenaRadius = config.arenaRadius.getValue();
		arenaSolid = config.arenaBorderSolid.getValue();
		delay = Math.max(config.startDelay.getValue(), 1);
		this.owner = owner;
		if(waves != -1)
			this.waves = waves;
	}
	
	public CybergrindGame(MinecraftServer server, CybergrindConfig config, Random rand, int waves, BlockPos center)
	{
		this(server, config, rand, null, waves);
		this.center = center;
		skipTargetAnnounce = true;
	}
	
	public CybergrindGame(MinecraftServer server, CybergrindConfig config, Random rand)
	{
		this(server, config, rand, null, -1);
	}
	
	void addParticipant(PlayerEntity player)
	{
		if(participants.contains(player))
			return;
		UltraComponents.WINGED.get(player).setCybergrindData(asData());
		participants.add(player);
	}
	
	public void removeParticipant(PlayerEntity player)
	{
		if(!participants.contains(player))
			return;
		UltraComponents.WINGED.get(player).setCybergrindData(null);
		participants.remove(player);
		if(participants.size() == 0)
			end();
	}
	
	public List<PlayerEntity> getParticipants()
	{
		return participants;
	}
	
	public void tick()
	{
		if (isOver())
			return;
		duration++;
		if (delay > 0)
		{
			delay--;
			if (delay == 0)
			{
				if(!initialized)
				{
					if(!skipTargetAnnounce)
						delay += 600;
					init();
				}
				else if(center == null && owner != null)
				{
					center = owner.getBlockPos();
					addParticipant(owner);
				}
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
			{
				win = true;
				end();
			}
		}
		else if(duration % 5 == 0)
		{
			int count = enemies.size();
			enemies.removeIf(e -> e.isDead() || e.isRemoved());
			if(count != enemies.size())
			{
				if(enemies.size() == 0)
				{
					delay = 60;
					participants.forEach(p -> {
						p.setHealth(p.getMaxHealth());
						p.sendMessage(Text.translatable("message.ultracraft.cybergrind.wave-complete", currentWave)
											  .setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
					});
				}
				enemiesDirty = true;
			}
			if(count == 1)
				enemies.forEach(e -> e.setGlowing(true));
		}
		if(duration % 20 == 0) //add players within arena bounds to the participant list
		{
			Vector4i bounds = getArenaBounds();
			for (ServerPlayerEntity player : world.getPlayers())
				if(!participants.contains(player) && player.getX() > bounds.x && player.getX() < bounds.z && player.getZ() > bounds.y && player.getZ() < bounds.w)
					addParticipant(player);
		}
		if(shouldSync())
			syncCybergrind();
	}
	
	void init()
	{
		ServerPlayerEntity winner = initStartingPlayer();
		this.world = winner.getServerWorld();
		this.owner = winner;
		Layer curLayer = Layer.fromRegistryKey(world.getRegistryKey());
		for (Map.Entry<EntityType<? extends HostileEntity>, IntegerEntry> entry : config.getCosts(curLayer).entrySet())
			spawnCosts.put(entry.getKey(), entry.getValue().getValue());
		int difficulty = world.getDifficulty().getId();
		if(waves == 0)
		{
			waves = config.wavesPerDifficultyBonus.getValue() * difficulty;
			for (int i = 0; i < difficulty; i++)
				waves += rand.nextBetween(config.wavesPerDifficultyLow.getValue(), config.wavesPerDifficultyHigh.getValue());
		}
		initialized = true;
		if(!skipTargetAnnounce)
			announceTarget();
	}
	
	ServerPlayerEntity initStartingPlayer()
	{
		if(owner != null && !owner.isRemoved())
			return owner;
		List<ServerPlayerEntity> candidates = CybergrindManager.getPlayersInUltracraftDimensions(server);
		if(candidates.size() == 0)
		{
			server.getPlayerManager().broadcast(Text.translatable("message.ultracraft.cybergrind.announce-cancel"), false);
			initialized = true;
			end();
			return null;
		}
		return candidates.get(rand.nextInt(candidates.size()));
	}
	
	void announceTarget()
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeText(owner.getDisplayName());
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
		participants.forEach(p -> {
			if(p instanceof ServerPlayerEntity serverPlayer)
				ServerPlayNetworking.send(serverPlayer, PacketRegistry.SYNC_CYBERGRIND, buf);
		});
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
		participants.forEach(Ultracraft::rechargeWeapons);
		verboseLog("starting wave " + currentWave);
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
		if(spawn(winner))
		{
			int cost = spawnCosts.get(winner);
			budget -= cost;
			verboseLog("§7budget: " + (budget + cost) + " -> " + budget);
		}
		return true;
	}
	
	boolean spawn(EntityType<? extends HostileEntity> type)
	{
		verboseLog("§8attempting to spawn " + type.toString());
		SnowballEntity temp = new SnowballEntity(EntityType.SNOWBALL, world);
		Vector4i bounds = getArenaBounds();
		boolean success = false;
		for (int i = 0; i < 16; i++)
		{
			int margin = arenaRadius / 2;
			Vec3d pos = new Vec3d(rand.nextBetween(bounds.x + margin, bounds.z - margin) + 0.5, center.getY() + 16,
					rand.nextBetween(bounds.y + margin, bounds.w - margin) + 0.5);
			BlockHitResult hit = world.raycast(new RaycastContext(pos, pos.add(0, -64, 0),
					RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, temp));
			verboseLog("§8spawn attempt " + (i + 1));
			if(!hit.getType().equals(HitResult.Type.MISS))
			{
				pos = hit.getPos();
				if(!world.isSpaceEmpty(type.getDimensions().getBoxAt(pos)) || i == 3)
					continue;
				HostileEntity enemy = type.spawn(world, BlockPos.ofFloored(pos), SpawnReason.SPAWNER);
				if(enemy instanceof AbstractUltraHostileEntity ultraEnemy)
					ultraEnemy.markCybergrind();
				if(!(enemy instanceof DroneEntity))
					enemy.setPersistent();
				enemy.getAttributes().addTemporaryModifiers(weitsichtModifier);
				enemies.add(enemy);
				success = true;
				verboseLog("§7enemy spawned at " + pos);
				break;
			}
		}
		temp.remove(Entity.RemovalReason.DISCARDED);
		enemiesDirty = true;
		return success;
	}
	
	void calculateBudget()
	{
		int difficulty = world.getDifficulty().getId();
		budget = config.baseBudget.getValue();
		for (int i = 0; i < currentWave; i++)
			budget += rand.nextBetween(config.minBudgetPerWave.getValue(), config.maxBudgetPerWave.getValue());
		budget += config.difficultyBudgetBonus.getValue() * difficulty;
		verboseLog("§bround budget -> " + budget);
	}
	
	public void setVerbose()
	{
		verbose = true;
	}
	
	void verboseLog(String message)
	{
		if(verbose)
			server.getPlayerManager().broadcast(Text.of(message), false);
	}
	
	public void end()
	{
		participants.forEach(p -> UltraComponents.WINGED.get(p).setCybergrindData(null));
		enemies.forEach(e -> e.remove(Entity.RemovalReason.DISCARDED));
		over = true;
		CybergrindManager.Instance.startCooldown();
		if(initialized)
			server.getPlayerManager().broadcast(Text.translatable("message.ultracraft.cybergrind.end" + (win ? "-win" : "")), false);
		else
			server.getPlayerManager().broadcast(Text.translatable("message.ultracraft.cybergrind.cancel"), false);
	}
	
	public boolean isOver()
	{
		return over;
	}
	
	public boolean isWin()
	{
		return win;
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
		if(getArenaBounds() == null)
			return null;
		CybergrindData data = new CybergrindData(waves, getArenaBounds());
		data.setEnemies(enemies.size());
		data.setCurrentWave(currentWave);
		data.setSolidBounds(arenaSolid);
		return data;
	}
	
	public BlockPos getCenter()
	{
		return center;
	}
	
	static {
		weitsichtModifier.put(EntityAttributes.GENERIC_FOLLOW_RANGE,
				new EntityAttributeModifier("WEITSICHTENERGIE", 16, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
	}
}
