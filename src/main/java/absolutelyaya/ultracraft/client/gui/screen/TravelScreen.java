package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.gui.TitleHUD;
import absolutelyaya.ultracraft.client.rendering.TitleBGRenderer;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class TravelScreen extends Screen
{
	static final Identifier TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/gui/travel.png");
	public static final TitleBGRenderer BG = new TitleBGRenderer(new CubeMapRenderer(new Identifier(Ultracraft.MOD_ID, "aaa")));
	float openAnimTime, closeAnimTime, blinkTimer;
	boolean shouldClose;
	TextRenderer textRenderer;
	List<ButtonWidget> buttons = new ArrayList<>();
	Layer curLayer;
	
	public TravelScreen(boolean closeImmediately)
	{
		super(Text.of("travel"));
		textRenderer = MinecraftClient.getInstance().textRenderer;
		shouldClose = closeImmediately;
	}
	
	@Override
	protected void init()
	{
		super.init();
		buttons.add(addDrawableChild(layerButton(0)));
		buttons.add(addDrawableChild(layerButton(1)));
		ButtonWidget button;
		buttons.add(addDrawableChild(button = layerButton(2)));
		button.active = false;
		buttons.add(addDrawableChild(button = layerButton(3)));
		button.active = false;
		
		buttons.add(addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.close"), b -> shouldClose = true)
								 .dimensions(width / 2 - 50, height - 32, 100, 20).build()));
		
		curLayer = Layer.fromRegistryKey(MinecraftClient.getInstance().world.getRegistryKey());
	}
	
	ButtonWidget layerButton(int layer)
	{
		return new TravelButton(width / 2 - 50, height / 2 + (layer - 2) * 30, 100, 20,
				Text.translatable("screen.ultracraft.travel.layer" + layer), b -> selectLayer(layer));
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		blinkTimer = (blinkTimer + delta / 20f) % 1f;
		if(openAnimTime < 1f)
		{
			openAnimTime = Math.min(openAnimTime + delta / 10f, 1f);
			buttons.forEach(b -> b.setAlpha(openAnimTime));
		}
		BG.setYOffset(-(1f - openAnimTime) * 5f + closeAnimTime * 5f);
		BG.render(delta, 1f);
		
		if(!shouldClose)
		{
			context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.title"), width / 2, 16, 0xffffffff);
			super.render(context, mouseX, mouseY, delta);
			if(blinkTimer < 0.5f)
				context.drawTexture(TEXTURE, width / 2 - 50 - 16, height / 2 + (curLayer.ordinal() - 2) * 30 + 6,
						0, 60, 11, 8, 128, 128);
		}
		if(shouldClose)
		{
			if(openAnimTime < 1f)
				openAnimTime = 1f;
			if(closeAnimTime < 1f)
				closeAnimTime = Math.min(closeAnimTime + delta / 10f, 1f);
			else
				close();
		}
	}
	
	@Override
	public boolean shouldCloseOnEsc()
	{
		return false;
	}
	
	void selectLayer(int layer)
	{
		shouldClose = true;
		if(Layer.values()[layer].worldKey.equals(curLayer.worldKey))
			return;
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(layer);
		ClientPlayNetworking.send(PacketRegistry.TRAVEL_PACKET_ID, buf);
		UltracraftClient.setTravelling(true);
		TitleHUD.setBigTitle(client.player, Text.translatable("title.entrance.layer-" + layer), 10f);
	}
	
	@Override
	public boolean shouldPause()
	{
		return !shouldClose;
	}
	
	static class TravelButton extends ButtonWidget
	{
		protected TravelButton(int x, int y, int width, int height, Text message, PressAction onPress)
		{
			super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		}
		
		@Override
		protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
		{
			MinecraftClient client = MinecraftClient.getInstance();
			TextRenderer renderer = client.textRenderer;
			float f = active ? 1f : 0.5f;
			context.setShaderColor(f, f, f, alpha);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			boolean hover = isHovered() && active;
			context.drawTexture(TEXTURE, getX(), getY(), 0, hover ? 20 : 0, getWidth(), getHeight(), 128, 128);
			context.setShaderColor(1f, 1f, 1f, 1f);
			context.drawText(renderer, getMessage(), getX() + (getWidth() - renderer.getWidth(getMessage())) / 2, getY() + (height) / 2 - 4,
					(MathHelper.ceil(alpha * 255f) << 24) + (hover ? 0xffffff : 0), false);
			if(!active)
				context.drawTexture(TEXTURE, getX(), getY(), 0, 40, getWidth(), getHeight(), 128, 128);
		}
	}
}
