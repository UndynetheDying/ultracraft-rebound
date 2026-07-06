package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.PierceRevolverItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.minecraft.util.Identifier;

public class AlternatePierceRevolverRenderer extends PierceRevolverRenderer
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/item/alternate_piercer");
	
	public AlternatePierceRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<PierceRevolverItem>(Ultracraft.identifier("alternate_revolver"))
					  .withAltAnimations(Ultracraft.identifier("revolver")));
	}
	
	@Override
	public Identifier getTextureLocation(PierceRevolverItem animatable)
	{
		return TEXTURE;
	}
}
