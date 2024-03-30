package absolutelyaya.ultracraft;

import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.sound.SoundEvent;

public enum FishPacket
{
	KILLER_SELECT(SoundRegistry.KILLERFISH_SELECT),
	LUMP_SELECT(SoundRegistry.LUMPFISH_SELECT),
	LUMP_UNSELECT(SoundRegistry.LUMPFISH_UNSELECT);
	public final SoundEvent sound;
	
	FishPacket(SoundEvent sound)
	{
		this.sound = sound;
	}
}
