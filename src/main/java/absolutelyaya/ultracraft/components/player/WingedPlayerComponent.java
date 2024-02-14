package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.client.gui.LevelHUD;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.cybergrind.CybergrindData;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.util.TimeUtil;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WingedPlayerComponent implements IWingedPlayerComponent, AutoSyncedComponent
{
	PlayerEntity provider;
	HashMap<Identifier, Long> bestTimes = new HashMap<>();
	HashMap<Identifier, Integer> lastPlayedVersion = new HashMap<>();
	GunCooldownManager gunCDM;
	boolean primaryFiring, justPlayedBloodhealNoise;
	byte wingState, lastState;
	int bloodHealCooldown, sharpshooterCooldown, magnets;
	AbstractWeaponItem lastPrimaryWeapon;
	BlockPos lastCheckpoint;
	RegistryKey<World> checkpointDimension;
	float checkpointRot;
	Identifier currentLevel;
	String currentLevelInstance;
	boolean fighting, perfect;
	int fightCheckCooldown;
	long timerStart = -1;
	CybergrindData cybergrindData;
	
	public WingedPlayerComponent(PlayerEntity provider)
	{
		this.provider = provider;
		gunCDM = new GunCooldownManager(provider);
	}
	
	@Override
	public void setWingState(byte state)
	{
		if(wingState != state)
		{
			lastState = wingState;
			wingState = state;
		}
	}
	
	@Override
	public void updateWingState()
	{
		IHivelComponent hivel = UltraComponents.HIVEL.get(provider);
		if(!(provider instanceof WingedPlayerEntity winged))
			return;
		if(hivel.isDashing())
			setWingState((byte)0);
		if (winged.isSliding())
			setWingState((byte)2);
		else if ((wingState == 0 && provider.isOnGround()) || (wingState == 2 && !winged.isSliding()))
			setWingState((byte)1);
	}
	
	@Override
	public byte getWingState()
	{
		return wingState;
	}
	
	@Override
	public void bloodHeal(float val)
	{
		if(bloodHealCooldown == 0)
			provider.heal(val);
		justPlayedBloodhealNoise = true;
	}
	
	@Override
	public void setBloodHealCooldown(int ticks)
	{
		bloodHealCooldown = ticks;
	}
	
	@Override
	public void setSharpshooterCooldown(int val)
	{
		sharpshooterCooldown = val;
	}
	
	@Override
	public int getSharpshooterCooldown()
	{
		return sharpshooterCooldown;
	}
	
	@Override
	public void setPrimaryFiring(boolean firing)
	{
		boolean last = primaryFiring;
		primaryFiring = firing;
		if(firing)
		{
			if(provider.getInventory().getMainHandStack().getItem() instanceof AbstractWeaponItem w)
			{
				lastPrimaryWeapon = w;
				if(!last)
					w.onPrimaryFireStart(provider.getWorld(), provider);
			}
		}
		else
		{
			if(lastPrimaryWeapon != null)
				lastPrimaryWeapon.onPrimaryFireStop(provider.getWorld(), provider);
			lastPrimaryWeapon = null;
		}
	}
	
	@Override
	public boolean isPrimaryFiring()
	{
		return primaryFiring;
	}
	
	@Override
	public @NotNull GunCooldownManager getGunCooldownManager()
	{
		return gunCDM;
	}
	
	public void setMagnets(int i)
	{
		magnets = i;
	}
	
	public int getMagnets()
	{
		return magnets;
	}
	
	@Override
	public boolean isJustPlayedBloodhealNoise()
	{
		return justPlayedBloodhealNoise;
	}
	
	@Override
	public void setJustPlayedBloodhealNoise()
	{
		justPlayedBloodhealNoise = true;
	}
	
	@Override
	public BlockPos getLastCheckpoint()
	{
		return lastCheckpoint;
	}
	
	@Override
	public void setLastCheckpoint(BlockPos pos, World dimension)
	{
		lastCheckpoint = pos;
		checkpointRot = provider.getYaw();
		if(dimension != null)
			checkpointDimension = dimension.getRegistryKey();
	}
	
	@Override
	public RegistryKey<World> getCheckpointDimension()
	{
		return checkpointDimension;
	}
	
	@Override
	public float getCheckpointRotation()
	{
		return checkpointRot;
	}
	
	public void sendBigTitle(Text text, float delay)
	{
		if(!provider.getWorld().isClient)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBoolean(true);
			buf.writeText(text);
			buf.writeFloat(delay);
			ServerPlayNetworking.send((ServerPlayerEntity)provider, PacketRegistry.TITLE_PACKET_ID, buf);
		}
	}
	
	public void sendBigTitle(Text text)
	{
		sendBigTitle(text, 0f);
	}
	
	public void sendBoxTitle(Text text, float duration)
	{
		if(!provider.getWorld().isClient)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBoolean(false);
			buf.writeText(text);
			buf.writeFloat(duration);
			ServerPlayNetworking.send((ServerPlayerEntity)provider, PacketRegistry.TITLE_PACKET_ID, buf);
		}
	}
	
	public void sendBoxTitle(Text text)
	{
		sendBoxTitle(text, 30f);
	}
	
	@Override
	public void enterLevel(Identifier levelId, String instance)
	{
		perfect = true;
		stopTimer(true);
		String lastInstance = currentLevelInstance; //prevents infinite loop when rescuing from null instance
		currentLevelInstance = instance;
		if(!provider.getWorld().isClient && lastInstance != null)
			LevelManager.Instance.leaveInstance((ServerPlayerEntity)provider, lastInstance);
		currentLevel = levelId;
		if(levelId != null)
			lastPlayedVersion.put(levelId, LevelManager.getLevelData(levelId).getVersion());
		UltraComponents.WINGED.sync(provider);
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
				e -> e instanceof AbstractUltraHostileEntity && e.isAlive()).size() > 0;
		fightCheckCooldown = 10;
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
		if(provider.getWorld().isClient)
			LevelHUD.Instance.initTimer(bestTimes.getOrDefault(currentLevel, Long.MAX_VALUE), LevelManager.getLevelData(currentLevel).getParTime());
	}
	
	@Override
	public void stopTimer(boolean interruption)
	{
		if(!interruption && currentLevel != null)
		{
			long elapsedTime = getElapsedTimer();
			boolean pb = bestTimes.getOrDefault(currentLevel, Long.MAX_VALUE) > elapsedTime;
			if(pb)
			{
				bestTimes.put(currentLevel, elapsedTime);
				if(provider.getWorld().isClient)
				{
					Text levelName = LevelManager.getLevelData(currentLevel).getTitle();
					provider.sendMessage(Text.translatable("message.ultracraft.level.new-pb-time", levelName, TimeUtil.milliToString(elapsedTime)));
				}
			}
			if(provider.getWorld().isClient)
				LevelHUD.Instance.stopTimer();
		}
		timerStart = -1;
	}
	
	@Override
	public long getElapsedTimer()
	{
		if(!isTimerRunning())
			return -1;
		return System.currentTimeMillis() - timerStart;
	}
	
	@Override
	public void removePerfect()
	{
		perfect = false;
		UltraComponents.WINGED.sync(provider);
	}
	
	@Override
	public boolean isPerfect()
	{
		return perfect;
	}
	
	@Override
	public long getBestTime(Identifier id)
	{
		return bestTimes.getOrDefault(id, -1L);
	}
	
	@Override
	public int getLastPlayedLevelVersion(Identifier id)
	{
		return lastPlayedVersion.getOrDefault(id, -1);
	}
	
	@Override
	public void setCybergrindData(CybergrindData v)
	{
		cybergrindData = v;
		if(!provider.getWorld().isClient && provider instanceof ServerPlayerEntity serverPlayer)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			if(v == null)
				buf.writeByte(CybergrindData.DESTROY_SYNC);
			else
			{
				buf.writeByte(CybergrindData.FULL_SYNC);
				buf.writeNbt(v.serialize());
			}
			ServerPlayNetworking.send(serverPlayer, PacketRegistry.SYNC_CYBERGRIND, buf);
		}
	}
	
	@Override
	public CybergrindData getCybergrindData()
	{
		return cybergrindData;
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("level", NbtElement.STRING_TYPE))
			currentLevel = new Identifier(tag.getString("level"));
		if(tag.contains("timerStart", NbtElement.LONG_TYPE))
			timerStart = tag.getLong("timerStart");
		if(tag.contains("timerStart", NbtElement.LONG_TYPE))
			timerStart = tag.getLong("timerStart");
		if(tag.contains("perfect", NbtElement.BYTE_TYPE))
			perfect = tag.getBoolean("perfect");
		if(tag.contains("records", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound records = tag.getCompound("records");
			for(String key : records.getKeys())
				bestTimes.put(Identifier.tryParse(key), records.getLong(key));
		}
		if(tag.contains("versions", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound versions = tag.getCompound("versions");
			for(String key : versions.getKeys())
				lastPlayedVersion.put(Identifier.tryParse(key), versions.getInt(key));
		}
		if(tag.contains("checkpoint", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound checkpoint = tag.getCompound("checkpoint");
			NbtCompound pos = checkpoint.getCompound("pos");
			lastCheckpoint = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
			checkpointRot = checkpoint.getFloat("rot");
			checkpointDimension = RegistryKey.of(RegistryKeys.WORLD, Identifier.tryParse(checkpoint.getString("dimension")));
		}
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		if(getCurrentLevel() != null)
		{
			tag.putString("level", getCurrentLevel().toString());
			tag.putBoolean("perfect", perfect);
		}
		if(timerStart != -1)
			tag.putLong("timerStart", timerStart);
		if(bestTimes.size() > 0)
		{
			NbtCompound records = new NbtCompound();
			for (Map.Entry<Identifier, Long> e : bestTimes.entrySet())
				records.putLong(e.getKey().toString(), e.getValue());
			tag.put("records", records);
		}
		if(lastPlayedVersion.size() > 0)
		{
			NbtCompound records = new NbtCompound();
			for (Map.Entry<Identifier, Integer> e : lastPlayedVersion.entrySet())
				records.putInt(e.getKey().toString(), e.getValue());
			tag.put("versions", records);
		}
		if(lastCheckpoint != null)
		{
			NbtCompound checkpoint = new NbtCompound();
			NbtCompound pos = new NbtCompound();
			pos.putInt("x", lastCheckpoint.getX());
			pos.putInt("y", lastCheckpoint.getY());
			pos.putInt("z", lastCheckpoint.getZ());
			checkpoint.put("pos", pos);
			checkpoint.putFloat("rot", checkpointRot);
			checkpoint.putString("dimension", getCheckpointDimension().getValue().toString());
			tag.put("checkpoint", checkpoint);
		}
	}
	
	@Override
	public void tick()
	{
		if(!Ultracraft.isTimeFrozen())
			gunCDM.tickCooldowns();
		if(bloodHealCooldown > 0)
			bloodHealCooldown--;
		if(sharpshooterCooldown > 0)
			sharpshooterCooldown--;
		if(justPlayedBloodhealNoise)
			justPlayedBloodhealNoise = false;
		updateWingState();
	}
}
