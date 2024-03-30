package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.util.TimeUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public class LevelHUD
{
	public static LevelHUD Instance;
	long[] rankRequirements = new long[0];
	long pb, ppb, last;
	float displayFinishedTimer;
	byte curTimeRank;
	
	public LevelHUD()
	{
		Instance = this;
	}
	
	public void render(DrawContext context, float tickDelta)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(client.player);
		boolean gamePaused = client.isPaused();
		if(gamePaused != levelStats.isTimerPaused())
			levelStats.setTimerPaused(gamePaused);
		if(levelStats.isTimerRunning())
			renderTimer(context, levelStats.getElapsedTimer());
		else if(displayFinishedTimer > 0)
			renderTimer(context, last);
		if(displayFinishedTimer > 0)
			displayFinishedTimer -= client.getLastFrameDuration() / 30f;
	}
	
	public void initTimer(Pair<Long, Long> pb, long[] rankRequirements)
	{
		this.pb = pb.getLeft();
		this.ppb = pb.getRight();
		if(rankRequirements != null)
			this.rankRequirements = rankRequirements;
		curTimeRank = 0;
	}
	
	public void stopTimer()
	{
		displayFinishedTimer += 10f;
		ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(MinecraftClient.getInstance().player);
		last = levelStats.getElapsedTimer();
	}
	
	void renderTimer(DrawContext context, long elapsedTimer)
	{
		TextRenderer tRenderer = MinecraftClient.getInstance().textRenderer;
		int width = context.getScaledWindowWidth();
		if(displayFinishedTimer > 0)
		{
			MutableText t = Text.of(TimeUtil.milliToString(elapsedTimer)).copy();
			if(rankRequirements.length > 0)
				t.append(" ").append(Text.translatable("screen.ultracraft.timer.rank" + curTimeRank));
			if(elapsedTimer < pb)
				t.append(" ").append(Text.translatable("screen.ultracraft.timer.best"));
			RenderSystem.setShaderColor(1f, 1f, 1f, Math.min(1f, displayFinishedTimer));
			context.drawText(tRenderer, t, (width - tRenderer.getWidth(t)) / 2, 32, 0xffffffff, true);
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		}
		else
		{
			int col = 0xff888888;
			if(rankRequirements.length > 0 && elapsedTimer < rankRequirements[0])
				col = 0xfff47e1b;
			else if(elapsedTimer < pb || pb == -1)
				col = 0xfff4c71b;
			MutableText t = Text.of(TimeUtil.milliToString(elapsedTimer)).copy();
			if(rankRequirements.length > 0)
				t.append(" ").append(Text.translatable("screen.ultracraft.timer.rank" + curTimeRank));
			context.drawText(tRenderer, t, (width - tRenderer.getWidth(t)) / 2, 32, col, true);
		}
		if(curTimeRank < rankRequirements.length && elapsedTimer > rankRequirements[curTimeRank])
			curTimeRank++;
	}
}
