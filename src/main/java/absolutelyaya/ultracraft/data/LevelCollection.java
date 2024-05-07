package absolutelyaya.ultracraft.data;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class LevelCollection
{
	final List<Identifier> levels = new ArrayList<>(), extraDestinations = new ArrayList<>();
	final Identifier id;
	final String title, description, author;
	final boolean builtin;
	
	public LevelCollection(Identifier id, String title, String description, String author, boolean builtin)
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.author = author;
		this.builtin = builtin;
	}
	
	public Identifier getID()
	{
		return id;
	}
	
	public Text getTitleText()
	{
		return Text.translatable(title);
	}
	
	public Text getDescriptionText()
	{
		return Text.translatable(description);
	}
	
	public Text getAuthorText()
	{
		return Text.translatable(author);
	}
	
	public boolean isBuiltin()
	{
		return builtin;
	}
	
	public void addLevel(Identifier level)
	{
		if(!LevelDataManager.isLevelExists(level))
		{
			Ultracraft.LOGGER.warn("Tried to register a Level that doesn't exist to a Level Collection ({} -> {}", level, id);
			return;
		}
		levels.add(level);
	}
	
	public void addExtraDestination(Identifier destination)
	{
		extraDestinations.add(destination);
	}
	
	public List<Identifier> getAllLevels()
	{
		return new ArrayList<>(levels);
	}
	
	public List<Identifier> getAllDestinations()
	{
		List<Identifier> ids = new ArrayList<>(levels);
		ids.addAll(extraDestinations);
		return ids;
	}
	
	public NbtCompound asNbt()
	{
		NbtCompound nbt = new NbtCompound();
		nbt.putString("id", id.toString());
		nbt.putString("title", title);
		nbt.putString("description", description);
		nbt.putString("author", author);
		nbt.putBoolean("builtin", builtin);
		NbtList levels = new NbtList();
		for (Identifier i : this.levels)
			levels.add(NbtString.of(i.toString()));
		nbt.put("levels", levels);
		NbtList extraDestinations = new NbtList();
		for (Identifier i : this.extraDestinations)
			levels.add(NbtString.of(i.toString()));
		nbt.put("extraDestinations", extraDestinations);
		return nbt;
	}
	
	public static LevelCollection fromNbt(NbtCompound nbt)
	{
		Identifier id = Identifier.tryParse(nbt.getString("id"));
		String title = nbt.getString("title");
		String description = nbt.getString("description");
		String author = nbt.getString("author");
		boolean builtin = nbt.getBoolean("builtin");
		LevelCollection output = new LevelCollection(id, title, description, author, builtin);
		for(NbtElement element : nbt.getList("levels", NbtElement.STRING_TYPE))
			output.addLevel(Identifier.tryParse(element.asString()));;
		for(NbtElement element : nbt.getList("extraDestinations", NbtElement.STRING_TYPE))
			output.addLevel(Identifier.tryParse(element.asString()));
		return output;
	}
	
	public static void serialize(PacketByteBuf buf, Map.Entry<Identifier, LevelCollection> pair)
	{
		buf.writeNbt(pair.getValue().asNbt());
	}
	
	public static LevelCollection deserialize(PacketByteBuf buf)
	{
		return fromNbt(buf.readNbt());
	}
}
