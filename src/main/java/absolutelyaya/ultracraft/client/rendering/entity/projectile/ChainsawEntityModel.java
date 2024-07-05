package absolutelyaya.ultracraft.client.rendering.entity.projectile;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.projectile.ChainsawEntity;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;

public class ChainsawEntityModel extends GeoModel<ChainsawEntity>
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/entity/chainsaw0");
	static final Identifier TEXTURE1 = Ultracraft.texIdentifier("textures/entity/chainsaw1");
	
	@Override
	public Identifier getModelResource(ChainsawEntity animatable)
	{
		return Ultracraft.identifier("geo/entities/chainsaw.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(ChainsawEntity animatable)
	{
		return animatable.age % 4 > 1 ? TEXTURE : TEXTURE1;
	}
	
	@Override
	public Identifier getAnimationResource(ChainsawEntity animatable)
	{
		return null;
	}
}
