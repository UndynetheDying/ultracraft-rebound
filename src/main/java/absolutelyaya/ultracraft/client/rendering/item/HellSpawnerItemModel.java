package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.HellSpawnerItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.GeoModel;

public class HellSpawnerItemModel extends GeoModel<HellSpawnerItem>
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/block/hell_spawner");
	
	@Override
	public Identifier getModelResource(HellSpawnerItem animatable)
	{
		return Ultracraft.identifier("geo/block/hell_spawner.geo.json");
	}
	
	@Override
	public Identifier getTextureResource(HellSpawnerItem animatable)
	{
		return TEXTURE;
	}
	
	@Override
	public Identifier getAnimationResource(HellSpawnerItem animatable)
	{
		return Ultracraft.identifier("animations/block/hell_spawner.animation.json");
	}
}
