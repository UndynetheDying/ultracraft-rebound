package absolutelyaya.ultracraft.components.world;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

import java.util.Map;

public interface IDimensionDataComponent extends ComponentV3
{
	boolean isFixedStructuresPlaced();
	
	void setFixedStructuresPlaced(boolean b);
	
	Map<String, Integer> getAllFlags();
	
	int getFlag(String id);
	
	void setFlag(String id, int value);
}
