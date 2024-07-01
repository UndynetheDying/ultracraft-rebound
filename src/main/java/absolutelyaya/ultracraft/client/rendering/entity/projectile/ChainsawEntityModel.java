package absolutelyaya.ultracraft.client.rendering.entity.projectile;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.projectile.ChainsawEntity;
import absolutelyaya.ultracraft.entity.projectile.MagnetEntity;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;

public class ChainsawEntityModel extends GeoModel<ChainsawEntity>
{
	@Override
	public Identifier getModelResource(ChainsawEntity animatable)
	{
		return Ultracraft.identifier("geo/entities/chainsaw.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(ChainsawEntity animatable)
	{
		return Ultracraft.identifier("textures/entity/chainsaw" + (animatable.age % 4 > 1 ? 0 : 1) + ".png");
	}
	
	@Override
	public Identifier getAnimationResource(ChainsawEntity animatable)
	{
		return null;
	}
}
