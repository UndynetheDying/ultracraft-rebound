package absolutelyaya.ultracraft.api;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.config.CybergrindConfig;
import absolutelyaya.ultracraft.registry.EntityRegistry;

public class CoreCybergrindInitializer implements CybergrindInitializer
{
	@Override
	public void registerEnemyCosts(CybergrindConfig config)
	{
		config.registerCost(EntityRegistry.FILTH, 1, Layer.OVERWORLD);
		config.registerCost(EntityRegistry.STRAY, 2, Layer.OVERWORLD);
		config.registerCost(EntityRegistry.SCHISM, 4, Layer.OVERWORLD);
		config.registerCost(EntityRegistry.SWORDSMACHINE, 12, Layer.OVERWORLD);
		config.registerCost(EntityRegistry.DRONE, 2, Layer.LIMBO);
		config.registerCost(EntityRegistry.STREET_CLEANER, 6, Layer.LIMBO);
		config.registerCost(EntityRegistry.MALICIOUS_FACE, 8, Layer.OVERWORLD);
		config.registerCost(EntityRegistry.CERBERUS, 10, Layer.OVERWORLD);
		config.registerCost(EntityRegistry.HIDEOUS_MASS, 24, Layer.LIMBO);
	}
}
