package absolutelyaya.ultracraft.mixin.client.gui;

import absolutelyaya.ultracraft.client.gui.screen.IntroScreen;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AccessibilityOnboardingScreen.class)
public abstract class AccessibilityScreenMixin
{
	@Shadow protected abstract void setScreen(Screen screen);
	
	@Inject(method = "close", at = @At("HEAD"), cancellable = true)
	void onClose(CallbackInfo ci)
	{
		IntroScreen intro = new IntroScreen();
		setScreen(intro);
		IntroScreen.RESOURCES_LOADED = true;
		intro.resourceLoadFinished();
		ci.cancel();
	}
}
