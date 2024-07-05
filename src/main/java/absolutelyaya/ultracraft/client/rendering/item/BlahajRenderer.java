package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.BlahajItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;
import net.minecraft.util.Identifier;

public class BlahajRenderer extends GeoItemRenderer<BlahajItem>
{
	static final Identifier BLUE = Ultracraft.texIdentifier("textures/item/fish/blahaj_blue");
	static final Identifier PINK = Ultracraft.texIdentifier("textures/item/fish/blahaj_pink");
	
	public BlahajRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("blahaj")));
	}
	
	@Override
	public Identifier getTextureLocation(BlahajItem animatable)
	{
		return animatable.isRare() ? PINK : BLUE;
	}
}
