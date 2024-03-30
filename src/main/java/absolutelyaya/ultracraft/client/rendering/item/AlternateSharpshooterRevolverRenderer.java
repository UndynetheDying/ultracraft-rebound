package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.SharpshooterRevolverItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.minecraft.util.Identifier;

public class AlternateSharpshooterRevolverRenderer extends SharpshooterRevolverRenderer
{
	public AlternateSharpshooterRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<SharpshooterRevolverItem>(new Identifier(Ultracraft.MOD_ID, "alternate_revolver"))
					  .withAltAnimations(new Identifier(Ultracraft.MOD_ID, "revolver")));
	}
	
	@Override
	public Identifier getTextureLocation(SharpshooterRevolverItem animatable)
	{
		return new Identifier(Ultracraft.MOD_ID, "textures/item/alternate_sharpshooter.png");
	}
}
