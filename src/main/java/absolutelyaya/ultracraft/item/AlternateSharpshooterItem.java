package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.client.rendering.item.AlternateSharpshooterRevolverRenderer;
import mod.azure.azurelib.animatable.client.RenderProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;

import java.util.function.Consumer;

public class AlternateSharpshooterItem extends SharpshooterRevolverItem
{
	public AlternateSharpshooterItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider() {
			private AlternateSharpshooterRevolverRenderer renderer;
			
			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.renderer == null)
					this.renderer = new AlternateSharpshooterRevolverRenderer();
				
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
