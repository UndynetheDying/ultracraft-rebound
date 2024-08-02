package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.HarpoonGunItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class HarpoonGunRenderer extends GeoItemRenderer<HarpoonGunItem>
{
	public HarpoonGunRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("harpoon_gun")));
	}
	
	@Override
	public Identifier getTextureLocation(HarpoonGunItem animatable)
	{
		return switch(animatable.texture) {
			default -> Ultracraft.texIdentifier("textures/item/harpoon_gun");
			case 1 -> Ultracraft.texIdentifier("textures/item/harpoon_gun0");
			case 2 -> Ultracraft.texIdentifier("textures/item/harpoon_gun1");
			case 3 -> Ultracraft.texIdentifier("textures/item/harpoon_gun2");
		};
	}
}
