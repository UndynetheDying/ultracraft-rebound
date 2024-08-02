package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.item.HankItem;
import mod.azure.azurelib.model.DefaultedBlockGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class HankItemRenderer extends GeoItemRenderer<HankItem>
{
	public HankItemRenderer()
	{
		super(new DefaultedBlockGeoModel<>(Ultracraft.identifier("hank")));
	}
}
