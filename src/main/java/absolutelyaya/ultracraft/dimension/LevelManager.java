package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class LevelManager extends JsonDataLoader implements DimensionManager
{
	private static final Identifier PLACEHOLDER_THUMBNAIL = new Identifier(Ultracraft.MOD_ID, "textures/level/placeholder.png");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static LevelManager Instance;
	public static final Identifier ID = new Identifier(Ultracraft.MOD_ID, "levels");
	public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, ID);
	ServerWorld world;
	public static final Map<Identifier, LevelData> levels = new HashMap<>();
	public static final Map<Identifier, LevelData> customLevels = new HashMap<>();
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
		levels.clear();
		customLevels.clear();
		prepared.forEach((id, element) -> {
			JsonObject json = element.getAsJsonObject();
			boolean builtin = JsonHelper.getBoolean(json, "builtin", false);
			if(!json.has("structure"))
			{
				Ultracraft.LOGGER.warn((builtin ? "Level '" : "Custom Level '") + id + "' does not have a structure parameter!");
				return;
			}
			Text author = Text.translatable(JsonHelper.getString(json, "author", "level.author.unknown"));
			String authorlink = JsonHelper.getString(json, "author-link", "");
			Text title = Text.translatable(JsonHelper.getString(json, "title", "level.unnamed"));
			Text description = Text.translatable(JsonHelper.getString(json, "description", ""));
			Identifier structure = Identifier.tryParse(JsonHelper.getString(json, "structure"));
			Identifier thumbnail = json.has("thumbnail") ? Identifier.tryParse(JsonHelper.getString(json, "thumbnail")) : PLACEHOLDER_THUMBNAIL;
			BlockPos spawnOffset = new BlockPos(0, 0, 0);
			if(json.has("spawn-offset"))
			{
				JsonObject pos = json.getAsJsonObject("spawn-offset");
				spawnOffset = spawnOffset.add(
						JsonHelper.getInt(pos, "x", 0),
						JsonHelper.getInt(pos, "y", 0),
						JsonHelper.getInt(pos, "z", 0));
			}
			long parTime = -1;
			String timeString = "";
			if(json.has("par-time"))
			{
				timeString = JsonHelper.getString(json, "par-time");
				try
				{
					LocalTime time = LocalTime.parse(timeString);
					parTime = time.get(ChronoField.MILLI_OF_DAY);
				}
				catch (DateTimeParseException e)
				{
					Ultracraft.LOGGER.warn("couldn't parse par-time!");
				}
			}
			System.out.println(parTime + " -> " + id + " par-time milliseconds");
			LevelData level = new LevelData(title, description, author, authorlink, parTime, timeString, thumbnail, structure, spawnOffset);
			if(builtin)
				levels.put(id, level);
			else
				customLevels.put(id, level);
		});
		Ultracraft.LOGGER.info("Loaded " + levels.size() + " Builtin Levels and " + customLevels.size() + " Custom Levels");
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
		return instantiated.get(level).add(data.spawnOffset());
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
			return null;
		}
	}
	
	public boolean instantiateLevel(Identifier id)
	{
		return instantiateLevel(id, nextLevelPos);
	}
	
	public boolean instantiateLevel(Identifier id, BlockPos pos)
	{
		if(!levels.containsKey(id))
		{
			Ultracraft.LOGGER.warn("Level Structure " + id + " instantiation failed; level data not found!");
			return false;
		}
		if(instantiated.containsKey(id))
		{
			Ultracraft.LOGGER.info("Level Structure " + id + " already exists; no instantiation necessary");
			return true;
		}
		LevelData data = levels.get(id);
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Identifier structure = data.structure();
		Ultracraft.LOGGER.info("Placing Level Structure " + structure);
		AtomicBoolean success = new AtomicBoolean(false);
		templateManager.getTemplate(structure)
				.ifPresent(i -> {
					i.place(world, pos, new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
					success.set(true);
					if(pos.equals(nextLevelPos))
					{
						BlockBox box = i.calculateBoundingBox(pos, BlockRotation.NONE, new BlockPos(0, 0, 0), BlockMirror.NONE);
						nextLevelPos = pos.add(box.getBlockCountX() + 128, 0, 0);
					}
				});
		instantiated.put(id, pos);
		if(!success.get())
			Ultracraft.LOGGER.warn("Level Structure " + structure + " placement failed; won't teleport Player.");
		return success.get();
	}
	
	public void destroyAllLevels()
	{
		instantiated.keySet().forEach(this::destroyLevel);
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
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Identifier structure = level.structure();
		Ultracraft.LOGGER.info("Destroying Level Structure " + structure);
		templateManager.getTemplate(structure)
				.ifPresent(i -> {
					BlockBox box = i.calculateBoundingBox(pos, BlockRotation.NONE, new BlockPos(0, 0, 0), BlockMirror.NONE);
					Iterable<BlockPos> blocks = BlockPos.iterate(pos, pos.add(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ()));
					blocks.forEach(block -> world.setBlockState(block, Blocks.AIR.getDefaultState()));
					world.getOtherEntities(null, new Box(pos, new BlockPos(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ())))
							.forEach(e -> e.remove(Entity.RemovalReason.DISCARDED));
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
