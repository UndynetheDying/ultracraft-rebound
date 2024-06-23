package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.BlahajItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;
import net.minecraft.util.Identifier;

public class BlahajRenderer extends GeoItemRenderer<BlahajItem>
{
	public BlahajRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("blahaj")));
	}
	
	@Override
	public Identifier getTextureLocation(BlahajItem animatable)
	{
		return Ultracraft.identifier("textures/item/fish/blahaj_" + (animatable.isRare() ? "pink" : "blue") + ".png");
	}
}
