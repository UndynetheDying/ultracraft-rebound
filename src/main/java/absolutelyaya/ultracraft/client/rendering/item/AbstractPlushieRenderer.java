package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.PlushieItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public abstract class AbstractPlushieRenderer<P extends PlushieItem & GeoAnimatable> extends GeoItemRenderer<P>
{
	static final Identifier V1 = Ultracraft.texIdentifier("textures/item/plushie/v1");
	static final Identifier YAYA = Ultracraft.texIdentifier("textures/item/plushie/yaya");
	static final Identifier HAKITA = Ultracraft.texIdentifier("textures/item/plushie/hakita");
	static final Identifier PITR = Ultracraft.texIdentifier("textures/item/plushie/pitr");
	static final Identifier SWORDSMACHINE = Ultracraft.texIdentifier("textures/item/plushie/swordsmachine");
	static final Identifier TUNDRA = Ultracraft.texIdentifier("textures/item/plushie/swordsmachine_tundra");
	static final Identifier AGONY = Ultracraft.texIdentifier("textures/item/plushie/swordsmachine_agony");
	static final Identifier TALON = Ultracraft.texIdentifier("textures/item/plushie/talon");
	static final Identifier V2 = Ultracraft.texIdentifier("textures/item/plushie/v2");
	static final Identifier ASHEN = Ultracraft.texIdentifier("textures/item/plushie/ashenwulf");
	
	public AbstractPlushieRenderer(DefaultedItemGeoModel<P> model)
	{
		super(model);
	}
	
	@Override
	public Identifier getTextureLocation(P animatable)
	{
		return switch(animatable.getType())
		{
			default -> V1;
			case YAYA -> YAYA;
			case HAKITA -> HAKITA;
			case PITR, PITRPOIN -> PITR;
			case SWORDSMACHINE -> SWORDSMACHINE;
			case SWORDSMACHINE_TUNDRA -> TUNDRA;
			case SWORDSMACHINE_AGONY -> AGONY;
			case TALON -> TALON;
			case V2 -> V2;
			case ASHEN -> ASHEN;
		};
	}
}
