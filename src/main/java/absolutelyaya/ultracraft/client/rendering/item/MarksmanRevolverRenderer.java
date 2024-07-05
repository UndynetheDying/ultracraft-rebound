package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.MarksmanRevolverItem;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class MarksmanRevolverRenderer extends GeoItemRenderer<MarksmanRevolverItem>
{
	public MarksmanRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("revolver")));
	}
	
	public MarksmanRevolverRenderer(GeoModel<MarksmanRevolverItem> model)
	{
		super(model);
	}
	
	@Override
	public Identifier getTextureLocation(MarksmanRevolverItem animatable)
	{
		String tex = "textures/item/marksman_revolver";
		int coins = animatable.getNbt(currentItemStack, "coins");
		
		if (coins == 3)
			return Ultracraft.texIdentifier(tex + 3);
		else if (coins == 2)
			return Ultracraft.texIdentifier(tex + 2);
		else if (coins == 1)
			return Ultracraft.texIdentifier(tex + 1);
		else if (coins == 0)
			return Ultracraft.texIdentifier(tex + 0);
		
		return Ultracraft.texIdentifier(tex);
	}
}
