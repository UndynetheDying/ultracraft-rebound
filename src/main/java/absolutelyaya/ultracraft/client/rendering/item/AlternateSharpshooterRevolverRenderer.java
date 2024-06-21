package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.SharpshooterRevolverItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.minecraft.util.Identifier;

public class AlternateSharpshooterRevolverRenderer extends SharpshooterRevolverRenderer
{
	public AlternateSharpshooterRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<SharpshooterRevolverItem>(Ultracraft.identifier("alternate_revolver"))
					  .withAltAnimations(Ultracraft.identifier("revolver")));
	}
	
	@Override
	public Identifier getTextureLocation(SharpshooterRevolverItem animatable)
	{
		return Ultracraft.identifier("textures/item/alternate_sharpshooter.png");
	}
}
