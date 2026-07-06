package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.PlushieItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;

public class PoinPlushieRenderer extends AbstractPlushieRenderer<PlushieItem>
{
	public PoinPlushieRenderer()
	{
		super(new DefaultedItemGeoModel<PlushieItem>(Ultracraft.identifier("plushie"))
					  .withAltModel(Ultracraft.identifier("poin_plushie")));
	}
}
