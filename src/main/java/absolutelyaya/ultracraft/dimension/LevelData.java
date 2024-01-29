package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.client.sound.ModularLevelMusic;
import absolutelyaya.ultracraft.util.TimeUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public final class LevelData
{
	final Identifier id;
	final String title, description, author, authorLink;
	final Identifier thumbnail, structure;
	final BlockPos spawnOffset;
	final boolean builtin;
	String parTimeString;
	long parTime;
	ModularLevelMusic music;
	boolean unimplemented, hidden;
	float spawnRot;
	int version;
	
	public LevelData(Identifier id, String title, String description, String author, String authorLink, Identifier structure, Identifier thumbnail, BlockPos spawnOffset, boolean builtin)
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.author = author;
		this.authorLink = authorLink;
		this.structure = structure;
		this.thumbnail = thumbnail;
		this.spawnOffset = spawnOffset;
		this.builtin = builtin;
	}
	
	public Identifier getID()
	{
		return id;
	}
	
	public Text getTitle()
	{
		return Text.translatable(title);
	}
	
	public Text getDescription()
	{
		return Text.translatable(description);
	}
	
	public Text getAuthor()
	{
		return Text.translatable(author);
	}
	
	public String getAuthorLink()
	{
		return authorLink;
	}
	
	public boolean hasParTime()
	{
		return parTime > 0 && parTimeString.length() > 0;
	}
	
	public long getParTime()
	{
		return parTime;
	}
	
	public String getParTimeString()
	{
		return parTimeString;
	}
	
	public Identifier getThumbnail()
	{
		return thumbnail;
	}
	
	public Identifier getStructure()
	{
		return structure;
	}
	
	public BlockPos getSpawnOffset()
	{
		return spawnOffset;
	}
	
	public boolean hasMusic()
	{
		return music != null;
	}
	
	public void setMusic(Identifier calm, Identifier fight)
	{
		music = new ModularLevelMusic(calm, fight);
	}
	
	public ModularLevelMusic getMusic()
	{
		return music;
	}
	
	public void setParTime(String string)
	{
		parTime = TimeUtil.parseToMilli(string);
		if(parTime > -1)
			parTimeString = string;
	}
	
	public void setParTime(long time)
	{
		parTime = time;
		parTimeString = TimeUtil.milliToString(time);
	}
	
	public boolean getBuiltin()
	{
		return builtin;
	}
	
	public void setUnimplemented(boolean unimplemented)
	{
		this.unimplemented = unimplemented;
	}
	
	public boolean isUnimplemented()
	{
		return unimplemented;
	}
	
	public void setHidden(boolean hidden)
	{
		this.hidden = hidden;
	}
	
	public boolean isHidden()
	{
		return hidden;
	}
	
	public float getSpawnRot()
	{
		return spawnRot;
	}
	
	public void setSpawnRot(float spawnRot)
	{
		this.spawnRot = spawnRot;
	}
	
	public int getVersion()
	{
		return version;
	}
	
	public void setVersion(int version)
	{
		this.version = version;
	}
	
	public NbtCompound asNbt()
	{
		NbtCompound nbt = new NbtCompound();
		nbt.putString("id", id.toString());
		nbt.putString("title", title);
		nbt.putString("description", description);
		nbt.putString("author", author);
		nbt.putString("authorLink", authorLink);
		nbt.putString("structure", structure.toString());
		nbt.putString("thumbnail", thumbnail.toString());
		NbtCompound spawnOffset = new NbtCompound();
		spawnOffset.putInt("x", this.spawnOffset.getX());
		spawnOffset.putInt("y", this.spawnOffset.getY());
		spawnOffset.putInt("z", this.spawnOffset.getZ());
		nbt.put("spawnOffset", spawnOffset);
		nbt.putBoolean("builtin", builtin);
		nbt.putBoolean("unimplemented", unimplemented);
		nbt.putBoolean("hidden", hidden);
		if(hasParTime())
			nbt.putLong("parTime", parTime);
		if(hasMusic())
		{
			NbtCompound music = new NbtCompound();
			RegistryEntry<SoundEvent> calm = this.music.getFightSound();
			if(calm != null)
				music.putString("calm", calm.value().getId().toString());
			RegistryEntry<SoundEvent> fight = this.music.getFightSound();
			if(fight != null)
				music.putString("fight", fight.value().getId().toString());
			nbt.put("music", music);
		}
		nbt.putFloat("spawnRot", spawnRot);
		nbt.putInt("version", version);
		return nbt;
	}
	
	public static LevelData fromNbt(NbtCompound nbt)
	{
		String id = nbt.getString("id");
		String title = nbt.getString("title");
		String description = nbt.getString("description");
		String author = nbt.getString("author");
		String authorLink = nbt.getString("authorLink");
		String structure = nbt.getString("structure");
		String thumbnail = nbt.getString("thumbnail");
		NbtCompound spawnOffsetNbt = nbt.getCompound("spawnOffset");
		BlockPos spawnOffset = new BlockPos(spawnOffsetNbt.getInt("x"), spawnOffsetNbt.getInt("y"), spawnOffsetNbt.getInt("z"));
		boolean builtin = nbt.getBoolean("builtin");
		LevelData data = new LevelData(Identifier.tryParse(id), title, description, author, authorLink,
				Identifier.tryParse(structure), Identifier.tryParse(thumbnail), spawnOffset, builtin);
		if(nbt.contains("parTime", NbtElement.LONG_TYPE))
			data.setParTime(nbt.getLong("parTime"));
		if(nbt.contains("music", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound music = nbt.getCompound("music");
			Identifier calm = null, fight = null;
			if(music.contains("calm", NbtElement.STRING_TYPE))
				calm = Identifier.tryParse(music.getString("calm"));
			if(music.contains("fight", NbtElement.STRING_TYPE))
				fight = Identifier.tryParse(music.getString("fight"));
			data.setMusic(calm, fight);
		}
		data.setUnimplemented(nbt.getBoolean("unimplemented"));
		data.setHidden(nbt.getBoolean("hidden"));
		data.setSpawnRot(nbt.getFloat("spawnRot"));
		data.setVersion(nbt.getInt("version"));
		return data;
	}
	
	public static void serialize(PacketByteBuf buf, Map.Entry<Identifier, LevelData> pair)
	{
		buf.writeNbt(pair.getValue().asNbt());
	}
	
	public static LevelData deserialize(PacketByteBuf buf)
	{
		return fromNbt(buf.readNbt());
	}
	
	@Override
	public String toString()
	{
		return "LevelData[" +
					   "title=" + title + ", " +
					   "description=" + description + ", " +
					   "author=" + author + ", " +
					   "authorLink=" + authorLink + ", " +
					   "parTime=" + parTime + ", " +
					   "parTimeString=" + parTimeString + ", " +
					   "thumbnail=" + thumbnail + ", " +
					   "structure=" + structure + ", " +
					   "spawnOffset=" + spawnOffset + ']';
	}
}
