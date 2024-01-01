package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.rendering.TitleBGRenderer;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TravelScreen extends Screen
{
	public static final TitleBGRenderer BG = new TitleBGRenderer(new CubeMapRenderer(new Identifier(Ultracraft.MOD_ID, "aaa")));
	float openAnimTime, closeAnimTime;
	boolean shouldClose;
	TextRenderer textRenderer;
	
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
		addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.layer0"), b -> selectLayer(0))
								 .dimensions(width / 2 - 50, height / 2 - 60, 100, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.layer1"), b -> selectLayer(1))
								 .dimensions(width / 2 - 50, height / 2 - 30, 100, 20).build());
		ButtonWidget button;
		addDrawableChild(button = ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.layer2"), b -> selectLayer(1))
								 .dimensions(width / 2 - 50, height / 2, 100, 20).build());
		button.active = false;
		addDrawableChild(button = ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.layer3"), b -> selectLayer(1))
								 .dimensions(width / 2 - 50, height / 2 + 30, 100, 20).build());
		button.active = false;
		
		addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.close"), b -> shouldClose = true)
								 .dimensions(width / 2 - 50, height - 32, 100, 20).build());
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		if(openAnimTime < 1f)
			openAnimTime = Math.min(openAnimTime + delta / 10f, 1f);
		BG.setYOffset(-(1f - openAnimTime) * 5f + closeAnimTime * 5f);
		BG.render(delta, 1f);
		
		if(!shouldClose)
			context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.title"), width / 2, 16, 0xffffffff);
		if(openAnimTime == 1f && !shouldClose)
			super.render(context, mouseX, mouseY, delta);
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
		if(Layer.values()[layer].worldKey.equals(MinecraftClient.getInstance().world.getRegistryKey()))
			return;
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(layer);
		ClientPlayNetworking.send(PacketRegistry.TRAVEL_PACKET_ID, buf);
		UltracraftClient.setTravelling(true);
	}
	
	@Override
	public boolean shouldPause()
	{
		return !shouldClose;
	}
}
