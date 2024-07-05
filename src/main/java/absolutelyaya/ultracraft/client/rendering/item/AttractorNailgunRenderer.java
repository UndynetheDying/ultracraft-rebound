package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.AttractorNailgunItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class AttractorNailgunRenderer extends GeoItemRenderer<AttractorNailgunItem>
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/item/nailgun");
	
	public AttractorNailgunRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("nailgun")));
	}
	
	@Override
	public Identifier getTextureLocation(AttractorNailgunItem animatable)
	{
		return TEXTURE;
	}
}
