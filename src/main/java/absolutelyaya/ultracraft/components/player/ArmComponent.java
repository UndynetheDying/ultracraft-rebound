package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.LivingEntityAccessor;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class ArmComponent implements IArmComponent
{
	public static final Identifier[] armIDs = new Identifier[]
		{
			ProgressionComponent.FEEDBACKER,
			ProgressionComponent.KNUCKLEBLASTER
		};
	
	final PlayerEntity provider;
	byte activeArm = 0;
	boolean visible, punchPressed;
	
	public ArmComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public byte getActiveArm()
	{
		return activeArm;
	}
	
	@Override
	public boolean setActiveArm(byte i)
	{
		boolean ignoreChangedCheck = false;
		if(i == -1)
		{
			ignoreChangedCheck = true;
			i = activeArm;
		}
		if(i == -1)
			return false;
		if(armIDs.length <= i || i < 0)
		{
			Ultracraft.LOGGER.warn("Tried equipping invalid Arm (index " + i + " out of bounds)");
			return false;
		}
		if(!ignoreChangedCheck && activeArm == i)
			return false;
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(provider);
		if(progression.isUnlocked(armIDs[i]))
			activeArm = i;
		else
			activeArm = getFirstUnlockedArmIfCurrentlyInvalid();
		sync();
		return true;
	}
	
	@Override
	public void cycleArms()
	{
		if(provider instanceof LivingEntityAccessor living)
			living.cancelPunch();
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(provider);
		byte start = activeArm;
		for (int i = 1; i < armIDs.length; i++)
		{
			int idx = (start + i) % (armIDs.length);
			Identifier id = armIDs[idx];
			if(activeArm != idx && progression.isUnlocked(id))
			{
				activeArm = (byte)idx;
				if(!provider.getWorld().isClient)
					sync();
				provider.playSound(SoundRegistry.ARM_SWITCH, 1f, 1f);
				return;
			}
		}
	}
	
	@Override
	public void sync()
	{
		UltraComponents.ARMS.sync(provider);
	}
	
	@Override
	public byte getUnlockedArmCount()
	{
		byte v = 0;
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(provider);
		for (Identifier id : armIDs)
		{
			if (progression.isUnlocked(id))
				v++;
		}
		return v;
	}
	
	@Override
	public boolean isFeedbacker()
	{
		return activeArm == 0;
	}
	
	@Override
	public boolean isKnuckleblaster()
	{
		return activeArm == 1;
	}
	
	@Override
	public void setArmVisible(boolean v)
	{
		visible = v;
		sync();
	}
	
	@Override
	public boolean isVisible()
	{
		return visible && getUnlockedArmCount() > 0;
	}
	
	@Override
	public void setPunchPressed(boolean v)
	{
		if(v)
		{
			byte i = getFirstUnlockedArmIfCurrentlyInvalid();
			if(i == -1)
			{
				punchPressed = false;
				sync();
				return;
			}
			else
				setActiveArm(i);
		}
		punchPressed = v;
		sync();
	}
	
	byte getFirstUnlockedArmIfCurrentlyInvalid()
	{
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(provider);
		if(activeArm == -1 || !progression.isUnlocked(armIDs[activeArm]))
		{
			for (int i = 0; i < armIDs.length; i++)
				if(progression.isUnlocked(armIDs[i]))
					return (byte)i;
			return -1;
		}
		return activeArm;
	}
	
	@Override
	public boolean isPunchPressed()
	{
		return punchPressed;
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("active", NbtElement.BYTE_TYPE))
			activeArm = tag.getByte("active");
		if(tag.contains("visible", NbtElement.BYTE_TYPE))
			visible = tag.getBoolean("visible");
		if(tag.contains("punchPressed", NbtElement.BYTE_TYPE))
			punchPressed = tag.getBoolean("punchPressed");
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putByte("active", activeArm);
		tag.putBoolean("visible", visible);
		tag.putBoolean("punchPressed", punchPressed);
	}
}
