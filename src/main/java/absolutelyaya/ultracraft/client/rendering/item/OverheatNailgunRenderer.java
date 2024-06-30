package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.OverheatNailgunItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;
import net.minecraft.util.Identifier;

public class OverheatNailgunRenderer extends GeoItemRenderer<OverheatNailgunItem>
{
	static final Identifier TEXTURE = Ultracraft.identifier("textures/item/overheat_nailgun.png");
	
	public OverheatNailgunRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("nailgun")));
	}
	
	@Override
	public Identifier getTextureLocation(OverheatNailgunItem animatable)
	{
		return TEXTURE;
	}
}
