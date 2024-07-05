package absolutelyaya.ultracraft.client.rendering.block.entity;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.HankBlockEntity;
import mod.azure.azurelib.model.DefaultedBlockGeoModel;
import net.minecraft.util.Identifier;

public class HankBlockEntityModel extends DefaultedBlockGeoModel<HankBlockEntity>
{
	public HankBlockEntityModel()
	{
		super(Ultracraft.identifier("hank"));
	}
	
	@Override
	public Identifier getTextureResource(HankBlockEntity animatable)
	{
		return Ultracraft.texIdentifier("textures/block/hank");
	}
}
