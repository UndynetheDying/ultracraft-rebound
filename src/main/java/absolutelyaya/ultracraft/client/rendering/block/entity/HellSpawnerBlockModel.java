package absolutelyaya.ultracraft.client.rendering.block.entity;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.HellSpawnerBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedBlockGeoModel;

public class HellSpawnerBlockModel extends DefaultedBlockGeoModel<HellSpawnerBlockEntity>
{
	public HellSpawnerBlockModel()
	{
		super(Ultracraft.identifier("hell_spawner"));
	}
	
	@Override
	public Identifier getTextureResource(HellSpawnerBlockEntity animatable)
	{
		return Ultracraft.identifier("textures/block/hell_spawner.png");
	}
	
	@Override
	public RenderLayer getRenderType(HellSpawnerBlockEntity animatable, Identifier texture)
	{
		return RenderLayer.getEntityCutout(getTextureResource(animatable));
	}
}
