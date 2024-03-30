package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.AttractorNailgunItem;
import absolutelyaya.ultracraft.item.OverheatNailgunItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;
import net.minecraft.util.Identifier;

public class OverheatNailgunRenderer extends GeoItemRenderer<OverheatNailgunItem>
{
	static final Identifier TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/item/overheat_nailgun.png");
	
	public OverheatNailgunRenderer()
	{
		super(new DefaultedItemGeoModel<>(new Identifier(Ultracraft.MOD_ID, "nailgun")));
	}
	
	@Override
	public Identifier getTextureLocation(OverheatNailgunItem animatable)
	{
		return TEXTURE;
	}
}
