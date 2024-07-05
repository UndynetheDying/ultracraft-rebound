package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.HellSpawnerItem;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class HellSpawnerItemRenderer extends GeoItemRenderer<HellSpawnerItem>
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/block/hell_spawner");
	
	public HellSpawnerItemRenderer()
	{
		super(new HellSpawnerItemModel());
	}
	
	@Override
	public Identifier getTextureLocation(HellSpawnerItem animatable)
	{
		return TEXTURE;
	}
}
