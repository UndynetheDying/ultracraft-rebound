package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.util.TimeUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class LevelHUD
{
	public static LevelHUD Instance;
	long pb, par, last;
	float displayFinishedTimer;
	
	public LevelHUD()
	{
		Instance = this;
	}
	
	public void render(DrawContext context, float tickDelta)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(MinecraftClient.getInstance().player);
		if(winged.isTimerRunning())
			renderTimer(context, winged.getElapsedTimer());
		else if(displayFinishedTimer > 0)
			renderTimer(context, last);
		if(displayFinishedTimer > 0)
			displayFinishedTimer -= MinecraftClient.getInstance().getLastFrameDuration() / 30f;
	}
	
	public void initTimer(long pb, long par)
	{
		this.pb = pb;
		this.par = par;
	}
	
	public void stopTimer()
	{
		displayFinishedTimer += 10f;
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(MinecraftClient.getInstance().player);
		last = winged.getElapsedTimer();
	}
	
	void renderTimer(DrawContext context, long elapsedTimer)
	{
		TextRenderer tRenderer = MinecraftClient.getInstance().textRenderer;
		int width = context.getScaledWindowWidth();
		if(displayFinishedTimer > 0)
		{
			MutableText t = Text.of(TimeUtil.milliToString(elapsedTimer)).copy();
			if(elapsedTimer < par)
			{
				IWingedPlayerComponent winged = UltraComponents.WINGED.get(MinecraftClient.getInstance().player);
				if(winged.isPerfect())
					t.append(Text.translatable("screen.ultracraft.timer.perfect"));
				else
					t.append(Text.translatable("screen.ultracraft.timer.imperfect"));
			}
			if(elapsedTimer < pb)
				t.append(Text.translatable("screen.ultracraft.timer.best"));
			RenderSystem.setShaderColor(1f, 1f, 1f, Math.min(1f, displayFinishedTimer));
			context.drawText(tRenderer, t, (width - tRenderer.getWidth(t)) / 2, 32, 0xffffffff, true);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		}
		else
		{
			int col = 0xff888888;
			if(elapsedTimer < par)
				col = 0xfff47e1b;
			else if(elapsedTimer < pb)
				col = 0xfff4c71b;
			Text t = Text.of(TimeUtil.milliToString(elapsedTimer));
			context.drawText(tRenderer, t, (width - tRenderer.getWidth(t)) / 2, 32, col, true);
		}
	}
}
