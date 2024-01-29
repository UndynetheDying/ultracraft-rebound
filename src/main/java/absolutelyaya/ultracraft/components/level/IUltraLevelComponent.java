package absolutelyaya.ultracraft.components.level;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IUltraLevelComponent extends ComponentV3
{
	boolean isHivelWhitelistActive();
	
	void setHivelWhitelistActive(boolean v);
	
	Map<UUID, String> getHivelWhitelist();
	
	boolean isPlayerAllowedToHivel(PlayerEntity player);
	
	boolean isGraffitiWhitelistActive();
	
	void setGraffitiWhitelistActive(boolean v);
	
	Map<UUID, String> getGraffitiWhitelist();
	
	boolean isPlayerAllowedToGraffiti(PlayerEntity player);
	
	boolean isDestinationUnlocked(Identifier id);
	
	boolean unlockDestination(Identifier id);
	
	void unlockAllDestinations();
	
	void lockDestination(Identifier id);
	
	void setDestinations(List<Identifier> ids);
	
	List<Identifier> getUnlockedDestinationList();
	
	void resetGlobalProgression();
}
