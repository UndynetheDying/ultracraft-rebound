package absolutelyaya.ultracraft;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Locale;

public enum Weapon
{
	REVOLVER(new Identifier[] {
			Ultracraft.identifier("pierce_revolver"),
			Ultracraft.identifier("marksman_revolver"),
			Ultracraft.identifier("sharpshooter_revolver"),
			Ultracraft.identifier("alternate_piercer"),
			Ultracraft.identifier("alternate_marksman"),
			Ultracraft.identifier("alternate_sharpshooter")
	}, Ultracraft.identifier("slab")),
	SHOTGUN(new Identifier[] {
			Ultracraft.identifier("core_shotgun"),
			Ultracraft.identifier("pump_shotgun"),
			Ultracraft.identifier("saw_shotgun")
	}, null),
	NAILGUN(new Identifier[]{
			Ultracraft.identifier("attractor_nailgun"),
			Ultracraft.identifier("overheat_nailgun"),
			Ultracraft.identifier("jumpstart_nailgun")
	}, null),
	RAILCANNON(null, null),
	ROCKET_LAUNCHER(null, null),
	UNIQUE(null, null);
	public final Identifier[] ids;
	public final Identifier altId;
	public static final int[] COLORS = new int[] { 0x28ccdf, 0x28df53, 0xdf2828 };
	
	Weapon(Identifier[] ids, Identifier altId)
	{
		this.ids = ids;
		this.altId = altId;
	}
	
	public boolean isAnyUnlocked(PlayerEntity player)
	{
		if(ids == null)
			return false;
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(player);
		for (Identifier id : ids)
			if(progression.isUnlocked(id))
				return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return super.toString().toLowerCase(Locale.ROOT);
	}
	
	public int getIdxForId(Identifier id)
	{
		for (int i = 0; i < ids.length; i++)
			if(ids[i].equals(id))
				return i;
		return -1;
	}
	
	public Identifier[] getDefaultLoadout()
	{
		if(ids == null)
			return new Identifier[0];
		Identifier[] loadout = new Identifier[Math.min(ids.length, 3)];
		System.arraycopy(ids, 0, loadout, 0, Math.min(ids.length, 3));
		return loadout;
	}
	
	public Identifier getAlt(Identifier id)
	{
		if (altId == null)
			return null;
		int i = getIdxForId(id) + ids.length / 2;
		if(i < ids.length)
			return ids[i];
		return null;
	}
}
