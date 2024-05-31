package absolutelyaya.ultracraft.client.rendering.entity.husk;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.husk.SchismEntity;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.GeoModel;

public class SchismModel extends GeoModel<SchismEntity>
{
	@Override
	public Identifier getModelResource(SchismEntity object)
	{
		return Ultracraft.identifier("geo/entities/schism.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(SchismEntity object)
	{
		return Ultracraft.identifier("textures/entity/schism.png");
	}
	
	@Override
	public Identifier getAnimationResource(SchismEntity animatable)
	{
		return Ultracraft.identifier("animations/entities/schism.animation.json");
	}
}
