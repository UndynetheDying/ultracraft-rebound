package absolutelyaya.ultracraft.accessor;

import absolutelyaya.ultracraft.block.TerminalBlockEntity;
import absolutelyaya.ultracraft.config.HivelConfig;
import absolutelyaya.ultracraft.entity.other.BackTank;
import net.minecraft.util.math.Vec3d;

public interface WingedPlayerEntity
{
	void initMovementConfig(HivelConfig config);
	
	Vec3d[] getWingPose();
	
	void setWingPose(Vec3d[] pose);
	
	void startSlam();
	
	void endSlam(boolean strong);
	
	Vec3d getSlideDir();
	
	void updateSpeedConfig();
	
	void updateSpeedConfig(boolean wingsActive);
	
	void setFocusedTerminal(TerminalBlockEntity terminal);
	
	TerminalBlockEntity getFocusedTerminal();
	
	boolean isOpped();
	
	void setBackTank(BackTank tank);
	
	BackTank getBacktank();
	
	float getScreenShake();
	
	void addScreenshake(float val);
	
	boolean isSliding();
	
	void setSliding(boolean v);
	
	boolean isSlamming();
	
	void setSlamming(boolean v);
}
