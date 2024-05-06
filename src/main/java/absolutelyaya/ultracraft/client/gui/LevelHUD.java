package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.client.sound.ModularLevelMusic;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.util.ColorUtil;
import absolutelyaya.ultracraft.util.TimeUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.joml.Vector3f;

import java.awt.*;

public class LevelHUD
{
	public static LevelHUD Instance;
	long[] rankRequirements = new long[0];
	long pb, ppb, last;
	float displayFinishedTimer, musicPopupTime, musicPopupDelay, musicPopupVisibility;
	byte curTimeRank;
	ModularLevelMusic musicPopup;
	
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
		
		if(musicPopupDelay > 0f)
		{
			musicPopupDelay = Math.max(musicPopupDelay - tickDelta / 20f, 0f);
			return;
		}
		
		if((musicPopupTime > 0f || musicPopupVisibility > 0f) && musicPopup != null)
			renderMusicPopup(context, tickDelta / 20f);
		else if(musicPopup != null)
			musicPopup = null;
		if(musicPopupTime > 0f && !MinecraftClient.getInstance().isPaused())
			musicPopupTime = Math.max(musicPopupTime - tickDelta / 20f, 0f);
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
	
	public void queueNewMusicPopup(ModularLevelMusic music)
	{
		musicPopupDelay = 2f;
		musicPopupTime = 10f;
		musicPopup = music;
		musicPopupVisibility = 0f;
	}
	
	void renderMusicPopup(DrawContext context, float delta)
	{
		RenderSystem.enableBlend();
		if(!MinecraftClient.getInstance().isPaused())
		{
			if(musicPopupTime > 0f && musicPopupVisibility < 1f)
				musicPopupVisibility = Math.min(musicPopupVisibility + delta, 1f);
			if(musicPopupTime == 0f && musicPopupVisibility > 0f)
				musicPopupVisibility = Math.max(musicPopupVisibility - delta, 0f);
		}
		TextRenderer tRenderer = MinecraftClient.getInstance().textRenderer;
		Text header = Text.translatable("message.ultracraft.announce-music");
		Text trackName = Text.translatable(musicPopup.getTrackName()).getWithStyle(Style.EMPTY.withBold(true)).get(0);
		Text author = Text.translatable("message.ultracraft.music-author-prefix", Text.translatable(musicPopup.getAuthor()));
		int minWidth = getHighest(tRenderer.getWidth(header), tRenderer.getWidth(trackName), tRenderer.getWidth(author), 128);
		context.setShaderColor(1f, 1f, 1f, musicPopupVisibility);
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(context.getScaledWindowWidth() - (minWidth + 8) * (musicPopupVisibility / 3f + 0.66f), 16, 0);
		context.fill(0, 0, minWidth + 8, 32, 0x88000000);
		context.drawText(tRenderer, header, 4, 2, 0xa0a0a0, true);
		context.drawText(tRenderer, trackName, 4, 2 + tRenderer.fontHeight, 0xffffff, true);
		context.drawText(tRenderer, author, 4, 2 + tRenderer.fontHeight * 2, 0xffffff, true);
		context.setShaderColor(1f, 1f, 1f, Math.min(musicPopupVisibility * 2, 1f));
		Vector3f col = ColorUtil.asVector3f(musicPopup.getColor(), true);
		if(musicPopupTime > 0f)
			col = col.lerp(new Vector3f(1f), 1f - musicPopupVisibility * musicPopupVisibility);
		context.fill(-2, 0, 0, 32, new Color(col.z, col.y, col.x).getRGB());
		matrices.pop();
		context.setShaderColor(1f, 1f, 1f, 1f);
	}
	
	int getHighest(int... args)
	{
		int highest = Integer.MIN_VALUE;
		for (int i : args)
			if(highest < i)
				highest = i;
		return highest;
	}
}
