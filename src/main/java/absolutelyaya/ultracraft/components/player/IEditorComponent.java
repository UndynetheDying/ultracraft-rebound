package absolutelyaya.ultracraft.components.player;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public interface IEditorComponent extends ComponentV3, AutoSyncedComponent
{
	void toggleEditMode();
	
	boolean isActive();
	
	void setActive(boolean state);
	
	void setEditFocus(String key, BlockPos pos);
	
	BlockPos getEditFocus(String key);
	
	HashMap<String, BlockPos> getEditFocus();
	
	void clearEditFocus();
	
	void clearEditFocus(String key);
	
	void setEditAreaStep(int i);
	
	void setEditAreaCore(BlockPos pos);
	
	int getEditAreaStep();
	
	ActionResult useBlock(PlayerEntity player, BlockPos pos);
	
	void setRebindingParent(BlockPos pos);
	
	BlockPos getRebindingParent();
	
	boolean isShowAreaOwner();
	
	void setShowAreaOwner(boolean v);
	
	boolean toggleShowAreaOwner();
	
	boolean isNoClip();
	
	void setNoClip(boolean v);
	
	boolean toggleNoClip();
	
	float getFlySpeed();
	
	void setFlySpeed(float v);
	
	void setGhost(boolean v);
	
	boolean isGhost();
	
	boolean toggleGhost();
	
	void sync();
}
