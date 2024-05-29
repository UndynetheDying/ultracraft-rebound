package absolutelyaya.ultracraft.client.sound;

import absolutelyaya.ultracraft.Ultracraft;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicMetadataManager implements SimpleSynchronousResourceReloadListener
{
	public static MusicMetadataManager INSTANCE;
	private static final String MUSIC_JSON = "music.json";
	private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
											 .registerTypeAdapter(MusicMetadata.class, new MusicMetadataDeserializer()).create();
	private static final TypeToken<Map<String, MusicMetadata>> TYPE = new TypeToken<>(){};
	static final Logger LOGGER = LogUtils.getLogger();
	private final Map<Identifier, MusicMetadata> musicData = Maps.newHashMap();
	
	public MusicMetadataManager()
	{
		INSTANCE = this;
	}
	
	public MusicMetadata getMusicMeta(Identifier id)
	{
		return musicData.get(id);
	}
	
	public boolean shouldShowPopup(Identifier lastTrack, Identifier nextTrack)
	{
		if(!musicData.containsKey(nextTrack) || nextTrack.equals(lastTrack))
			return false;
		MusicMetadata lastData = getMusicMeta(lastTrack), nextData = getMusicMeta(nextTrack);
		return lastData == null || lastData.group() == null || !nextData.group().equals(lastData.group());
	}
	
	@Override
	public Identifier getFabricId()
	{
		return new Identifier(Ultracraft.MOD_ID, "music_metadata");
	}
	
	@Override
	public void reload(ResourceManager manager)
	{
		Map<Identifier, MusicMetadata> musicMetaList = new HashMap<>();
		for (String string : manager.getAllNamespaces()) {
			try
			{
				List<Resource> list = manager.getAllResources(new Identifier(string, MUSIC_JSON));
				for (Resource resource : list)
				{
					try (BufferedReader reader = resource.getReader())
					{
						Map<String, MusicMetadata> map = JsonHelper.deserialize(GSON, reader, TYPE);
						for (Map.Entry<String, MusicMetadata> entry : map.entrySet())
							musicMetaList.put(Identifier.tryParse(entry.getKey()), entry.getValue());
					}
					catch (RuntimeException runtimeException)
					{
						LOGGER.warn("Invalid {} in resourcepack: '{}'", MUSIC_JSON, resource.getResourcePackName(), runtimeException);
					}
				}
			}
			catch (IOException ignored)
			{
			
			}
		}
		musicData.clear();
		musicData.putAll(musicMetaList);
	}
}
