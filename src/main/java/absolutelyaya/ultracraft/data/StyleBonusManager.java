package absolutelyaya.ultracraft.data;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.style.StyleBonus;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class StyleBonusManager extends JsonDataLoader
{
	private static Map<Identifier, StyleBonus> bonuses = ImmutableMap.of();
	
	public StyleBonusManager(Gson gson)
	{
		super(gson, "ultracraft/style");
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
			StyleBonus bonus = new StyleBonus(JsonHelper.getString(json, "translation-key"), JsonHelper.getInt(json, "score"));
			if(json.has("entity"))
				bonus.setEntityType(Registries.ENTITY_TYPE.get(Identifier.tryParse(JsonHelper.getString(json, "entity"))));
			if(json.has("damage"))
			{
				String damageType = JsonHelper.getString(json, "damage");
				//TODO: figure out how the fuck to get damage types from a string with no world
			}
			builder.put(id, bonus);
		});
		bonuses = builder.build();
	}
	
	public static Map<Identifier, StyleBonus> getBonuses()
	{
		return bonuses;
	}
}
