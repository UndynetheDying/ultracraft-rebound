package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.JumpstartNailgunItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;
import net.minecraft.util.Identifier;

public class JumpstartNailgunRenderer extends GeoItemRenderer<JumpstartNailgunItem>
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/item/jumpstart_nailgun");
	
	public JumpstartNailgunRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("nailgun")));
	}
	
	@Override
	public Identifier getTextureLocation(JumpstartNailgunItem animatable)
	{
		return TEXTURE;
	}
}
