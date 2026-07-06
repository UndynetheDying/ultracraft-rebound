package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.MachineSwordItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class MachineSwordRenderer extends GeoItemRenderer<MachineSwordItem>
{
	public MachineSwordRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("machinesword")));
	}
	
	@Override
	public Identifier getTextureLocation(MachineSwordItem animatable)
	{
		return switch(MachineSwordItem.getType(getCurrentItemStack()))
		{
			case NORMAL -> Ultracraft.texIdentifier("textures/item/machinesword");
			case TUNDRA -> Ultracraft.texIdentifier("textures/item/machinesword_tundra");
			case AGONY -> Ultracraft.texIdentifier("textures/item/machinesword_agony");
		};
	}
}
