package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class LevelManager extends JsonDataLoader implements DimensionManager
{
	public static final LevelData ERR_DATA = new LevelData(new Identifier(Ultracraft.MOD_ID, "placeholder"),
			"level.ultracraft.error.title", "level.ultracraft.error.description", "", "", null,
			new Identifier(Ultracraft.MOD_ID, "textures/level/err.png"), BlockPos.ORIGIN, true);
	public static final Identifier PLACEHOLDER_THUMB = new Identifier(Ultracraft.MOD_ID, "textures/level/placeholder.png");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static LevelManager Instance;
	public static final Identifier ID = new Identifier(Ultracraft.MOD_ID, "levels");
	public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, ID);
	ServerWorld world;
	public static Map<Identifier, LevelData> levels = new HashMap<>();
	public static Map<Identifier, LevelData> customLevels = new HashMap<>();
	static final Map<Identifier, HashMap<String, LevelInstance>> instances = new HashMap<>();
	static final Map<Identifier, BlockBox> levelBounds = new HashMap<>();
	static final Map<String, Identifier> levelIdForInstanceId = new HashMap<>();
	static BlockPos nextLevelPos = new BlockPos(0, 0, 0); //TODO make it so that positions are picked based on level type and instances of each
	
	public LevelManager()
	{
		super(GSON, "ultracraft/level");
		Instance = this;
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener()
		{
			@Override
			public Identifier getFabricId()
			{
				return new Identifier(Ultracraft.MOD_ID, "ultracraft/level");
			}
			
			@Override
			public void reload(ResourceManager manager)
			{
				apply(prepare(manager, null), manager, null);
			}
		});
	}
	
	public void init(ServerWorld world)
	{
		if(this.world == null)
			this.world = world;
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler)
	{
		destroyAllLevels();
		nextLevelPos = new BlockPos(0, 0, 0);
		ImmutableMap.Builder<Identifier, LevelData> builtinBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<Identifier, LevelData> customBuilder = ImmutableMap.builder();
		prepared.forEach((id, element) -> {
			JsonObject json = element.getAsJsonObject();
			boolean builtin = JsonHelper.getBoolean(json, "builtin", false);
			if(!json.has("structure"))
			{
				Ultracraft.LOGGER.warn((builtin ? "Level '" : "Custom Level '") + id + "' does not have a structure parameter!");
				return;
			}
			String author = JsonHelper.getString(json, "author", "level.author.unknown");
			String authorlink = JsonHelper.getString(json, "author-link", "");
			String title = JsonHelper.getString(json, "title", "level.unnamed");
			String description = JsonHelper.getString(json, "description", "");
			Identifier structure = Identifier.tryParse(JsonHelper.getString(json, "structure"));
			Identifier thumbnail = json.has("thumbnail") ? Identifier.tryParse(JsonHelper.getString(json, "thumbnail")) :
										   LevelManager.PLACEHOLDER_THUMB;
			BlockPos spawnOffset = new BlockPos(0, 0, 0);
			if(json.has("spawn-offset"))
			{
				JsonObject pos = json.getAsJsonObject("spawn-offset");
				spawnOffset = spawnOffset.add(
						JsonHelper.getInt(pos, "x", 0),
						JsonHelper.getInt(pos, "y", 0),
						JsonHelper.getInt(pos, "z", 0));
			}
			LevelData level = new LevelData(id, title, description, author, authorlink, structure, thumbnail, spawnOffset, builtin);
			if(json.has("par-time"))
				level.setParTime(JsonHelper.getString(json, "par-time"));
			if(json.has("music"))
			{
				JsonObject music = json.getAsJsonObject("music");
				Identifier calm = null, fight = null;
				if(music.has("calm"))
					calm = Identifier.tryParse(JsonHelper.getString(music, "calm"));
				if(music.has("fight"))
					fight = Identifier.tryParse(JsonHelper.getString(music, "fight"));
				level.setMusic(calm, fight);
			}
			if(json.has("unimplemented"))
				level.setUnimplemented(JsonHelper.getBoolean(json, "unimplemented"));
			if(json.has("hidden"))
				level.setHidden(JsonHelper.getBoolean(json, "hidden"));
			if(json.has("version"))
				level.setVersion(JsonHelper.getInt(json, "version"));
			if(json.has("spawn-rot"))
				level.setSpawnRot(JsonHelper.getFloat(json, "spawn-rot"));
			if(builtin)
				builtinBuilder.put(id, level);
			else
				customBuilder.put(id, level);
		});
		setLevels(builtinBuilder.build(), true);
		setLevels(customBuilder.build(), false);
		Ultracraft.LOGGER.info("Loaded " + levels.size() + " Builtin Levels and " + customLevels.size() + " Custom Levels");
	}
	
	public LevelInstance getInstance(String id)
	{
		Identifier level = levelIdForInstanceId.get(id);
		if(level == null)
			return null;
		Map<String, LevelInstance> instanceMap = instances.get(level);
		if(level == null)
			return null;
		return instanceMap.get(id);
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
	
	public static void sync(ServerPlayerEntity player)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCollection(levels.entrySet(), LevelData::serialize);
		buf.writeCollection(customLevels.entrySet(), LevelData::serialize);
		ServerPlayNetworking.send(player, PacketRegistry.SEND_LEVELS_PACKET_ID, buf);
	}
	
	public static void setLevels(ImmutableMap<Identifier, LevelData> map, boolean builtin)
	{
		if(builtin)
			levels = map;
		else
			customLevels = map;
	}
	
	public static Map<Identifier, LevelData> getAllCustomLevels()
	{
		return customLevels;
	}
	
	public static BlockPos getSpawnPos(String instanceId)
	{
		Identifier levelId = levelIdForInstanceId.getOrDefault(instanceId, new Identifier(""));
		LevelData data = getLevelData(levelId);
		if(data == null)
		{
			Ultracraft.LOGGER.warn("Getting spawnpoint for level '" + instanceId + "' failed; Level wasn't found!");
			return null;
		}
		if(!instances.containsKey(levelId))
		{
			Ultracraft.LOGGER.warn("Getting spawnpoint for level '" + instanceId + "' failed; Level wasn't instantiated!");
			return null;
		}
		return instances.get(levelId).get(instanceId).pos.add(data.getSpawnOffset());
	}
	
	public static LevelData getLevelData(Identifier id)
	{
		if(levels.containsKey(id))
			return levels.get(id);
		else if(customLevels.containsKey(id))
			return customLevels.get(id);
		else
		{
			Ultracraft.LOGGER.warn("Couldn't find Level '" + id + "' in loaded lists!");
			return ERR_DATA;
		}
	}
	
	public Pair<String, LevelInstance> instantiateLevel(Identifier levelId)
	{
		return instantiateLevel(levelId, nextLevelPos);
	}
	
	public Pair<String, LevelInstance> instantiateLevel(Identifier levelId, BlockPos pos)
	{
		if(!levels.containsKey(levelId) && !customLevels.containsKey(levelId))
		{
			Ultracraft.LOGGER.warn("Level Structure " + levelId + " instantiation failed; level data not found!");
			return null;
		}
		LevelData data = getLevelData(levelId);
		Identifier structure = data.getStructure();
		if(structure == null)
		{
			Ultracraft.LOGGER.info("Level Structure " + levelId + " instantiation failed; structure is null");
			return null;
		}
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Ultracraft.LOGGER.info("Placing Level Structure " + structure + " at " + nextLevelPos);
		HashMap<String, LevelInstance> pool = instances.computeIfAbsent(levelId, k -> new HashMap<>());
		AtomicReference<Pair<String, LevelInstance>> inst = new AtomicReference<>();
		templateManager.getTemplate(structure)
				.ifPresent(i -> {
					i.place(world, pos, new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
					BlockBox box = i.calculateBoundingBox(pos, BlockRotation.NONE, new BlockPos(0, 0, 0), BlockMirror.NONE);
					Iterable<BlockPos> blocks = BlockPos.iterate(pos, pos.add(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ()));
					blocks.forEach(block -> {
						if(world.getBlockEntity(block) instanceof AbstractMappingBlockEntity mapBlock)
							mapBlock.reset();
					});
					List<Entity> list = world.getOtherEntities(null, new Box(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ()));
					list.forEach(e -> {
						if(!(e instanceof PlayerEntity))
							e.remove(Entity.RemovalReason.DISCARDED);
					});
					if(pos.equals(nextLevelPos))
						nextLevelPos = pos.add(box.getBlockCountX() + 128, 0, 0);
					LevelInstance newInstance = new LevelInstance(pos);
					levelBounds.put(levelId, box);
					String newId = levelId.toString() + "_" + pool.size();
					levelIdForInstanceId.put(newId, levelId);
					inst.set(new Pair<>(newId, newInstance));
					pool.put(newId, newInstance);
				});
		if(inst.get() == null)
			Ultracraft.LOGGER.warn("Level Structure " + structure + " placement failed; won't teleport Player.");
		else
			Ultracraft.LOGGER.info("Level Structure " + structure + " placed successfully.");
		return inst.get();
	}
	
	public void destroyAllLevels()
	{
		for (HashMap<String, LevelInstance> instances : instances.values())
			for (String id : new ArrayList<>(instances.keySet()))
				destroyInstance(id);
		instances.clear();
		levelBounds.clear();
		levelIdForInstanceId.clear();
	}
	
	public boolean destroyIfEmpty(String id)
	{
		if(!isLevelEmpty(null, id))
			return false;
		removeInstance(id);
		return true;
	}
	
	public boolean destroyInstance(String id)
	{
		return destroyInstance(id, false);
	}
	
	public boolean destroyInstance(String instanceId, boolean reload)
	{
		Identifier levelId = levelIdForInstanceId.get(instanceId);
		if(levelId == null)
			return true; //no levels of this type instantiated at all, no destruction necessary
		instances.get(levelId);
		if(levelId == null)
			return true; //level not instantiated, no destruction necessary
		LevelInstance instance = instances.get(levelId).get(instanceId);
		if(instance == null)
		{
			Ultracraft.LOGGER.warn("Level Structure " + instanceId + " destruction failed; level instance not found!");
			return false;
		}
		if(!levelBounds.containsKey(levelId))
		{
			Ultracraft.LOGGER.warn("Level Structure " + instanceId + " destruction failed; level bounds not found!");
			return false;
		}
		if(!reload)
		{
			List<ServerPlayerEntity> needsRescue = new ArrayList<>(instance.players); //prevent concurrent modification
			needsRescue.forEach(p -> rescue(p, RescueReason.INSTANCE_DESTROYED));
		}
		Ultracraft.LOGGER.info("Destroying Level Instance " + instanceId);
		BlockPos pos = instance.pos;
		BlockBox box = levelBounds.get(levelId);
		Iterable<BlockPos> blocks = BlockPos.iterate(pos, pos.add(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ()));
		blocks.forEach(block -> {
			world.setBlockState(block, Blocks.AIR.getDefaultState());
			world.removeBlockEntity(block);
		});
		List<Entity> list = world.getOtherEntities(null, new Box(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ()));
		list.forEach(e -> {
			if(!(e instanceof PlayerEntity))
				e.remove(Entity.RemovalReason.DISCARDED);
		});
		instances.get(levelId).remove(instanceId);
		Ultracraft.LOGGER.info("Finished Destroying Level Instance " + instanceId);
		return true;
	}
	
	public void rescueIfNecessary(ServerPlayerEntity player)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		if(!player.isCreative() && (winged.getCurrentLevel() == null || !instances.containsKey(winged.getCurrentLevel())))
			rescue(player, RescueReason.INSTANCE_NULL);
	}
	
	void rescue(ServerPlayerEntity player, RescueReason reason)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		winged.sendBoxTitle(Text.translatable(reason.message));
		ServerWorld overworld = world.getServer().getOverworld();
		FabricDimensions.teleport(player, overworld, new TeleportTarget(overworld.getSpawnPos().toCenterPos(), Vec3d.ZERO, player.getYaw(), player.getPitch()));
		winged.enterLevel(null, null);
	}
	
	public LevelInstance reloadInstance(String id)
	{
		Ultracraft.LOGGER.info("Re-Placing Level Instance " + id);
		LevelInstance old = getInstance(id);
		if(old == null)
		{
			Ultracraft.LOGGER.info("Tried reloading a level instance that didn't exist: (" + id + ")");
			return null;
		}
		Pair<String, LevelInstance> newInstance = instantiateLevel(levelIdForInstanceId.get(id));
		old.players.forEach(p -> onJoinLevel(p, newInstance.getLeft()));
		destroyInstance(id);
		return newInstance.getRight();
	}
	
	public void onJoinLevel(ServerPlayerEntity player, String id)
	{
		LevelInstance instance = getInstance(id);
		if(instance == null)
			rescue(player, RescueReason.INSTANCE_NULL);
		instance.players.add(player);
		UltraComponents.WINGED.get(player).enterLevel(levelIdForInstanceId.get(id), id);
	}
	
	public void onLeaveLevel(ServerPlayerEntity player, String id)
	{
		LevelInstance instance = getInstance(id);
		if(instance == null)
		{
			rescue(player, RescueReason.INSTANCE_NULL);
			return;
		}
		instance.players.remove(player);
		if(instance.players.isEmpty())
			removeInstance(id);
	}
	
	public Pair<String, LevelInstance> ReloadIfEmpty(PlayerEntity except, String id)
	{
		Identifier level = levelIdForInstanceId.get(id);
		if(level != null && instances.containsKey(level))
			if(isLevelEmpty(except, id))
				return new Pair<>(id, reloadInstance(id));
		return instantiateLevel(level);
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
	
	public static boolean isCustomLevelsPresent()
	{
		return customLevels.size() > 0;
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
	public ActionResult onBlockInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit)
	{
		return ActionResult.PASS;
	}
	
	@Override
	public ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction)
	{
		return player.isCreative() ? ActionResult.PASS : ActionResult.FAIL;
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
	
	public static class LevelInstance
	{
		public final List<ServerPlayerEntity> players = new ArrayList<>();
		public final BlockPos pos;
		public ServerPlayerEntity owner;
		
		public LevelInstance(BlockPos pos)
		{
			this.pos = pos;
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
