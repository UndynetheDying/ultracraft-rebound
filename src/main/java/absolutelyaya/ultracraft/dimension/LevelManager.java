package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.data.LevelCollectionManager;
import absolutelyaya.ultracraft.data.LevelData;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.entity.demon.MaliciousFaceEntity;
import absolutelyaya.ultracraft.registry.BlockRegistry;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static absolutelyaya.ultracraft.data.LevelDataManager.getAllCustomLevels;
import static absolutelyaya.ultracraft.data.LevelDataManager.getLevelData;

public class LevelManager extends DimensionManager
{
	public static LevelManager Instance;
	public static final Identifier ID = new Identifier(Ultracraft.MOD_ID, "levels");
	public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, ID);
	ServerWorld world;
	
	static final Map<Identifier, LevelInstancePool> instances = new HashMap<>();
	static final Map<String, Identifier> levelIdForInstanceId = new HashMap<>();
	static int nextLevelBaseX = 0;
	
	public LevelManager()
	{
		Instance = this;
		new LevelDataManager();
		new LevelCollectionManager();
	}
	
	public void init(ServerWorld world)
	{
		if(this.world == null)
			this.world = world;
	}
	
	public void onServerStop()
	{
		destroyAllInstances();
		this.world = null;
	}
	
	public LevelInstance getInstance(String id)
	{
		Identifier level = levelIdForInstanceId.get(id);
		if(level == null)
			return null;
		LevelInstancePool pool = instances.get(level);
		return pool.get(id);
	}
	
	public void removeInstance(String id)
	{
		LevelInstance instance = getInstance(id);
		if(instance == null)
			return;
		destroyInstance(id);
		instances.get(levelIdForInstanceId.get(id)).remove(id);
		levelIdForInstanceId.remove(id);
	}
	
	public static BlockPos getSpawnPos(String instanceId)
	{
		Identifier levelId = levelIdForInstanceId.getOrDefault(instanceId, new Identifier(""));
		LevelData data = getLevelData(levelId);
		if(data == null)
		{
			Ultracraft.LOGGER.warn("Getting spawnpoint for level '{}' failed; Level wasn't found!", instanceId);
			return null;
		}
		if(!instances.containsKey(levelId))
		{
			Ultracraft.LOGGER.warn("Getting spawnpoint for level '{}' failed; Level wasn't instantiated!", instanceId);
			return null;
		}
		return instances.get(levelId).get(instanceId).pos.add(data.getSpawnOffset());
	}
	
	public Pair<String, LevelInstance> instantiateLevel(Identifier levelId, boolean privat)
	{
		LevelInstancePool pool = instances.get(levelId);
		return instantiateLevel(levelId, pool == null ? new BlockPos(nextLevelBaseX, 0, 0) : pool.getFirstFreePos(), privat);
	}
	
	public Pair<String, LevelInstance> instantiateLevel(Identifier levelId, BlockPos pos, boolean privat)
	{
		if(!LevelDataManager.isLevelExists(levelId))
		{
			Ultracraft.LOGGER.warn("Level Structure {} instantiation failed; level data not found!", levelId);
			return null;
		}
		LevelData data = getLevelData(levelId);
		Identifier structure = data.getStructure();
		if(structure == null)
		{
			Ultracraft.LOGGER.info("Level Structure {} instantiation failed; structure is null", levelId);
			return null;
		}
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Ultracraft.LOGGER.info("Placing Level Structure {} at {}", structure, pos.toString());
		AtomicReference<Pair<String, LevelInstance>> inst = new AtomicReference<>();
		templateManager.getTemplate(structure)
				.ifPresent(i -> {
					i.place(world, pos, new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
					BlockBox box = i.calculateBoundingBox(pos, BlockRotation.NONE, new BlockPos(0, 0, 0), BlockMirror.NONE);
					Iterable<BlockPos> blocks = BlockPos.iterate(pos, pos.add(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ()));
					blocks.forEach(p -> {
						BlockEntity block = world.getBlockEntity(p);
						if(block instanceof AbstractMappingBlockEntity mapBlock)
							mapBlock.reset();
					});
					List<Entity> list = world.getOtherEntities(null, new Box(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ()));
					list.forEach(e -> {
						boolean decorative = e instanceof MaliciousFaceEntity malicious && malicious.isDecorative();
						if(!(decorative || e instanceof PlayerEntity || e instanceof DisplayEntity || e instanceof AbstractDecorationEntity))
							e.remove(Entity.RemovalReason.DISCARDED);
					});
					LevelInstancePool pool = instances.computeIfAbsent(levelId, k -> {
						nextLevelBaseX += box.getBlockCountX() + 128;
						return new LevelInstancePool(pos, box);
					});
					int idx = pool.getFirstFreeIndex();
					LevelInstance newInstance = new LevelInstance(pos, idx);
					if(privat)
						newInstance.setPrivate();
					String newId = levelId.toString() + "_" + idx;
					levelIdForInstanceId.put(newId, levelId);
					Ultracraft.LOGGER.info("New Level Instance ID: {}", newId);
					inst.set(new Pair<>(newId, newInstance));
					pool.put(newId, newInstance);
					
					for (int x = 0; x < box.getBlockCountX(); x += 16)
						for (int z = 0; z < box.getBlockCountZ(); z += 16)
							world.getChunkManager().setChunkForced(new ChunkPos(new BlockPos(x + box.getMinX(), 0, z + box.getMinZ())), true);
				});
		if(inst.get() == null)
			Ultracraft.LOGGER.warn("Level Structure {} placement failed; won't teleport Player.", structure);
		else
			Ultracraft.LOGGER.info("Level Structure {} placed successfully.", structure);
		return inst.get();
	}
	
	public void destroyAllInstances()
	{
		for (LevelInstancePool pool : instances.values())
			for (String id : new ArrayList<>(pool.instances.keySet()))
				destroyInstance(id);
		instances.clear();
		levelIdForInstanceId.clear();
		nextLevelBaseX = 0;
	}
	
	public void destroyIfEmpty(@Nullable PlayerEntity except, String id)
	{
		if(!isLevelEmpty(except, id))
			return;
		removeInstance(id);
	}
	
	public void destroyInstance(String id)
	{
		destroyInstance(id, false);
	}
	
	public void destroyInstance(String instanceId, boolean reload)
	{
		Identifier levelId = levelIdForInstanceId.get(instanceId);
		if(levelId == null)
			return; //no levels of this type instantiated at all, no destruction necessary
		LevelInstancePool pool = instances.get(levelId);
		if(pool == null)
			return; //level type not instantiated at all, no destruction necessary
		LevelInstance instance = pool.get(instanceId);
		if(instance == null)
		{
			Ultracraft.LOGGER.warn("Level Structure {} destruction failed; level instance not found!", instanceId);
			return;
		}
		if(!reload)
		{
			List<ServerPlayerEntity> needsRescue = new ArrayList<>(instance.players); //prevent concurrent modification
			needsRescue.forEach(p -> rescue(p, RescueReason.INSTANCE_DESTROYED));
		}
		Ultracraft.LOGGER.info("Destroying Level Instance {}", instanceId);
		BlockPos pos = instance.pos;
		BlockBox box = pool.bounds;
		Iterable<BlockPos> blocks = BlockPos.iterate(pos, pos.add(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ()));;
		List<BlockPos> replaced = new ArrayList<>();
		boolean prevTileDrops = world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS);
		world.getGameRules().get(GameRules.DO_TILE_DROPS).set(false, world.getServer());
		blocks.forEach(block -> {
			Clearable.clear(world.getBlockEntity(block));
			if(world.setBlockState(block, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS))
				replaced.add(block);
		});
		world.getGameRules().get(GameRules.DO_TILE_DROPS).set(prevTileDrops, world.getServer());
		replaced.forEach(p -> world.updateNeighbors(p, world.getBlockState(p).getBlock()));
		List<Entity> list = world.getOtherEntities(null, new Box(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ()));
		list.forEach(e -> {
			if(!(e instanceof PlayerEntity))
				e.remove(Entity.RemovalReason.DISCARDED);
		});
		instances.get(levelId).remove(instanceId);
		Ultracraft.LOGGER.info("Finished Destroying Level Instance {}", instanceId);
		
		for (int x = 0; x < box.getBlockCountX(); x += 16)
			for (int z = 0; z < box.getBlockCountZ(); z += 16)
				world.getChunkManager().setChunkForced(new ChunkPos(new BlockPos(x + box.getMinX(), 0, z + box.getMinZ())), false);
	}
	
	void rescue(ServerPlayerEntity player, RescueReason reason)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		winged.sendBoxTitle(Text.translatable(reason.message));
		ServerWorld overworld = world.getServer().getOverworld();
		FabricDimensions.teleport(player, overworld, new TeleportTarget(overworld.getSpawnPos().toCenterPos(), Vec3d.ZERO, player.getYaw(), player.getPitch()));
		ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(player);
		levelStats.enterLevel(null, null);
	}
	
	public void reloadInstance(String id, boolean privat)
	{
		Ultracraft.LOGGER.info("Re-Placing Level Instance {}", id);
		LevelInstance old = getInstance(id);
		if(old == null)
		{
			Ultracraft.LOGGER.info("Tried reloading a level instance that didn't exist: ({})", id);
			return;
		}
		Pair<String, LevelInstance> newInstance = instantiateLevel(levelIdForInstanceId.get(id), privat);
		for (ServerPlayerEntity p : new ArrayList<>(old.players))
			joinInstance(p, newInstance.getLeft());
	}
	
	public void joinInstance(ServerPlayerEntity player, String id)
	{
		LevelInstance instance = getInstance(id);
		if(instance == null)
			rescue(player, RescueReason.INSTANCE_NULL);
		else
		{
			if(!instance.players.contains(player))
				instance.players.add(player);
			if(instance.getOwner() == null)
				instance.setOwner(player);
		}
		//teleport player
		ServerWorld world = getWorld().getServer().getWorld(LevelManager.WORLD_KEY);
		BlockPos spawnPos = LevelManager.getSpawnPos(id);
		FabricDimensions.teleport(player, world, new TeleportTarget(spawnPos.toCenterPos(), Vec3d.ZERO,
				LevelDataManager.getLevelData(levelIdForInstanceId.get(id)).getSpawnRot(), 0f));
		BlockHitResult groundScan = player.getWorld().raycast(new RaycastContext(player.getPos(), player.getPos().subtract(0, 32, 0),
				RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
		if(groundScan.getType().equals(HitResult.Type.MISS))
			player.getWorld().setBlockState(player.getBlockPos().down(), BlockRegistry.PORTAL.getDefaultState());
		
		ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(player);
		levelStats.enterLevel(levelIdForInstanceId.get(id), id);
	}
	
	public void leaveInstance(ServerPlayerEntity player, String id)
	{
		LevelInstance instance = getInstance(id);
		if(instance == null)
		{
			rescue(player, RescueReason.INSTANCE_NULL);
			return;
		}
		instance.players.remove(player);
		destroyIfEmpty(player, id);
	}
	
	boolean isLevelEmpty(@Nullable PlayerEntity except, String id)
	{
		LevelInstance instance = getInstance(id);
		if(instance == null)
			return true;
		for (PlayerEntity player : instance.players)
		{
			if(player.equals(except))
				continue;
			return false;
		}
		return true;
	}
	
	@Override
	public void tick()
	{
		for(ServerPlayerEntity player : new ArrayList<>(world.getPlayers()))
		{
			if(player.getY() < world.getBottomY() - 10)
				rescue(player, RescueReason.VOID);
		}
	}
	
	@Override
	public ServerWorld getWorld()
	{
		return world;
	}
	
	@Override
	public void onWorldLoad()
	{
	
	}
	
	@Override
	Text getModifyFailText()
	{
		return Text.empty();
	}
	
	public void debugInstanceEverythingALot()
	{
		List<Identifier> allLevels = new ArrayList<>(LevelDataManager.getAllLevels().keySet());
		allLevels.addAll(getAllCustomLevels().keySet());
		int total = allLevels.size() * 8, done = 0;
		for (Identifier id : allLevels)
		{
			for (int i = 0; i < 8; i++)
			{
				instantiateLevel(id, false);
				done++;
				for (PlayerEntity player : world.getServer().getPlayerManager().getPlayerList())
					player.sendMessage(Text.translatable("command.ultracraft.debug.level-instance.progress", i + 1, id, done, total));
			}
		}
	}
	
	public boolean isInstanceExistant(String instanceId)
	{
		return levelIdForInstanceId.containsKey(instanceId);
	}
	
	public NbtCompound serializePool(Identifier levelId)
	{
		NbtCompound nbt = new NbtCompound();
		LevelInstancePool pool = instances.get(levelId);
		if(pool == null)
			return nbt;
		for (Map.Entry<String, LevelInstance> entry : pool.getAll().entrySet())
		{
			LevelInstance val = entry.getValue();
			if( val.isPrivate())
				continue;
			NbtCompound instance = new NbtCompound();
			instance.putUuid("owner", val.getOwner() == null ? UUID.randomUUID() : val.getOwner().getUuid());
			nbt.put(entry.getKey(), instance);
		}
		return nbt;
	}
	
	public static class LevelInstancePool
	{
		static final int separation = 128;
		public final HashMap<String, LevelInstance> instances = new HashMap<>();
		public final BlockPos basePos;
		public final BlockBox bounds;
		
		public LevelInstancePool(BlockPos basePos, BlockBox bounds)
		{
			this.basePos = basePos;
			this.bounds = bounds;
		}
		
		int getFirstFreeIndex()
		{
			List<LevelInstance> instance =  instances.values().stream().sorted().toList();
			for (int i = 0; i < instances.size(); i++)
				if(instance.get(i).index != i)
					return i;
			return instance.size();
		}
		
		BlockPos getFirstFreePos()
		{
			return basePos.add(0, 0, getFirstFreeIndex() * (bounds.getBlockCountZ() + separation));
		}
		
		public LevelInstance get(String id)
		{
			return instances.get(id);
		}
		
		public LevelInstance remove(String id)
		{
			return instances.remove(id);
		}
		
		public LevelInstance put(String id, LevelInstance instance)
		{
			return instances.put(id, instance);
		}
		
		public HashMap<String, LevelInstance> getAll()
		{
			return instances;
		}
	}
	
	public static class LevelInstance implements Comparable<LevelInstance>
	{
		public final List<ServerPlayerEntity> players = new ArrayList<>();
		public final BlockPos pos;
		public final int index;
		public ServerPlayerEntity owner;
		public boolean privat;
		public int kills, lastCheckpointKills;
		
		public LevelInstance(BlockPos pos, int index)
		{
			this.pos = pos;
			this.index = index;
		}
		
		public ServerPlayerEntity getOwner()
		{
			return owner;
		}
		
		public void setOwner(ServerPlayerEntity owner)
		{
			this.owner = owner;
			addPlayer(owner);
		}
		
		public void addPlayer(ServerPlayerEntity player)
		{
			if(!players.contains(player))
				players.add(player);
		}
		
		public void setPrivate()
		{
			privat = true;
		}
		
		public boolean isPrivate()
		{
			return privat;
		}
		
		@Override
		public int compareTo(@NotNull LevelManager.LevelInstance o)
		{
			return index - o.index;
		}
		
		public void onCheckpoint()
		{
			if(!(Instance.world.isClient && !Instance.world.getServer().isRemote()))
				lastCheckpointKills = kills;
		}
		
		public void restoreLastCheckpointKills()
		{
			if(!(Instance.world.isClient && !Instance.world.getServer().isRemote()))
				setKills(lastCheckpointKills);
		}
		
		public void onKill()
		{
			if(!(Instance.world.isClient && !Instance.world.getServer().isRemote()))
				setKills(kills + 1);
		}
		
		public int getKills()
		{
			return kills;
		}
		
		void setKills(int i)
		{
			kills = i;
			players.forEach(p -> UltraComponents.LEVEL_STATS.get(p).setKills(i));
		}
	}
	
	enum RescueReason
	{
		INSTANCE_DESTROYED("message.ultracraft.rescue.level-destroyed"),
		VOID("message.ultracraft.rescue.void"),
		INSTANCE_NULL("message.ultracraft.rescue.null");
		public final String message;
		
		RescueReason(String message)
		{
			this.message = message;
		}
	}
}
