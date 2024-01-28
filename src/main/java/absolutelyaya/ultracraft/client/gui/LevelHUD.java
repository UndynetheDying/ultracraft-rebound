package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.util.ColorUtil;
import absolutelyaya.ultracraft.util.TimeUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class LevelHUD
{
	public static LevelHUD Instance;
	long pb, par;
	
	public LevelHUD()
	{
		Instance = this;
	}
	
	public void render(DrawContext context, float tickDelta)
	{
		PlayerEntity player = MinecraftClient.getInstance().player;
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		if(winged.isTimerRunning())
			renderTimer(context, winged.getElapsedTimer());
	}
	
	public void initTimer(long pb, long par)
	{
		this.pb = pb;
		this.par = par;
	}
	
	void renderTimer(DrawContext context, long elapsedTimer)
	{
		TextRenderer tRenderer = MinecraftClient.getInstance().textRenderer;
		int width = context.getScaledWindowWidth();
		int col = 0xff888888;
		if(elapsedTimer < par)
			col = 0xfff47e1b;
		else if(elapsedTimer < pb)
			col = 0xfff4c71b;
		Text t = Text.of(TimeUtil.milliToString(elapsedTimer));
		context.drawText(tRenderer, t, (width - tRenderer.getWidth(t)) / 2, 32, col, true);
	}
}
