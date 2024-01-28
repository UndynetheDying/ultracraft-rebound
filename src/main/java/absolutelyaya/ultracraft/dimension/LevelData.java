package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.sound.ModularLevelMusic;
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
	
	public Identifier id()
	{
		return id;
	}
	
	public Text title()
	{
		return Text.translatable(title);
	}
	
	public Text description()
	{
		return Text.translatable(description);
	}
	
	public Text author()
	{
		return Text.translatable(author);
	}
	
	public String authorLink()
	{
		return authorLink;
	}
	
	public boolean hasParTime()
	{
		return parTime > 0 && parTimeString.length() > 0;
	}
	
	public long parTime()
	{
		return parTime;
	}
	
	public String parTimeString()
	{
		return parTimeString;
	}
	
	public Identifier thumbnail()
	{
		return thumbnail;
	}
	
	public Identifier structure()
	{
		return structure;
	}
	
	public BlockPos spawnOffset()
	{
		return spawnOffset;
	}
	
	public boolean hasMusic()
	{
		return music != null;
	}
	
	public void music(Identifier calm, Identifier fight)
	{
		music = new ModularLevelMusic(calm, fight);
	}
	
	public ModularLevelMusic music()
	{
		return music;
	}
	
	public void parTime(String string)
	{
		try
		{
			String[] segments = string.split(":");
			long parTime = 0;
			for (int i = Math.min(segments.length - 1, 3); i >= 0; i--)
			{
				long ms = Long.parseLong(segments[i]);
				if(i > 2)
					ms *= 60; //hours to minutes
				if(i > 1)
					ms *= 60; //minutes to seconds
				if(i > 0)
					ms *= 1000; //seconds to millisecond
				parTime += ms;
			}
			this.parTime = parTime;
			parTimeString = string;
		}
		catch (NumberFormatException e)
		{
			parTimeString = "";
			parTime = -1;
			Ultracraft.LOGGER.warn("Couldn't parse par-time for '" + id + "'; Number Format Exception");
		}
	}
	
	public void parTime(long time)
	{
		long milli = time % 1000, sec = time / 1000, min = sec / 60, hour = min / 60;
		StringBuilder builder = new StringBuilder();
		if(hour > 0)
			builder.append(String.format("%d:", hour));
		if(min > 0)
			builder.append(String.format("%02d:", min));
		if(sec > 0)
			builder.append(String.format("%02d:", sec));
		if(milli > 0)
			builder.append(String.format("%04d:", milli));
		parTimeString = builder.toString();
		parTime = time;
	}
	
	public boolean builtin()
	{
		return builtin;
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
			data.parTime(nbt.getLong("parTime"));
		if(nbt.contains("music", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound music = nbt.getCompound("music");
			Identifier calm = null, fight = null;
			if(music.contains("calm", NbtElement.STRING_TYPE))
				calm = Identifier.tryParse(music.getString("calm"));
			if(music.contains("fight", NbtElement.STRING_TYPE))
				fight = Identifier.tryParse(music.getString("fight"));
			data.music(calm, fight);
		}
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
