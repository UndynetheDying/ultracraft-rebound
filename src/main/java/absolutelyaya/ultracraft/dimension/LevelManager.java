package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
	static final Map<Identifier, BlockPos> instantiated = new HashMap<>();
	static BlockPos nextLevelPos = new BlockPos(0, 0, 0);
	
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
	
	public static BlockPos getSpawnPos(Identifier level)
	{
		LevelData data = getLevelData(level);
		if(data == null)
		{
			Ultracraft.LOGGER.warn("Getting spawnpoint for level '" + level + "' failed; Level wasn't found!");
			return null;
		}
		if(!instantiated.containsKey(level))
		{
			Ultracraft.LOGGER.warn("Getting spawnpoint for level '" + level + "' failed; Level wasn't instantiated!");
			return null;
		}
		return instantiated.get(level).add(data.getSpawnOffset());
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
	
	public boolean instantiateLevel(Identifier id)
	{
		return instantiateLevel(id, nextLevelPos);
	}
	
	public boolean instantiateLevel(Identifier id, BlockPos pos)
	{
		if(!levels.containsKey(id) && !customLevels.containsKey(id))
		{
			Ultracraft.LOGGER.warn("Level Structure " + id + " instantiation failed; level data not found!");
			return false;
		}
		if(instantiated.containsKey(id))
		{
			Ultracraft.LOGGER.info("Level Structure " + id + " already exists; no instantiation necessary");
			return true;
		}
		LevelData data = getLevelData(id);
		Identifier structure = data.getStructure();
		if(structure == null)
		{
			Ultracraft.LOGGER.info("Level Structure " + id + " instantiation failed; structure is null");
			return true;
		}
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Ultracraft.LOGGER.info("Placing Level Structure " + structure + " at " + nextLevelPos);
		AtomicBoolean success = new AtomicBoolean(false);
		templateManager.getTemplate(structure)
				.ifPresent(i -> {
					i.place(world, pos, new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
					success.set(true);
					BlockBox box = i.calculateBoundingBox(pos, BlockRotation.NONE, new BlockPos(0, 0, 0), BlockMirror.NONE);
					Iterable<BlockPos> blocks = BlockPos.iterate(pos, pos.add(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ()));
					blocks.forEach(block -> {
						if(world.getBlockEntity(block) instanceof RoomBlockEntity room)
							room.reset();
					});
					if(pos.equals(nextLevelPos))
						nextLevelPos = pos.add(box.getBlockCountX() + 128, 0, 0);
				});
		instantiated.put(id, pos);
		if(!success.get())
			Ultracraft.LOGGER.warn("Level Structure " + structure + " placement failed; won't teleport Player.");
		else
			Ultracraft.LOGGER.info("Level Structure " + structure + " placed successfully.");
		return success.get();
	}
	
	public void destroyAllLevels()
	{
		for (Identifier id : instantiated.keySet().toArray(new Identifier[0]))
			destroyLevel(id);
	}
	
	public boolean destroyIfEmpty(Identifier id)
	{
		if(!isLevelEmpty(null, id))
			return false;
		destroyLevel(id);
		return true;
	}
	
	public boolean destroyLevel(Identifier id)
	{
		return destroyLevel(id, false);
	}
	
	public boolean destroyLevel(Identifier id, boolean reload)
	{
		if(!instantiated.containsKey(id))
			return true; //level not instantiated, no destruction necessary
		if(!reload)
		{
			List<ServerPlayerEntity> needsRescue = new ArrayList<>();
			world.getPlayers().forEach(player -> {
				IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
				if(id.equals(winged.getCurrentLevel()))
					needsRescue.add(player);
			});
			needsRescue.forEach(this::rescue);
		}
		BlockPos pos = instantiated.get(id);
		LevelData level = getLevelData(id);
		if(level == null)
		{
			Ultracraft.LOGGER.warn("Level Structure " + id + " destruction failed; level data not found!");
			return false;
		}
		Identifier structure = level.getStructure();
		if(structure == null)
		{
			Ultracraft.LOGGER.info("Level Structure " + id + " destruction failed; structure is null");
			return true;
		}
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Ultracraft.LOGGER.info("Destroying Level Structure " + structure);
		templateManager.getTemplate(structure)
				.ifPresent(i -> {
					BlockBox box = i.calculateBoundingBox(pos, BlockRotation.NONE, new BlockPos(0, 0, 0), BlockMirror.NONE);
					Iterable<BlockPos> blocks = BlockPos.iterate(pos, pos.add(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ()));
					blocks.forEach(block -> world.setBlockState(block, Blocks.AIR.getDefaultState()));
					List<Entity> list = world.getOtherEntities(null, new Box(box.getMinX(), box.getMinY(), box.getMinZ(), box.getMaxX(), box.getMaxY(), box.getMaxZ()));
					list.forEach(e -> {
						if(!(e instanceof PlayerEntity))
							e.remove(Entity.RemovalReason.DISCARDED);
					});
				});
		instantiated.remove(id);
		Ultracraft.LOGGER.info("Finished Destroying Level Structure " + structure);
		return true;
	}
	
	public void rescueIfNecessary(ServerPlayerEntity player)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		if(!player.isCreative() && (winged.getCurrentLevel() == null || !instantiated.containsKey(winged.getCurrentLevel())))
			rescue(player);
	}
	
	void rescue(ServerPlayerEntity player)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		winged.sendBoxTitle(Text.of("message.ultracraft.level-destroyed"));
		ServerWorld overworld = world.getServer().getOverworld();
		FabricDimensions.teleport(player, overworld, new TeleportTarget(overworld.getSpawnPos().toCenterPos(), Vec3d.ZERO, player.getYaw(), player.getPitch()));
		winged.setCurrentLevel(null);
	}
	
	public boolean reloadLevel(Identifier id)
	{
		Ultracraft.LOGGER.info("Re-Placing Level Structure " + id);
		if(!instantiated.containsKey(id))
			return instantiateLevel(id);
		BlockPos pos = instantiated.get(id);
		if(!destroyLevel(id, true))
			return false;
		return instantiateLevel(id, pos);
	}
	
	public boolean instantiateLevelOrReloadIfEmpty(PlayerEntity except, Identifier level)
	{
		if(instantiated.containsKey(level))
			if(isLevelEmpty(except, level))
				return reloadLevel(level);
		return instantiateLevel(level);
	}
	
	boolean isLevelEmpty(@Nullable PlayerEntity except, Identifier id)
	{
		boolean empty = true;
		for (PlayerEntity player : world.getPlayers())
		{
			if(player.equals(except))
				continue;
			IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
			if(id.equals(winged.getCurrentLevel()))
			{
				empty = false;
				break;
			}
		}
		return empty;
	}
	
	public static boolean isCustomLevelsPresent()
	{
		return customLevels.size() > 0;
	}
	
	@Override
	public void tick()
	{
	
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
}
