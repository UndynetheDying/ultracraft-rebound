package absolutelyaya.ultracraft.api;

import absolutelyaya.ultracraft.config.CybergrindConfig;
import net.minecraft.entity.EntityType;

@FunctionalInterface
public interface CybergrindInitializer
{
	/**
	 * Register your enemy spawn costs in here!<br>
	 * Entrypoint: "cybergrind"
	 * @see CybergrindConfig#registerCost(EntityType, int) 
	 */
	void registerEnemyCosts(CybergrindConfig config);
}
