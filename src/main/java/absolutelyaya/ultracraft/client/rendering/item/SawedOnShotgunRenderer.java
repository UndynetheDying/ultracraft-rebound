package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.item.weapons.SawedOnShotgunItem;
import mod.azure.azurelib.renderer.GeoItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Random;

public class SawedOnShotgunRenderer extends GeoItemRenderer<SawedOnShotgunItem>
{
	final Random random = new Random();
	
	public SawedOnShotgunRenderer()
	{
		super(new SawedOnShotgunModel());
	}
	
	@Override
	public Identifier getTextureLocation(SawedOnShotgunItem animatable)
	{
		float useTime = 1f - (animatable.getMaxUseTime(null) - animatable.getApproxUseTime()) / (float)(animatable.getMaxUseTime(null));
		String tex = "textures/item/saw_shotgun";
		
		PlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null)
			return Ultracraft.texIdentifier(tex);
		
		GunCooldownManager cdm = UltraComponents.WINGED.get(player).getGunCooldownManager();
		float primaryCD = cdm.getCooldownPercent(animatable, GunCooldownManager.PRIMARY);
		if(primaryCD > 0f)
		{
			if(primaryCD < 0.4f)
				tex += 2;
			else if(primaryCD < 0.5f)
				tex += 1;
			else if(primaryCD < 0.65f)
				tex += 0;
			return Ultracraft.texIdentifier(tex);
		}
		float secondaryCD = cdm.getCooldownPercent(animatable, GunCooldownManager.SECONDARY);
		if(secondaryCD > 0f)
		{
			if(secondaryCD < 0.25f)
				tex += 6;
			else if(secondaryCD < 0.5f)
				tex += 5;
			else if(secondaryCD < 0.75f)
				tex += 4;
			else if(secondaryCD < 1f)
				tex += 3;
			return Ultracraft.texIdentifier(tex);
		}
		
		if(useTime < 0.99f)
		{
			if(useTime > 0.79f)
				tex += 6;
			else if(useTime > 0.59f)
				tex += 5;
			else if(useTime > 0.39f)
				tex += 4;
			else if(useTime > 0f)
				tex += 3;
		}
		return Ultracraft.texIdentifier(tex);
	}
	
	@Override
	public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay)
	{
		if(!transformType.isFirstPerson())
		{
			super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
			return;
		}
		
		poseStack.push();
		float useTime = 1f - (stack.getItem().getMaxUseTime(stack) - ((SawedOnShotgunItem)stack.getItem()).getApproxUseTime()) / (float)(stack.getItem().getMaxUseTime(stack));
		float f = UltracraftClient.getConfig().safeVFX ? 0.01f : 0.025f;
		if(useTime > 0)
			poseStack.translate((random.nextFloat() - 0.5f) * f, (random.nextFloat() - 0.5f) * f, (random.nextFloat() - 0.5f) * f);
		super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
		poseStack.pop();
	}
}
