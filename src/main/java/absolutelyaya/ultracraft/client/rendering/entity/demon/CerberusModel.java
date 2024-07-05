package absolutelyaya.ultracraft.client.rendering.entity.demon;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.demon.CerberusEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.constant.DataTickets;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.model.data.EntityModelData;

public class CerberusModel extends GeoModel<CerberusEntity>
{
	static final Identifier NORMAL = Ultracraft.texIdentifier("textures/entity/cerberus");
	static final Identifier CRACKED = Ultracraft.texIdentifier("textures/entity/cerberus_cracked");
	
	@Override
	public Identifier getModelResource(CerberusEntity object)
	{
		return Ultracraft.identifier("geo/entities/cerberus.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(CerberusEntity object)
	{
		return object.isCracked() ? CRACKED : NORMAL;
	}
	
	@Override
	public Identifier getAnimationResource(CerberusEntity animatable)
	{
		return Ultracraft.identifier("animations/entities/cerberus.animation.json");
	}
	
	@Override
	public void setCustomAnimations(CerberusEntity animatable, long instanceId, AnimationState<CerberusEntity> animationState)
	{
		super.setCustomAnimations(animatable, instanceId, animationState);
		CoreGeoBone head = this.getAnimationProcessor().getBone("head");
		
		float f = ((float) Math.PI / 180F);
		if(MinecraftClient.getInstance().isPaused())
			return;
		
		
		EntityModelData extraData = (EntityModelData)animationState.getExtraData().get(DataTickets.ENTITY_MODEL_DATA);
		if(head != null && animatable.getAnimation() != 1)
		{
			head.setRotX(head.getRotX() + extraData.headPitch() * f);
			head.setRotY(extraData.netHeadYaw() * f);
		}
	}
}
