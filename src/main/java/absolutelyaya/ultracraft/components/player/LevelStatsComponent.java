package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.gui.LevelHUD;
import absolutelyaya.ultracraft.client.sound.ModularLevelMusic;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.util.TimeUtil;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;

import static absolutelyaya.ultracraft.data.LevelDataManager.getLevelData;

public class LevelStatsComponent implements ILevelStatsComponent, AutoSyncedComponent
{
	PlayerEntity provider;
	HashMap<Identifier, Pair<Long, Long>> bestTimes = new HashMap<>();
	HashMap<Identifier, Integer> lastPlayedVersion = new HashMap<>();
	HashMap<Identifier, Integer> bestRanks = new HashMap<>();
	Identifier currentLevel;
	String currentLevelInstance, curLevelSoundTrackKey = "default";
	boolean fighting, undamaged, invalid, shouldMusicFade;
	int fightCheckCooldown, deaths, kills, combatThreshold;
	float style;
	long timerStart = -1, lastStoppedTimer = -1, timerPause = -1;
	
	public LevelStatsComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public void enterLevel(Identifier levelId, String instance)
	{
		deaths = 0;
		style = 0;
		lastStoppedTimer = -1;
		undamaged = true;
		invalid = false;
		stopTimer(true);
		String lastInstance = currentLevelInstance; //prevents infinite loop when rescuing from null instance
		currentLevelInstance = instance;
		if(!provider.getWorld().isClient && lastInstance != null)
			LevelManager.Instance.leaveInstance((ServerPlayerEntity)provider, lastInstance);
		setShouldMusicFade(currentLevel != null);
		currentLevel = levelId;
		if(levelId != null)
		{
			lastPlayedVersion.put(levelId, getLevelData(levelId).getVersion());
			Ultracraft.rechargeWeapons(provider);
			provider.setHealth(provider.getMaxHealth());
		}
		setCurLevelSoundTrackKey("default");
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public Identifier getCurrentLevel()
	{
		return currentLevel;
	}
	
	@Override
	public String getCurrentLevelInstance()
	{
		return currentLevelInstance;
	}
	
	@Override
	public boolean isInFight()
	{
		if(fightCheckCooldown-- > 0)
			return fighting;
		fighting = provider.getWorld().getOtherEntities(provider, provider.getBoundingBox().expand(32f),
				e -> e instanceof AbstractUltraHostileEntity && e.isAlive()).size() > combatThreshold;
		fightCheckCooldown = 5;
		return fighting;
	}
	
	@Override
	public boolean isTimerRunning()
	{
		return timerStart > -1;
	}
	
	@Override
	public void startTimer()
	{
		if(currentLevel == null)
			return;
		timerStart = System.currentTimeMillis();
		timerPause = -1;
		if(provider.getWorld().isClient)
			LevelHUD.Instance.initTimer(bestTimes.getOrDefault(currentLevel, new Pair<>(-1L, -1L)), getLevelData(currentLevel).getTimeRanks());
	}
	
	@Override
	public void stopTimer(boolean interruption)
	{
		//if(!isTimerRunning())
		//{
		//	timerStart = -1;
		//	return;
		//}
		//UltraComponents.LEVEL_STATS.sync(provider);
		if(!interruption && currentLevel != null)
		{
			lastStoppedTimer = getElapsedTimer();
			if(provider.getWorld().isClient)
				LevelHUD.Instance.stopTimer();
		}
		timerStart = -1;
	}
	
	@Override
	public void setTimerPaused(boolean v)
	{
		if(v && !isTimerPaused() && isTimerRunning())
			timerPause = System.currentTimeMillis();
		else if(isTimerPaused() && isTimerRunning())
		{
			timerStart += System.currentTimeMillis() - timerPause;
			timerPause = -1;
		}
	}
	
	@Override
	public boolean isTimerPaused()
	{
		return timerPause != -1;
	}
	
	@Override
	public long getElapsedTimer()
	{
		if(!isTimerRunning())
			return -1;
		long time = System.currentTimeMillis();
		if(timerPause == -1)
			return time - timerStart;
		else
			return (time - timerStart) - (time - timerPause);
	}
	
	@Override
	public void setBestTime(Identifier levelId, boolean perfect, long time)
	{
		if(time == -1 || isInvalid())
			return;
		Pair<Long, Long> pb = bestTimes.getOrDefault(levelId, new Pair<>(-1L, -1L));
		boolean pbChanged = false;
		if(perfect && (pb.getRight() == -1 || time < pb.getRight()))
		{
			pb.setRight(time);
			pbChanged = true;
		}
		if(pb.getLeft() == -1 || time < pb.getLeft())
		{
			pb.setLeft(time);
			pbChanged = true;
		}
		if(!pbChanged)
			return;
		bestTimes.put(levelId, pb);
		if(provider.getWorld().isClient)
		{
			Text levelName = getLevelData(levelId).getTitleText();
			provider.sendMessage(Text.translatable("message.ultracraft.level.new-pb-time", levelName, TimeUtil.milliToString(time)));
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeIdentifier(levelId);
			buf.writeLong(time);
			buf.writeBoolean(perfect);
			ClientPlayNetworking.send(PacketRegistry.SUBMIT_BEST_TIME_PACKET_ID, buf);
		}
	}
	
	@Override
	public long getLastStoppedTimer()
	{
		return lastStoppedTimer;
	}
	
	@Override
	public void onDeath()
	{
		deaths++;
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public int getDeaths()
	{
		return deaths;
	}
	
	@Override
	public int getKills()
	{
		LevelManager.LevelInstance inst = LevelManager.Instance.getInstance(currentLevelInstance);
		if(inst == null)
			return kills;
		return inst.getKills();
	}
	
	@Override
	public void setKills(int kills)
	{
		this.kills = kills;
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public int getStyle()
	{
		return (int)style;
	}
	
	@Override
	public void onStyle(float score)
	{
		style += score;
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public void onDamage()
	{
		undamaged = false;
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public boolean isUndamaged()
	{
		return undamaged;
	}
	
	@Override
	public long getBestTime(Identifier id, boolean perfect)
	{
		Pair<Long, Long> pb = bestTimes.getOrDefault(id, null);
		if(pb == null)
			return -1L;
		return perfect ? pb.getRight() : pb.getLeft();
	}
	
	@Override
	public void resetBestTime(Identifier levelId)
	{
		bestTimes.remove(levelId);
	}
	
	@Override
	public int getLastPlayedLevelVersion(Identifier id)
	{
		return lastPlayedVersion.getOrDefault(id, -1);
	}
	
	@Override
	public int getBestRank(Identifier levelId)
	{
		return bestRanks.getOrDefault(levelId, -1);
	}
	
	@Override
	public void setBestRank(Identifier levelId, int rank)
	{
		if(rank == -1)
		{
			bestRanks.remove(levelId);
			return;
		}
		if(isInvalid())
			return;
		//lower is better!! P-Rank == 0
		if(bestRanks.containsKey(levelId) && bestRanks.get(levelId) <= rank)
			return;
		bestRanks.put(levelId, rank);
		if(provider.getWorld().isClient)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeIdentifier(levelId);
			buf.writeByte(rank);
			ClientPlayNetworking.send(PacketRegistry.SUBMIT_BEST_RANK_PACKET_ID, buf);
		}
	}
	
	@Override
	public void setInvalid()
	{
		invalid = true;
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	public boolean isInvalid()
	{
		return invalid || provider.isCreative() || provider.isSpectator();
	}
	
	@Override
	public void setCurLevelSoundTrackKey(String key)
	{
		curLevelSoundTrackKey = key;
		if(currentLevel != null)
		{
			ModularLevelMusic music = LevelDataManager.getLevelData(currentLevel).getMusic(key);
			if(music != null && music.shouldShowPopup())
			{
				LevelHUD.Instance.queueNewMusicPopup(music);
				combatThreshold = music.getCombatThreshold();
			}
		}
	}
	
	@Override
	public String getCurLevelSoundTrackKey()
	{
		return curLevelSoundTrackKey;
	}
	
	@Override
	public void setShouldMusicFade(boolean val)
	{
		shouldMusicFade = val;
		System.out.println(val ? "FADE" : "NOO");
	}
	
	@Override
	public boolean shouldLevelMusicFade()
	{
		return shouldMusicFade;
	}
	
	@Override
	public void onFinishLevel()
	{
		if(currentLevel != null)
		{
			combatThreshold = 0;
			setShouldMusicFade(true);
			curLevelSoundTrackKey = null;
			UltraComponents.LEVEL_STATS.sync(provider);
		}
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("level", NbtElement.STRING_TYPE))
			currentLevel = new Identifier(tag.getString("level"));
		else
			currentLevel = null;
		if(tag.contains("timerStart", NbtElement.LONG_TYPE))
			timerStart = tag.getLong("timerStart");
		if(tag.contains("lastStoppedTime", NbtElement.LONG_TYPE))
			lastStoppedTimer = tag.getLong("lastStoppedTime");
		if(tag.contains("style", NbtElement.FLOAT_TYPE))
			style = tag.getLong("style");
		if(tag.contains("deaths", NbtElement.INT_TYPE))
			deaths = tag.getInt("deaths");
		if(tag.contains("kills", NbtElement.INT_TYPE))
			kills = tag.getInt("kills");
		if(tag.contains("combatThreshold", NbtElement.INT_TYPE))
			combatThreshold = tag.getInt("combatThreshold");
		invalid = tag.contains("invalid", NbtElement.BYTE_TYPE);
		undamaged = tag.contains("undamaged", NbtElement.BYTE_TYPE);
		if(tag.contains("shouldMusicFade", NbtElement.BYTE_TYPE))
			setShouldMusicFade(true);
		if(tag.contains("bestTimes", NbtElement.COMPOUND_TYPE))
		{
			bestTimes.clear();
			NbtCompound records = tag.getCompound("bestTimes");
			for(String key : records.getKeys())
			{
				NbtCompound pb = records.getCompound(key);
				bestTimes.put(Identifier.tryParse(key), new Pair<>(pb.getLong("imperfect"), pb.getLong("perfect")));
			}
		}
		if(tag.contains("bestRanks", NbtElement.COMPOUND_TYPE))
		{
			bestRanks.clear();
			NbtCompound records = tag.getCompound("bestRanks");
			for(String key : records.getKeys())
				bestRanks.put(Identifier.tryParse(key), records.getInt(key));
		}
		if(tag.contains("versions", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound versions = tag.getCompound("versions");
			for(String key : versions.getKeys())
				lastPlayedVersion.put(Identifier.tryParse(key), versions.getInt(key));
		}
		if(tag.contains("currentLevelInstance", NbtElement.STRING_TYPE))
			currentLevelInstance = tag.getString("currentLevelInstance");
		if(tag.contains("soundtrackKey", NbtElement.STRING_TYPE))
			curLevelSoundTrackKey = tag.getString("soundtrackKey");
		else
			curLevelSoundTrackKey = null;
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putLong("timerStart", timerStart);
		tag.putLong("lastStoppedTime", lastStoppedTimer);
		tag.putFloat("style", style);
		tag.putInt("deaths", deaths);
		tag.putInt("kills", kills);
		if(invalid)
			tag.putBoolean("invalid", true);
		if(undamaged)
			tag.putBoolean("undamaged", true);
		if(shouldMusicFade)
		{
			tag.putBoolean("shouldMusicFade", true);
			shouldMusicFade = false;
		}
		tag.putInt("combatThreshold", combatThreshold);
		if(bestTimes != null)
		{
			NbtCompound records = new NbtCompound();
			for (Map.Entry<Identifier, Pair<Long, Long>> e : bestTimes.entrySet())
			{
				NbtCompound pb = new NbtCompound();
				pb.putLong("imperfect", e.getValue().getLeft());
				pb.putLong("perfect", e.getValue().getRight());
				records.put(e.getKey().toString(), pb);
			}
			tag.put("bestTimes", records);
		}
		if(bestRanks != null)
		{
			NbtCompound records = new NbtCompound();
			for (Map.Entry<Identifier, Integer> e : bestRanks.entrySet())
				records.putInt(e.getKey().toString(), e.getValue());
			tag.put("bestRanks", records);
		}
		if(lastPlayedVersion.size() > 0)
		{
			NbtCompound records = new NbtCompound();
			for (Map.Entry<Identifier, Integer> e : lastPlayedVersion.entrySet())
				records.putInt(e.getKey().toString(), e.getValue());
			tag.put("versions", records);
		}
		if(getCurrentLevel() != null)
			tag.putString("level", getCurrentLevel().toString());
		if(currentLevelInstance != null && !currentLevelInstance.isEmpty())
			tag.putString("currentLevelInstance", currentLevelInstance);
		if(curLevelSoundTrackKey != null)
			tag.putString("soundtrackKey", curLevelSoundTrackKey);
	}
}
