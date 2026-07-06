package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.weapons.SharpshooterRevolverItem;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import net.minecraft.util.Identifier;

public class AlternateSharpshooterRevolverRenderer extends SharpshooterRevolverRenderer
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/item/alternate_sharpshooter");
	
	public AlternateSharpshooterRevolverRenderer()
	{
		super(new DefaultedItemGeoModel<SharpshooterRevolverItem>(Ultracraft.identifier("alternate_revolver"))
					  .withAltAnimations(Ultracraft.identifier("revolver")));
	}
	
	@Override
	public Identifier getTextureLocation(SharpshooterRevolverItem animatable)
	{
		return TEXTURE;
	}
}
