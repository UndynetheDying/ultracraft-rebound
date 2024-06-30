package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.FlamethrowerItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class FlamethrowerRenderer extends GeoItemRenderer<FlamethrowerItem>
{
	static final Identifier TEXTURE = Ultracraft.identifier("textures/item/flamethrower.png");
	
	public FlamethrowerRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("flamethrower")));
	}
	
	@Override
	public Identifier getTextureLocation(FlamethrowerItem animatable)
	{
		return TEXTURE;
	}
}
