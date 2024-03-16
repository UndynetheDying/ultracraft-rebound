package absolutelyaya.ultracraft.data;

import absolutelyaya.ultracraft.client.sound.ModularLevelMusic;
import absolutelyaya.ultracraft.util.TimeUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class LevelData
{
	final Identifier id;
	final String title, description, author, authorLink;
	final Identifier thumbnail, structure;
	final BlockPos spawnOffset;
	final boolean builtin;
	String[] timeStrings;
	long[] timeRanks;
	int[] killRanks, styleRanks;
	ModularLevelMusic music;
	boolean unimplemented, hidden;
	float spawnRot;
	int version;
	Identifier nextLevel;
	
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
	
	public Text getTitleText()
	{
		return Text.translatable(title);
	}
	
	public String getTitleKey()
	{
		return title;
	}
	
	public Text getDescriptionText()
	{
		return Text.translatable(description);
	}
	
	public Text getAuthorText()
	{
		return Text.translatable(author);
	}
	
	public String getAuthorLink()
	{
		return authorLink;
	}
	
	public boolean hasFullRankingData()
	{
		return hasTimeRanking() && hasKillRanking() && hasStyleRanking();
	}
	
	public boolean hasTimeRanking()
	{
		return timeRanks != null && timeRanks.length > 0;
	}
	
	public boolean hasKillRanking()
	{
		return killRanks != null && killRanks.length > 0;
	}
	
	public boolean hasStyleRanking()
	{
		return styleRanks != null && styleRanks.length > 0;
	}
	
	public long[] getTimeRanks()
	{
		return timeRanks;
	}
	
	public int[] getKillRanks()
	{
		return killRanks;
	}
	
	public int[] getStyleRanks()
	{
		return styleRanks;
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
	
	public void setTimeRanks(String[] strings)
	{
		List<Pair<String, Long>> ranks = new ArrayList<>();
		for (String s : strings)
		{
			long time = TimeUtil.parseToMilli(s);
			if(time > -1)
				ranks.add(new Pair<>(s, time));
		}
		timeRanks = new long[ranks.size()];
		timeStrings = new String[ranks.size()];
		for (int i = 0; i < ranks.size(); i++)
		{
			timeStrings[i] = ranks.get(i).getLeft();
			timeRanks[i] = ranks.get(i).getRight();
		}
	}
	
	public void setTimeRanks(long[] ranks)
	{
		timeRanks = ranks;
		timeStrings = new String[ranks.length];
		for (int i = 0; i < ranks.length; i++)
			timeStrings[i] = TimeUtil.milliToString(ranks[i]);
	}
	
	public void setKillRanks(int[] ranks)
	{
		killRanks = ranks;
	}
	
	public void setStyleRanks(int[] ranks)
	{
		styleRanks = ranks;
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
	
	public Identifier getNextLevel()
	{
		return nextLevel;
	}
	
	public void setNextLevel(Identifier nextLevel)
	{
		this.nextLevel = nextLevel;
	}
	
	public int getRankForTime(long time)
	{
		return getRankForRequirement(time, timeRanks);
	}
	
	public int getRankForKills(int kills)
	{
		return getRankForRequirement(kills, killRanks);
	}
	
	public int getRankForStyle(int style)
	{
		return getRankForRequirement(style, styleRanks);
	}
	
	int getRankForRequirement(long val, long[] list)
	{
		int rank = 0;
		for (long i : list)
		{
			if (i < val)
				rank++;
			else
				break;
		}
		return rank;
	}
	
	int getRankForRequirement(long val, int[] list)
	{
		int rank = 0;
		for (long i : list)
		{
			if (i > val)
				rank++;
			else
				break;
		}
		return rank;
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
		if(hasFullRankingData())
		{
			NbtCompound rankingData = new NbtCompound();
			rankingData.putLongArray("timeRanks", timeRanks);
			rankingData.putIntArray("killRanks", killRanks);
			rankingData.putIntArray("styleRanks", styleRanks);
			nbt.put("rankingData", rankingData);
		}
		if(hasMusic())
		{
			NbtCompound music = new NbtCompound();
			RegistryEntry<SoundEvent> calm = this.music.getCalmSound();
			if(calm != null && calm.value() != null)
				music.putString("calm", calm.value().getId().toString());
			RegistryEntry<SoundEvent> combat = this.music.getCombatSound();
			if(combat != null && combat.value() != null)
				music.putString("combat", combat.value().getId().toString());
			nbt.put("music", music);
		}
		nbt.putFloat("spawnRot", spawnRot);
		nbt.putInt("version", version);
		if(nextLevel != null)
			nbt.putString("nextLevel", nextLevel.toString());
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
		if(nbt.contains("rankingData", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound ranking = nbt.getCompound("rankingData");
			data.setTimeRanks(ranking.getLongArray("timeRanks"));
			data.setKillRanks(ranking.getIntArray("killRanks"));
			data.setStyleRanks(ranking.getIntArray("styleRanks"));
		}
		if(nbt.contains("music", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound music = nbt.getCompound("music");
			Identifier calm = null, combat = null;
			if(music.contains("calm", NbtElement.STRING_TYPE))
				calm = Identifier.tryParse(music.getString("calm"));
			if(music.contains("combat", NbtElement.STRING_TYPE))
				combat = Identifier.tryParse(music.getString("combat"));
			data.setMusic(calm, combat);
		}
		data.setUnimplemented(nbt.getBoolean("unimplemented"));
		data.setHidden(nbt.getBoolean("hidden"));
		data.setSpawnRot(nbt.getFloat("spawnRot"));
		data.setVersion(nbt.getInt("version"));
		if(nbt.contains("nextLevel", NbtElement.STRING_TYPE))
			data.setNextLevel(Identifier.tryParse(nbt.getString("nextLevel")));
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
					   "TimeRanks=" + Arrays.toString(timeRanks) + ", " +
					   "TimeRankStrings=" + Arrays.toString(timeStrings) + ", " +
					   "KillRanks=" + Arrays.toString(killRanks) + ", " +
					   "StyleRanks=" + Arrays.toString(styleRanks) + ", " +
					   "thumbnail=" + thumbnail + ", " +
					   "structure=" + structure + ", " +
					   "spawnOffset=" + spawnOffset + ']';
	}
}
