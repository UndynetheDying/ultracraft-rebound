package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.PlushieItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;

public class SwordsmachinePlushieRenderer extends AbstractPlushieRenderer<PlushieItem>
{
	public SwordsmachinePlushieRenderer()
	{
		super(new DefaultedItemGeoModel<PlushieItem>(Ultracraft.identifier("plushie"))
					  .withAltModel(Ultracraft.identifier("swordsmachine_plushie"))
					  .withAltAnimations(Ultracraft.identifier("swordsmachine_plushie")));
	}
}
