package absolutelyaya.ultracraft.client.rendering.entity.machine;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.machine.StreetCleanerEntity;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.GeoModel;

public class StreetCleanerEntityModel extends GeoModel<StreetCleanerEntity>
{
	@Override
	public Identifier getModelResource(StreetCleanerEntity animatable)
	{
		return Ultracraft.identifier("geo/entities/streetcleaner.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(StreetCleanerEntity animatable)
	{
		return Ultracraft.identifier("textures/entity/streetcleaner.png");
	}
	
	@Override
	public Identifier getAnimationResource(StreetCleanerEntity animatable)
	{
		return Ultracraft.identifier("animations/entities/streetcleaner.animation.json");
	}
}
