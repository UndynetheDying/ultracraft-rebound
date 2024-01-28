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

public abstract class AbstractTravelScreen extends Screen
{
	protected static final Identifier TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/gui/travel.png");
	public static final TitleBGRenderer BG = new TitleBGRenderer(new CubeMapRenderer(new Identifier(Ultracraft.MOD_ID, "aaa")));
	protected float openAnimTime, closeAnimTime;
	protected boolean shouldClose;
	protected Layer curLayer, selectedLayer;
	
	protected AbstractTravelScreen(Text title)
	{
		super(title);
	}
	
	@Override
	protected void init()
	{
		super.init();
		curLayer = Layer.fromRegistryKey(MinecraftClient.getInstance().world.getRegistryKey());
		ClientPlayNetworking.send(PacketRegistry.REQUEST_DESTINATIONS_PACKET_ID, new PacketByteBuf(Unpooled.buffer()));
	}
	
	protected ButtonWidget layerButton(int layer)
	{
		return new LayerButton(width / 2 - 50, height / 2 + (layer - 2) * 30, 100, 20,
				Text.translatable("screen.ultracraft.travel.layer" + layer), b -> selectLayer(layer));
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
		BG.setYOffset(-(1f - openAnimTime) * 5f + closeAnimTime * 5f);
		BG.render(delta, 1f);
		
		if(shouldClose)
		{
			if(openAnimTime < 1f)
				openAnimTime = 1f;
			if(closeAnimTime < 1f)
				closeAnimTime = Math.min(closeAnimTime + delta / 10f, 1f);
			else
				close();
		}
		if(openAnimTime < 1f)
			openAnimTime = Math.min(openAnimTime + delta / 10f, 1f);
	}
	
	protected void travel(Layer layer)
	{
		shouldClose = true;
		if(curLayer != null && layer.worldKey.equals(curLayer.worldKey))
			return;
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(layer.ordinal());
		ClientPlayNetworking.send(PacketRegistry.TRAVEL_PACKET_ID, buf);
		UltracraftClient.setTravelling(true);
		TitleHUD.Instance.setBigTitle(Text.translatable("title.entrance.layer-" + layer.ordinal()), 10f);
	}
	
	protected void enterLevel(Identifier id)
	{
		shouldClose = true;
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(id);
		ClientPlayNetworking.send(PacketRegistry.ENTER_LEVEL_PACKET_ID, buf);
	}
	
	protected void selectLayer(int layer)
	{
		selectedLayer =	Layer.values()[layer];
	}
	
	@Override
	public boolean shouldPause()
	{
		return !shouldClose;
	}
	
	protected static class LayerButton extends ButtonWidget
	{
		protected LayerButton(int x, int y, int width, int height, Text message, PressAction onPress)
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
