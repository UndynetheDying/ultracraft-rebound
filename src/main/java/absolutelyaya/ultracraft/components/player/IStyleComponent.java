package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.data.StyleBonus;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.util.Identifier;

public interface IStyleComponent extends ComponentV3, AutoSyncedComponent, CommonTickingComponent
{
	void styleBonusGet(StyleBonus bonus);
	
	void clientStyleBonusGet(String key);
	
	String[] getRecentBonuses();
	
	int getStaleness(Identifier id);
	
	int getScore();
	
	int getRank();
	
	void resetScore();
	
	void takeDamage(float damage);
	
	void markDirty();
	
	float getRankProgress();
	
	float getChain();
	
	float getMovementMultiplier();
}
