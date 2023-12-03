package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.style.StyleBonus;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.util.Identifier;

public interface IStyleComponent extends ComponentV3, AutoSyncedComponent, CommonTickingComponent
{
	void styleBonusGet(StyleBonus bonus);
	
	String[] getRecentBonuses();
	
	int getStaleness(Identifier id);
	
	int getScore();
	
	void resetScore();
	
	void takeDamage(float damage);
	
	void sync();
}
