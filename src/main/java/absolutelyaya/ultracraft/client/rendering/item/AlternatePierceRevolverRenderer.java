package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.PierceRevolverItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.minecraft.util.Identifier;

public class AlternatePierceRevolverRenderer extends PierceRevolverRenderer
{
	public AlternatePierceRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<PierceRevolverItem>(new Identifier(Ultracraft.MOD_ID, "alternate_revolver"))
					  .withAltAnimations(new Identifier(Ultracraft.MOD_ID, "revolver")));
	}
	
	@Override
	public Identifier getTextureLocation(PierceRevolverItem animatable)
	{
		return new Identifier(Ultracraft.MOD_ID, "textures/item/alternate_piercer.png");
	}
}
