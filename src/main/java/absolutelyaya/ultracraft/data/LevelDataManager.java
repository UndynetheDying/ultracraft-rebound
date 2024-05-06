package absolutelyaya.ultracraft.data;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public class LevelDataManager extends JsonDataLoader
{
	public static final LevelData ERR_DATA = new LevelData(new Identifier(Ultracraft.MOD_ID, "placeholder"),
			"level.ultracraft.error.title", "level.ultracraft.error.description", "", "", null,
			new Identifier(Ultracraft.MOD_ID, "textures/level/err.png"), BlockPos.ORIGIN, true);
	public static final Identifier PLACEHOLDER_THUMB = new Identifier(Ultracraft.MOD_ID, "textures/level/placeholder.png");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static LevelDataManager Instance;
	public static Map<Identifier, LevelData> levels = new HashMap<>();
	public static Map<Identifier, LevelData> customLevels = new HashMap<>();
	
	public LevelDataManager()
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
	
	public static boolean isLevelExists(Identifier levelId)
	{
		return levels.containsKey(levelId) || customLevels.containsKey(levelId);
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler)
	{
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
										   LevelDataManager.PLACEHOLDER_THUMB;
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
			if(json.has("ranking"))
			{
				JsonObject ranking = json.getAsJsonObject("ranking");
				if(ranking.has("time"))
				{
					JsonArray array = JsonHelper.getArray(ranking, "time");
					String[] ranks = new String[array.size()];
					for (int i = 0; i < array.size(); i++)
						ranks[i] = array.get(i).getAsString();
					level.setTimeRanks(ranks);
				}
				if(ranking.has("kills"))
				{
					JsonArray array = JsonHelper.getArray(ranking, "kills");
					int[] ranks = new int[array.size()];
					for (int i = 0; i < array.size(); i++)
						ranks[i] = array.get(i).getAsInt();
					level.setKillRanks(ranks);
				}
				if(ranking.has("style"))
				{
					JsonArray array = JsonHelper.getArray(ranking, "style");
					int[] ranks = new int[array.size()];
					for (int i = 0; i < array.size(); i++)
						ranks[i] = array.get(i).getAsInt();
					level.setStyleRanks(ranks);
				}
			}
			if(json.has("music"))
			{
				JsonObject music = json.getAsJsonObject("music");
				for (Map.Entry<String, JsonElement> entry : music.entrySet())
				{
					String trackAuthor = null, trackName = "untitled";
					Identifier calm = null, combat = null;
					int col = 0xff0000, combatThreshold = 0;
					boolean noCalmdown = false;
					JsonObject elementt = entry.getValue().getAsJsonObject();
					if(elementt.has("author"))
						trackAuthor = JsonHelper.getString(elementt, "author");
					if(elementt.has("title"))
						trackName = JsonHelper.getString(elementt, "title");
					if(elementt.has("color"))
						col = Integer.valueOf(JsonHelper.getString(elementt, "color"), 16);
					if(elementt.has("calm"))
						calm = Identifier.tryParse(JsonHelper.getString(elementt, "calm"));
					if(elementt.has("combat"))
						combat = Identifier.tryParse(JsonHelper.getString(elementt, "combat"));
					if(elementt.has("combat-threshold"))
						combatThreshold = JsonHelper.getInt(elementt, "combat-threshold");
					if(elementt.has("no-calmdown"))
						noCalmdown = JsonHelper.getBoolean(elementt, "no-calmdown");
					level.putMusic(entry.getKey(), trackAuthor, trackName, col, calm, combat, combatThreshold, noCalmdown);
				}
			}
			if(json.has("unimplemented"))
				level.setUnimplemented(JsonHelper.getBoolean(json, "unimplemented"));
			if(json.has("hidden"))
				level.setHidden(JsonHelper.getBoolean(json, "hidden"));
			if(json.has("version"))
				level.setVersion(JsonHelper.getInt(json, "version"));
			if(json.has("spawn-rot"))
				level.setSpawnRot(JsonHelper.getFloat(json, "spawn-rot"));
			if(json.has("next-level"))
				level.setNextLevel(Identifier.tryParse(JsonHelper.getString(json, "next-level")));
			if(builtin)
				builtinBuilder.put(id, level);
			else
				customBuilder.put(id, level);
		});
		setLevels(builtinBuilder.build(), true);
		setLevels(customBuilder.build(), false);
		Ultracraft.LOGGER.info("Loaded " + levels.size() + " Builtin Levels and " + customLevels.size() + " Custom Levels");
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
	
	public static Identifier getNextLevel(Identifier id)
	{
		LevelData data = getLevelData(id);
		if(data.equals(ERR_DATA) || data.isUnimplemented())
			return null;
		return data.getNextLevel();
	}
	
	public static void setLevels(ImmutableMap<Identifier, LevelData> map, boolean builtin)
	{
		if(builtin)
			levels = map;
		else
			customLevels = map;
	}
	
	public static Map<Identifier, LevelData> getAllLevels()
	{
		return levels;
	}
	
	public static Map<Identifier, LevelData> getAllCustomLevels()
	{
		return customLevels;
	}
	
	public static boolean isCustomLevelsPresent()
	{
		return customLevels.size() > 0;
	}
	
	public static void sync(ServerPlayerEntity player)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCollection(levels.entrySet(), LevelData::serialize);
		buf.writeCollection(customLevels.entrySet(), LevelData::serialize);
		ServerPlayNetworking.send(player, PacketRegistry.SEND_LEVELS_PACKET_ID, buf);
	}
}
