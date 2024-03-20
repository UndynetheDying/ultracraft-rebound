package absolutelyaya.ultracraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class TitleHUD
{
	public static TitleHUD Instance;
	boolean forceDelete;
	float typeSpeed = 60f, displayDuration = 5f, deleteSpeed = 100f, delay, boxDuration;
	String targetText = "", curText = "";
	Text boxText;
	
	public TitleHUD()
	{
		Instance = this;
	}
	
	public void render(DrawContext context, float tickDelta)
	{
		Screen screen = MinecraftClient.getInstance().currentScreen;
		if(screen != null && screen.shouldPause())
			return;
		int width = context.getScaledWindowWidth(), height = context.getScaledWindowHeight();
		MatrixStack matrices = context.getMatrices();
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		float delta = MinecraftClient.getInstance().getLastFrameDuration() / 10f;
		
		//big typer title update
		if(delay <= 0f)
		{
			int curLength = curText.length(), targetLength = targetText.length();
			if(curLength > 0 && (targetLength < curLength || forceDelete))
			{
				curText = curText.substring(0, curLength - 1);
				delay += 10f / deleteSpeed;
			}
			else if(targetLength > curLength)
			{
				if(forceDelete) //if this is reached, curString has to be empty
					forceDelete = false;
				curText = targetText.substring(0, curLength + 1);
				delay += 10f / typeSpeed;
			}
		}
		if(targetText.length() - curText.length() != 0 || delay > 0)
			delay -= delta;
		else if(targetText.length() > 0 && delay <= 0f)
		{
			forceDelete = true;
			delay = displayDuration;
			targetText = "";
		}
		//render big title
		if(curText.length() > 0)
		{
			matrices.push();
			matrices.translate(width / 2f, height / 5f, 10);
			matrices.scale(2f, 2f, 2f);
			RenderSystem.enableBlend();
			context.drawCenteredTextWithShadow(renderer, curText, 0, 0, 0xffffff);
			matrices.pop();
		}
		
		//render box
		if(boxDuration > 0f)
		{
			int maxWidth = width / 2, widestLine = 0;
			List<OrderedText> lines = renderer.wrapLines(boxText, maxWidth);
			for (OrderedText t : lines)
			{
				int w = renderer.getWidth(t);
				if(w > widestLine)
					widestLine = w;
			}
			int boxHeight = renderer.getWrappedLinesHeight(boxText, maxWidth);
			matrices.push();
			matrices.translate(0, height - 42 - boxHeight, -10);
			matrices.push();
			matrices.translate(width / 2f - widestLine / 2f, 0, 0);
			context.fill(-2, -2, widestLine + 1, boxHeight, 0x88000000);
			matrices.pop();
			for (int i = 0; i < lines.size(); i++)
				context.drawText(renderer, lines.get(i), width / 2 - renderer.getWidth(lines.get(i)) / 2, i * renderer.fontHeight, 0xffffff, false);
			matrices.pop();
			boxDuration -= delta;
		}
	}
	
	public void setBigTitle(Text text, float delay)
	{
		if(curText.length() > 0 || this.delay > 0f)
		{
			forceDelete = true;
			this.delay = 0f;
		}
		else
			this.delay = delay;
		targetText = text.getString();
	}
	
	public void setBoxTitle(Text text, float duration)
	{
		boxText = text;
		boxDuration = duration;
	}
}
