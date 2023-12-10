package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public interface IWingedPlayerComponent extends ComponentV3, CommonTickingComponent
{
	void setWingState(byte state);
	
	void updateWingState();
	
	byte getWingState();
	
	void bloodHeal(float val);
	
	void setBloodHealCooldown(int ticks);
	
	void setSharpshooterCooldown(int val);
	
	int getSharpshooterCooldown();
	
	void setPrimaryFiring(boolean firing);
	
	boolean isPrimaryFiring();
	
	@NotNull
	GunCooldownManager getGunCooldownManager();
	
	int getMagnets();
	
	void setMagnets(int i);
}
