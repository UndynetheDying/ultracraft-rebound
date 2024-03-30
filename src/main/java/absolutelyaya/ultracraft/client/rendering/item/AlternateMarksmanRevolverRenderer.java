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
		super(new DefaultedItemGeoModel<MarksmanRevolverItem>(new Identifier(Ultracraft.MOD_ID, "alternate_revolver"))
					  .withAltAnimations(new Identifier(Ultracraft.MOD_ID, "revolver")));
	}
	
	@Override
	public Identifier getTextureLocation(MarksmanRevolverItem animatable)
	{
		return new Identifier(Ultracraft.MOD_ID, "textures/item/alternate_marksman.png");
	}
}
