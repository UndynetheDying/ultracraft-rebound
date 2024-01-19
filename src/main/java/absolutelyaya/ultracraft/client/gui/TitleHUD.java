package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
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
		typeSpeed = 60f; displayDuration = 5f; deleteSpeed = 100f;
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
			matrices.translate(width / 2f, height / 5f, 0);
			matrices.scale(3f, 3f, 3f);
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
			matrices.push();
			matrices.translate(0, height / 5f * 4f, 0);
			matrices.push();
			matrices.translate(width / 2f - widestLine / 2f, 0, 0);
			context.fill(-2, -2, widestLine + 2, renderer.getWrappedLinesHeight(boxText, maxWidth) + 2, 0x88000000);
			matrices.pop();
			for (int i = 0; i < lines.size(); i++)
				context.drawText(renderer, lines.get(i), width / 2 - renderer.getWidth(lines.get(i)) / 2, i * renderer.fontHeight, 0xffffff, false);
			matrices.pop();
			boxDuration -= delta;
		}
	}
	
	void setBigTitleInternal(Text text, float delay)
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
	
	public static void setBigTitle(PlayerEntity player, Text text, float delay)
	{
		if(!player.getWorld().isClient)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBoolean(true);
			buf.writeText(text);
			buf.writeFloat(delay);
			ServerPlayNetworking.send((ServerPlayerEntity)player, PacketRegistry.TITLE_PACKET_ID, buf);
		}
		else
			Instance.setBigTitleInternal(text, delay);
	}
	
	public static void setBigTitle(PlayerEntity player, Text text)
	{
		setBigTitle(player, text, 0f);
	}
	
	void setBoxTitleInternal(Text text, float duration)
	{
		boxText = text;
		boxDuration = duration;
	}
	
	public static void setBoxTitle(PlayerEntity player, Text text, float duration)
	{
		if(!player.getWorld().isClient)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeBoolean(false);
			buf.writeText(text);
			buf.writeFloat(duration);
			ServerPlayNetworking.send((ServerPlayerEntity)player, PacketRegistry.TITLE_PACKET_ID, buf);
		}
		else
		{
			Instance.setBoxTitleInternal(text, duration);
			player.playSound(SoundRegistry.RECEIVE_BOX_TITLE, 1f, 1f);
		}
	}
	
	public static void setBoxTitle(PlayerEntity player, Text text)
	{
		setBoxTitle(player, text, 30f);
	}
}
