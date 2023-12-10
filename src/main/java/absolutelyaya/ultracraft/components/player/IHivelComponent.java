package absolutelyaya.ultracraft.components.player;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;

public interface IHivelComponent extends ComponentV3, AutoSyncedComponent, CommonTickingComponent
{
	void setSliding(boolean v);
	
	boolean isSliding();
	
	void onDash();
	
	void cancelDash();
	
	void onDashJump();
	
	boolean isDashing();
	
	boolean wasDashing();
	
	boolean wasDashing(int i);
	
	int getDashingTicks();
	
	float getStamina();
	
	boolean consumeStamina();
	
	void replenishStamina(int i);
	
	void setSlamming(boolean b);
	
	boolean isSlamming();
	
	boolean shouldIgnoreSlowdown();
	
	void setIgnoreSlowdown(boolean b);
	
	void setAirControlIncreased(boolean b);
	
	boolean isAirControlIncreased();
	
	float getSlamDamageCooldown();
	
	void setSlamDamageCooldown(int i);
	
	float getMaxNoSlowdownVelocity();
	
	void markDirty();
}
