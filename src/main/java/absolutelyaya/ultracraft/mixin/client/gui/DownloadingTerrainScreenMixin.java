package absolutelyaya.ultracraft.mixin.client.gui;

import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.gui.screen.TravelScreen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DownloadingTerrainScreen.class)
public abstract class DownloadingTerrainScreenMixin extends Screen
{
	protected DownloadingTerrainScreenMixin(Text title)
	{
		super(title);
	}
	
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/DownloadingTerrainScreen;renderBackgroundTexture(Lnet/minecraft/client/gui/DrawContext;)V"))
	void redirectRenderBackground(DownloadingTerrainScreen instance, DrawContext context, Operation<Void> original)
	{
		if(UltracraftClient.isTravelling())
			TravelScreen.BG.render(client.getLastFrameDuration(), 1f);
		else
			original.call(instance, context);
	}
}
