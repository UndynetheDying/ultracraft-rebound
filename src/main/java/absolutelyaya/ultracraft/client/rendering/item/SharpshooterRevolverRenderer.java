package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.SharpshooterRevolverItem;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class SharpshooterRevolverRenderer extends GeoItemRenderer<SharpshooterRevolverItem>
{
	public SharpshooterRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("revolver")));
	}
	
	public SharpshooterRevolverRenderer(GeoModel<SharpshooterRevolverItem> model)
	{
		super(model);
	}
	
	@Override
	public Identifier getTextureLocation(SharpshooterRevolverItem animatable)
	{
		int charges = animatable.getNbt(currentItemStack, "charges");
		
		return switch (charges)
		{
			case 0 -> Ultracraft.texIdentifier("textures/item/sharpshooter_revolver2");
			case 1 -> Ultracraft.texIdentifier("textures/item/sharpshooter_revolver1");
			case 2 -> Ultracraft.texIdentifier("textures/item/sharpshooter_revolver0");
			default -> Ultracraft.texIdentifier("textures/item/sharpshooter_revolver");
		};
	}
}
