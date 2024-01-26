package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.client.GunCooldownManager;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
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
	
	@NotNull
	GunCooldownManager getGunCooldownManager();
	
	int getMagnets();
	
	void setMagnets(int i);
	
	boolean isJustPlayedBloodhealNoise();
	
	void setJustPlayedBloodhealNoise();
	
	BlockPos getLastCheckpoint();
	
	void setLastCheckpoint(BlockPos pos, World dimension);
	
	RegistryKey<World> getCheckpointDimension();
	
	void sendBigTitle(Text text, float delay);
	
	void sendBigTitle(Text text);
	
	void sendBoxTitle(Text text, float duration);
	
	void sendBoxTitle(Text text);
	
	Identifier getCurrentLevel();
	
	void setCurrentLevel(Identifier id);
}
