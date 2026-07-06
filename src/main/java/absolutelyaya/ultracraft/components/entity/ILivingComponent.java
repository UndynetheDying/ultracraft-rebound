package absolutelyaya.ultracraft.components.entity;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public interface ILivingComponent extends ComponentV3, AutoSyncedComponent
{
	boolean isCancerous();
	
	void setCanerous(boolean v);
	
	boolean isEnraged();
	
	void setEnraged(boolean v);
	
	int getNails();
	
	void setNails(int nails);
	
	void incrementNails();
}
