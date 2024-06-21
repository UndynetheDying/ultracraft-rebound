package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.MarksmanRevolverItem;
import absolutelyaya.ultracraft.item.PierceRevolverItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.minecraft.util.Identifier;

public class AlternateMarksmanRevolverRenderer extends MarksmanRevolverRenderer
{
	public AlternateMarksmanRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<MarksmanRevolverItem>(Ultracraft.identifier("alternate_revolver"))
					  .withAltAnimations(Ultracraft.identifier("revolver")));
	}
	
	@Override
	public Identifier getTextureLocation(MarksmanRevolverItem animatable)
	{
		return Ultracraft.identifier("textures/item/alternate_marksman.png");
	}
}
