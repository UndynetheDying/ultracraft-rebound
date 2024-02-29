package absolutelyaya.ultracraft.components.level;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.config.ServerConfig;
import absolutelyaya.ultracraft.data.LevelDataManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.world.WorldProperties;

import java.util.*;

public class UltraLevelComponent implements IUltraLevelComponent
{
	final WorldProperties provider;
	
	boolean hivelWhitelistActive, graffitiWhitelistActive;
	Map<UUID, String> hivelWhitelist = new HashMap<>(), graffitiWhitelist = new HashMap<>();
	List<Identifier> unlockedDestinations = new ArrayList<>();
	
	public UltraLevelComponent(WorldProperties properties)
	{
		provider = properties;
	}
	
	@Override
	public boolean isHivelWhitelistActive()
	{
		return hivelWhitelistActive;
	}
	
	@Override
	public void setHivelWhitelistActive(boolean v)
	{
		hivelWhitelistActive = v;
	}
	
	@Override
	public Map<UUID, String> getHivelWhitelist()
	{
		return hivelWhitelist;
	}
	
	@Override
	public boolean isPlayerAllowedToHivel(PlayerEntity player)
	{
		return !hivelWhitelistActive || hivelWhitelist.containsKey(player.getUuid());
	}
	
	@Override
	public boolean isGraffitiWhitelistActive()
	{
		return graffitiWhitelistActive;
	}
	
	@Override
	public void setGraffitiWhitelistActive(boolean v)
	{
		graffitiWhitelistActive = v;
	}
	
	@Override
	public Map<UUID, String> getGraffitiWhitelist()
	{
		return graffitiWhitelist;
	}
	
	@Override
	public boolean isPlayerAllowedToGraffiti(PlayerEntity player)
	{
		return !graffitiWhitelistActive || graffitiWhitelist.containsKey(player.getUuid());
	}
	
	@Override
	public boolean isDestinationUnlocked(Identifier id)
	{
		return unlockedDestinations.contains(id) || (!LevelDataManager.getLevelData(id).getBuiltin() && ServerConfig.INSTANCE.customLevelsUnlocked.getValue());
	}
	
	@Override
	public boolean unlockDestination(Identifier id)
	{
		if(!isDestinationUnlocked(id))
			return unlockedDestinations.add(id);
		return false;
	}
	
	@Override
	public void unlockAllDestinations()
	{
		unlockDestination(new Identifier(Ultracraft.MOD_ID, "dimension.overworld"));
		unlockDestination(new Identifier(Ultracraft.MOD_ID, "tutorial"));
		unlockDestination(new Identifier(Ultracraft.MOD_ID, "prelude1"));
		unlockDestination(new Identifier(Ultracraft.MOD_ID, "limbo1"));
		unlockDestination(new Identifier(Ultracraft.MOD_ID, "dimension.limbo"));
	}
	
	@Override
	public void lockDestination(Identifier id)
	{
		if(isDestinationUnlocked(id))
			unlockedDestinations.remove(id);
	}
	
	@Override
	public void setDestinations(List<Identifier> ids)
	{
		unlockedDestinations = ids;
	}
	
	@Override
	public List<Identifier> getUnlockedDestinationList()
	{
		return unlockedDestinations;
	}
	
	@Override
	public void resetGlobalProgression()
	{
		unlockedDestinations.clear();
		unlockedDestinations.add(new Identifier(Ultracraft.MOD_ID, "dimension.overworld"));
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(!tag.contains("whitelists", NbtElement.COMPOUND_TYPE))
			return;
		NbtCompound whitelists = tag.getCompound("whitelists");
		if(whitelists.contains("hivel", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound hivel = whitelists.getCompound("hivel");
			if(hivel.contains("active", NbtElement.BYTE_TYPE))
				hivelWhitelistActive = hivel.getBoolean("active");
			if(hivel.contains("entries", NbtElement.LIST_TYPE))
			{
				hivel.getList("entries", NbtElement.STRING_TYPE).forEach(i -> {
					String[] segments = i.asString().split("@");
					if(segments.length < 2)
						return;
					UUID id = UUID.fromString(segments[0]);
					hivelWhitelist.put(id, segments[1]);
				});
			}
		}
		if(whitelists.contains("graffiti", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound graffiti = whitelists.getCompound("graffiti");
			if(graffiti.contains("active", NbtElement.BYTE_TYPE))
				graffitiWhitelistActive = graffiti.getBoolean("active");
			if(graffiti.contains("entries", NbtElement.LIST_TYPE))
			{
				graffiti.getList("entries", NbtElement.STRING_TYPE).forEach(i -> {
					String[] segments = i.asString().split("@");
					if(segments.length < 2)
						return;
					UUID id = UUID.fromString(segments[0]);
					graffitiWhitelist.put(id, segments[1]);
				});
			}
		}
		NbtCompound progression = tag.getCompound("progression");
		if(progression.contains("destinations", NbtElement.LIST_TYPE))
		{
			unlockedDestinations.clear();
			NbtList list = progression.getList("destinations", NbtElement.STRING_TYPE);
			list.forEach(i -> unlockedDestinations.add(Identifier.tryParse(i.asString())));
		}
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		NbtCompound whitelists = new NbtCompound();
		
		NbtCompound hivel = new NbtCompound();
		hivel.putBoolean("active", hivelWhitelistActive);
		NbtList hivelList = new NbtList();
		hivelWhitelist.forEach((key, value) -> hivelList.add(NbtString.of(key + "@" + value)));
		hivel.put("entries", hivelList);
		whitelists.put("hivel", hivel);
		
		NbtCompound graffiti = new NbtCompound();
		graffiti.putBoolean("active", graffitiWhitelistActive);
		NbtList graffitiList = new NbtList();
		graffitiWhitelist.forEach((key, value) -> graffitiList.add(NbtString.of(key + "@" + value)));
		graffiti.put("entries", graffitiList);
		whitelists.put("graffiti", graffiti);
		
		NbtCompound progression = new NbtCompound();
		
		NbtList destinations = new NbtList();
		unlockedDestinations.forEach(i -> destinations.add(NbtString.of(i.toString())));
		
		progression.put("destinations", destinations);
		
		tag.put("whitelists", whitelists);
		tag.put("progression", progression);
	}
}
