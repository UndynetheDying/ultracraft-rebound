package absolutelyaya.ultracraft;

import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public enum Weapon
{
	REVOLVER(new Identifier[] {
			new Identifier(Ultracraft.MOD_ID, "pierce_revolver"),
			new Identifier(Ultracraft.MOD_ID, "marksman_revolver"),
			new Identifier(Ultracraft.MOD_ID, "sharpshooter_revolver"),
			new Identifier(Ultracraft.MOD_ID, "alternate_piercer"),
			new Identifier(Ultracraft.MOD_ID, "alternate_marksman"),
			new Identifier(Ultracraft.MOD_ID, "alternate_sharpshooter")
	}),
	SHOTGUN(new Identifier[] {
			new Identifier(Ultracraft.MOD_ID, "core_shotgun"),
			new Identifier(Ultracraft.MOD_ID, "pump_shotgun")
	}),
	NAILGUN(new Identifier[]{
			new Identifier(Ultracraft.MOD_ID, "attractor_nailgun"),
			new Identifier(Ultracraft.MOD_ID, "overheat_nailgun")
	}),
	RAILCANNON(null),
	ROCKET_LAUNCHER(null),
	UNIQUE(null);
	public final Identifier[] ids;
	public static final int[] COLORS = new int[] { 0x28ccdf, 0x28df53, 0xdf2828 };
	
	Weapon(Identifier[] ids)
	{
		this.ids = ids;
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
		return super.toString().toLowerCase();
	}
}
