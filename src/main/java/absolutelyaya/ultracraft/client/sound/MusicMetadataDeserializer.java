package absolutelyaya.ultracraft.client.sound;

import com.google.gson.*;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;

public class MusicMetadataDeserializer implements JsonDeserializer<MusicMetadata>
{
	
	@Override
	public MusicMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
	{
		String group = null;
		Text author = null, title = Text.of("untitled");
		int color = 0xff0000;
		JsonObject object = jsonElement.getAsJsonObject();
		if(JsonHelper.hasString(object, "group"))
			group = JsonHelper.getString(object, "group");
		if(JsonHelper.hasString(object, "author"))
			author = Text.translatable(JsonHelper.getString(object, "author"));
		if(JsonHelper.hasString(object, "title"))
			title = Text.translatable(JsonHelper.getString(object, "title"));
		if(JsonHelper.hasString(object, "color"))
			color = Integer.valueOf(JsonHelper.getString(object, "color"), 16);
		return new MusicMetadata(group, author, title, color);
	}
}
