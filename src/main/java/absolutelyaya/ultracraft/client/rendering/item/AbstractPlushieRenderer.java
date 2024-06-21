package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.PlushieItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public abstract class AbstractPlushieRenderer<P extends PlushieItem & GeoAnimatable> extends GeoItemRenderer<P>
{
	public AbstractPlushieRenderer(DefaultedItemGeoModel<P> model)
	{
		super(model);
	}
	
	@Override
	public Identifier getTextureLocation(P animatable)
	{
		return switch(animatable.getType())
		{
			default -> Ultracraft.identifier("textures/item/plushie/v1.png");
			case YAYA -> Ultracraft.identifier("textures/item/plushie/yaya.png");
			case HAKITA -> Ultracraft.identifier("textures/item/plushie/hakita.png");
			case PITR, PITRPOIN -> Ultracraft.identifier("textures/item/plushie/pitr.png");
			case SWORDSMACHINE -> Ultracraft.identifier("textures/item/plushie/swordsmachine.png");
			case SWORDSMACHINE_TUNDRA -> Ultracraft.identifier("textures/item/plushie/swordsmachine_tundra.png");
			case SWORDSMACHINE_AGONY -> Ultracraft.identifier("textures/item/plushie/swordsmachine_agony.png");
			case TALON -> Ultracraft.identifier("textures/item/plushie/talon.png");
			case V2 -> Ultracraft.identifier("textures/item/plushie/v2.png");
			case ASHEN -> Ultracraft.identifier("textures/item/plushie/ashenwulf.png");
		};
	}
}
