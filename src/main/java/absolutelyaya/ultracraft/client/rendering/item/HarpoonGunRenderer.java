package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.HarpoonGunItem;
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
			default -> Ultracraft.identifier("textures/item/harpoon_gun.png");
			case 1 -> Ultracraft.identifier("textures/item/harpoon_gun0.png");
			case 2 -> Ultracraft.identifier("textures/item/harpoon_gun1.png");
			case 3 -> Ultracraft.identifier("textures/item/harpoon_gun2.png");
		};
	}
}
