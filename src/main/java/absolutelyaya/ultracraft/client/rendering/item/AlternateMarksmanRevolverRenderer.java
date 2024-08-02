package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.MarksmanRevolverItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.minecraft.util.Identifier;

public class AlternateMarksmanRevolverRenderer extends MarksmanRevolverRenderer
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/item/alternate_marksman");
	
	public AlternateMarksmanRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<MarksmanRevolverItem>(Ultracraft.identifier("alternate_revolver"))
					  .withAltAnimations(Ultracraft.identifier("revolver")));
	}
	
	@Override
	public Identifier getTextureLocation(MarksmanRevolverItem animatable)
	{
		return TEXTURE;
	}
}
