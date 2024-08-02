package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.item.weapons.SawedOnShotgunItem;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.minecraft.client.MinecraftClient;

public class SawedOnShotgunModel extends DefaultedItemGeoModel<SawedOnShotgunItem>
{
	public SawedOnShotgunModel()
	{
		super(Ultracraft.identifier("shotgun"));
	}
	
	@Override
	public void setCustomAnimations(SawedOnShotgunItem animatable, long instanceId, AnimationState<SawedOnShotgunItem> animationState)
	{
		super.setCustomAnimations(animatable, instanceId, animationState);
		if(MinecraftClient.getInstance().player != null)
		{
			GunCooldownManager cdm = UltraComponents.WINGED.get(MinecraftClient.getInstance().player).getGunCooldownManager();
			CoreGeoBone saw = this.getAnimationProcessor().getBone("Chainsaw");
			saw.setHidden(!cdm.isUsable(animatable, GunCooldownManager.SECONDARY));
		}
	}
}
