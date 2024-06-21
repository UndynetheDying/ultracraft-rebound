package absolutelyaya.ultracraft.data;

import absolutelyaya.ultracraft.Ultracraft;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public  class LevelCollectionManager extends JsonDataLoader
{
	public static LevelCollectionManager Instance;
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static Map<Identifier, LevelCollection> layers = new HashMap<>(), customLayers = new HashMap<>();
	
	public LevelCollectionManager()
	{
		super(GSON, "ultracraft/layer");
		Instance = this;
		
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener()
		{
			@Override
			public Identifier getFabricId()
			{
				return Ultracraft.identifier("ultracraft/layer");
			}
			
			@Override
			public void reload(ResourceManager manager)
			{
				apply(prepare(manager, null), manager, null);
			}
		});
	}
	
	@Override
	protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler)
	{
		ImmutableMap.Builder<Identifier, LevelCollection> builtinBuilder = ImmutableMap.builder(), customBuilder = ImmutableMap.builder();
		prepared.forEach((id, element) -> {
			JsonObject json = element.getAsJsonObject();
			String title = JsonHelper.getString(json, "title", "layer.unnamed");
			String description = JsonHelper.getString(json, "description", "");
			String author = JsonHelper.getString(json, "author", "");
			boolean builtin = JsonHelper.getBoolean(json, "builtin", false);
			LevelCollection output = new LevelCollection(id, title, description, author, builtin);
			if(json.has("levels"))
				for(JsonElement i : JsonHelper.getArray(json, "levels"))
					output.addLevel(Identifier.tryParse(i.getAsString()));
			if(json.has("extra-destinations"))
				for(JsonElement i : JsonHelper.getArray(json, "extra-destinations"))
					output.addExtraDestination(Identifier.tryParse(i.getAsString()));
			if(builtin)
				builtinBuilder.put(id, output);
			else
				customBuilder.put(id, output);
		});
		layers = builtinBuilder.build();
		customLayers = customBuilder.build();
		Ultracraft.LOGGER.info("Loaded {} Builtin Layers and {} Custom Layers", layers.size(), customLayers.size());
	}
	
	public static void setLayers(ImmutableMap<Identifier, LevelCollection> map, boolean builtin)
	{
		if(builtin)
			layers = map;
		else
			customLayers = map;
	}
	
	public static LevelCollection getLevelCollection(Identifier id)
	{
		if(layers.containsKey(id))
			return layers.get(id);
		else if(customLayers.containsKey(id))
			return customLayers.get(id);
		else
		{
			Ultracraft.LOGGER.warn("Couldn't find Layer '{}' in loaded lists!", id);
			return null;
		}
	}
	
	public static Map<Identifier, LevelCollection> getAllLayers()
	{
		return layers;
	}
	
	public static Map<Identifier, LevelCollection> getAllCustomLayers()
	{
		return customLayers;
	}
	
	public static boolean isCustomLayersPresent()
	{
		return !customLayers.isEmpty();
	}
}
