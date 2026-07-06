package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.DroneMaskItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class DroneMaskRenderer extends GeoItemRenderer<DroneMaskItem>
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/item/drone_mask");
	
	public DroneMaskRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("drone_mask")));
	}
	
	@Override
	public Identifier getTextureLocation(DroneMaskItem animatable)
	{
		return TEXTURE;
	}
}
