package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.client.gui.TitleHUD;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.cybergrind.CybergrindData;
import absolutelyaya.ultracraft.item.weapons.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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

public class WingedPlayerComponent implements IWingedPlayerComponent, AutoSyncedComponent
{
	PlayerEntity provider;
	GunCooldownManager gunCDM;
	boolean primaryFiring, justPlayedBloodhealNoise;
	byte wingState, lastState;
	int bloodHealCooldown, sharpshooterCooldown, magnets;
	AbstractWeaponItem lastPrimaryWeapon;
	BlockPos lastCheckpoint;
	RegistryKey<World> checkpointDimension;
	float checkpointRot;
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
					w.onPrimaryFireStart(provider.getWorld(), provider, provider.getInventory().selectedSlot);
			}
		}
		else
		{
			if(lastPrimaryWeapon != null)
				lastPrimaryWeapon.onPrimaryFireStop(provider.getWorld(), provider, provider.getInventory().selectedSlot);
			lastPrimaryWeapon = null;
		}
	}
	
	@Override
	public void onUpdateActiveSlot(int lastSlot, int newValue)
	{
		if(lastPrimaryWeapon != null)
			lastPrimaryWeapon.onBeforeSwitch(provider.getWorld(), provider, provider.getInventory().selectedSlot);
		if(provider.getInventory().main.get(newValue).getItem() instanceof AbstractWeaponItem w)
			w.onSwitch(provider.getWorld(), provider, newValue);
		if(provider.getWorld().isClient)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeByte(lastSlot);
			buf.writeByte(newValue);
			ClientPlayNetworking.send(PacketRegistry.SWITCH_SLOT_PACKET_ID, buf);
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
	public boolean setLastCheckpoint(BlockPos pos, World dimension)
	{
		boolean changed = lastCheckpoint == null || !lastCheckpoint.equals(pos);
		lastCheckpoint = pos;
		checkpointRot = provider.getYaw();
		if(dimension != null)
			checkpointDimension = dimension.getRegistryKey();
		else
			checkpointDimension = null;
		return changed;
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
		else
			TitleHUD.Instance.setBigTitle(text, delay);
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
		else
		{
			TitleHUD.Instance.setBoxTitle(text, duration);
			provider.playSound(SoundRegistry.RECEIVE_BOX_TITLE, 1f, 1f);
		}
	}
	
	public void sendBoxTitle(Text text)
	{
		sendBoxTitle(text, 20f);
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
			ServerPlayNetworking.send(serverPlayer, PacketRegistry.SYNC_CYBERGRIND_PACKET_ID, buf);
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
		if(tag.contains("checkpoint", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound checkpoint = tag.getCompound("checkpoint");
			NbtCompound pos = checkpoint.getCompound("pos");
			lastCheckpoint = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
			checkpointRot = checkpoint.getFloat("rot");
			checkpointDimension = RegistryKey.of(RegistryKeys.WORLD, Identifier.tryParse(checkpoint.getString("dimension")));
		}
		else
		{
			lastCheckpoint = null;
			checkpointRot = 0f;
			checkpointDimension = null;
		}
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
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
