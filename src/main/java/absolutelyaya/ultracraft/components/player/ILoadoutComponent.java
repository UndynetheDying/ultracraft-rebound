package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.util.Identifier;

public interface ILoadoutComponent extends ComponentV3, AutoSyncedComponent
{
	Identifier[] getLoadoutForWeapon(Weapon weapon);
	
	void setLoadoutForWeapon(Weapon weapon, Identifier[] ids);
	
	boolean isInLoadout(AbstractWeaponItem weaponItem);
	
	boolean isInLoadout(Weapon weapon, Identifier id);
	
	boolean isAltInLoadout(Weapon weapon, Identifier id);
	
	void sync(Weapon weapon);
}
