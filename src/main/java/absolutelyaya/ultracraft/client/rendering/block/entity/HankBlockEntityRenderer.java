package absolutelyaya.ultracraft.client.rendering.block.entity;

import absolutelyaya.ultracraft.block.HankBlockEntity;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.GeoBlockRenderer;
import mod.azure.azurelib.renderer.layer.BlockAndItemGeoLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.util.math.RotationAxis;

public class HankBlockEntityRenderer extends GeoBlockRenderer<HankBlockEntity>
{
	public HankBlockEntityRenderer()
	{
		super(new HankBlockEntityModel());
		
		addRenderLayer(new BlockAndItemGeoLayer<>(this)
		{
			@Override
			public ItemStack getStackForBone(GeoBone bone, HankBlockEntity animatable)
			{
				if(bone.getName().equals("head"))
					return animatable.getHeld();
				return super.getStackForBone(bone, animatable);
			}
			
			@Override
			public void renderStackForBone(MatrixStack matrices, GeoBone bone, ItemStack stack, HankBlockEntity animatable, VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay)
			{
				matrices.push();
				if(bone.getName().equals("head"))
				{
					if(stack.getItem() instanceof VerticallyAttachableBlockItem)
						matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
					else if(stack.getItem() instanceof BlockItem)
						matrices.scale(0.5f, 0.5f, 0.5f);
					matrices.translate(0f, 0.5f, 0f);
				}
				super.renderStackForBone(matrices, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
				matrices.pop();
			}
		});
	}
}
