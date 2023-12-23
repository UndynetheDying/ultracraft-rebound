package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.client.rendering.item.AlternatePierceRevolverRenderer;
import mod.azure.azurelib.animatable.client.RenderProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;

import java.util.function.Consumer;

public class AlternatePiercerItem extends PierceRevolverItem
{
	public AlternatePiercerItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider() {
			private AlternatePierceRevolverRenderer renderer;
			
			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.renderer == null)
					this.renderer = new AlternatePierceRevolverRenderer();
				
				return renderer;
			}
		});
	}
	
	@Override
	protected boolean isAlternate()
	{
		return true;
	}
}
