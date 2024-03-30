package absolutelyaya.ultracraft.data;

import absolutelyaya.ultracraft.Ultracraft;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class StyleBonusManager extends JsonDataLoader
{
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static Map<Identifier, StyleBonus> bonuses = ImmutableMap.of();
	
	public StyleBonusManager()
	{
		super(GSON, "ultracraft/style");
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener()
		{
			@Override
			public Identifier getFabricId()
			{
				return new Identifier(Ultracraft.MOD_ID, "ultracraft/style");
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
		ImmutableMap.Builder<Identifier, StyleBonus> builder = ImmutableMap.builder();
		prepared.forEach((id, element) -> {
			JsonObject json = element.getAsJsonObject();
			StyleBonus bonus = new StyleBonus(id, JsonHelper.getString(json, "translation-key"), JsonHelper.getInt(json, "score"));
			if(json.has("entity"))
				bonus.setEntityType(Registries.ENTITY_TYPE.get(Identifier.tryParse(JsonHelper.getString(json, "entity"))));
			if(json.has("damage"))
				bonus.setDamageType(JsonHelper.getString(json, "damage"));
			if(json.has("use-staleness"))
				bonus.setUseStaleness(JsonHelper.getBoolean(json, "use-staleness"));
			if(json.has("impossible") && JsonHelper.getBoolean(json, "impossible"))
				bonus.setImpossible();
			builder.put(id, bonus);
		});
		bonuses = builder.build();
		Ultracraft.LOGGER.info("Loaded " + bonuses.size() + " Style Bonuses");
	}
	
	public static Map<Identifier, StyleBonus> getBonuses()
	{
		return bonuses;
	}
}
