package absolutelyaya.ultracraft.client.rendering.entity.demon;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.demon.RodentEntity;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.GeoModel;

public class RodentModel extends GeoModel<RodentEntity>
{
	@Override
	public Identifier getModelResource(RodentEntity animatable)
	{
		return Ultracraft.identifier("geo/entities/rodent.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(RodentEntity animatable)
	{
		return Ultracraft.identifier("textures/entity/rodent.png");
	}
	
	@Override
	public Identifier getAnimationResource(RodentEntity animatable)
	{
		return Ultracraft.identifier("animations/entities/rodent.animation.json");
	}
}
