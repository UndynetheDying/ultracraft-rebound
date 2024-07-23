package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.cybergrind.CybergrindData;
import absolutelyaya.ultracraft.entity.projectile.JumpstartHookEntity;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
	
	void onUpdateActiveSlot(int lastSlot, int newValue);
	
	@NotNull
	GunCooldownManager getGunCooldownManager();
	
	int getMagnets();
	
	void setMagnets(int i);
	
	boolean isJustPlayedBloodhealNoise();
	
	void setJustPlayedBloodhealNoise();
	
	BlockPos getLastCheckpoint();
	
	boolean setLastCheckpoint(BlockPos pos, World dimension);
	
	RegistryKey<World> getCheckpointDimension();
	
	float getCheckpointRotation();
	
	void sendBigTitle(Text text, float delay);
	
	void sendBigTitle(Text text);
	
	void sendBoxTitle(Text text, float duration);
	
	void sendBoxTitle(Text text);
	
	void setCybergrindData(CybergrindData v);
	
	CybergrindData getCybergrindData();
	
	/***
	 * hooked means the entity that this providers jumpstart cable is attached to
	 */
	Entity getHookedEntity();
	
	/***
	 * hooked means the entity that this providers jumpstart cable is attached to
	 */
	boolean isHasHookedEntity();
	
	JumpstartHookEntity getHook();
	
	void setHook(JumpstartHookEntity hook);
	
	void attachMovingSound(String id, Identifier sound, boolean warmUp, float volume);
	
	void removeMovingSound(String id);
}
