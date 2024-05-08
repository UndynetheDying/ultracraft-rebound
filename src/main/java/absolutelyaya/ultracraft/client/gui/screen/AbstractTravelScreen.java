package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.gui.TitleHUD;
import absolutelyaya.ultracraft.client.gui.widget.LevelInstanceButton;
import absolutelyaya.ultracraft.client.rendering.TitleBGRenderer;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.data.LevelCollection;
import absolutelyaya.ultracraft.data.LevelCollectionManager;
import absolutelyaya.ultracraft.data.LevelDataManager;
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
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Vector2i;

import java.util.*;

public abstract class AbstractTravelScreen extends Screen
{
	protected static final Identifier TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/gui/travel.png");
	public static final TitleBGRenderer BG = new TitleBGRenderer(new CubeMapRenderer(new Identifier(Ultracraft.MOD_ID, "aaa")));
	protected float openAnimTime, closeAnimTime;
	protected boolean shouldClose, awaitingFeedback;
	protected long waitingSince;
	protected Identifier curLayer, selectedLayer;
	protected Identifier selectedLevel;
	Map<String, UUID> instanceMap = new HashMap<>();
	List<ClickableWidget> instanceButtons = new ArrayList<>();
	
	protected AbstractTravelScreen(Text title)
	{
		super(title);
	}
	
	@Override
	protected void init()
	{
		super.init();
		if(MinecraftClient.getInstance().world != null)
			curLayer = MinecraftClient.getInstance().world.getRegistryKey().getValue();
		ClientPlayNetworking.send(PacketRegistry.REQUEST_DESTINATIONS_PACKET_ID, new PacketByteBuf(Unpooled.buffer()));
		if(selectedLevel != null)
			instanceButtons = initInstanceButtons();
	}
	
	protected ButtonWidget layerButton(Identifier layer, int idx)
	{
		LevelCollection collection = LevelCollectionManager.getLevelCollection(layer);
		if(collection == null)
			return null;
		return new LayerButton(width / 2 - 50, height / 2 + (idx - 2) * 30, 100, 20,
				collection.getTitleText(), b -> selectLayer(layer));
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		if(!awaitingFeedback)
			super.render(context, mouseX, mouseY, delta);
		else if(shouldCloseOnEsc())
		{
			Text t = Text.translatable("screen.ultracraft.travel.waiting-long");
			context.drawText(textRenderer, t, (width - textRenderer.getWidth(t)) / 2, 64, 0xffffffff, true);
		}
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
		
		if(selectedLevel != null && !awaitingFeedback)
			renderInstanceSelection(context, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean shouldCloseOnEsc()
	{
		return awaitingFeedback && (System.currentTimeMillis() - waitingSince > 10 * 1000);
	}
	
	void renderInstanceSelection(DrawContext context, int mouseX, int mouseY, float delta)
	{
		if(shouldClose)
			return;
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		if(instanceButtons.size() == 3)
			context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.instance.empty"),
					width / 2, 32, 0xffffffff);
		instanceButtons.forEach(b -> b.render(context, mouseX, mouseY, delta));
		context.drawCenteredTextWithShadow(textRenderer,
				Text.translatable("screen.ultracraft.travel.instance.title", LevelDataManager.getLevelData(selectedLevel).getTitleText()),
				width / 2, 16, 0xffffffff);
	}
	
	protected void travel(Layer layer)
	{
		if(layer.id.equals(curLayer))
		{
			setShouldClose();
			return;
		}
		awaitingFeedback = true;
		waitingSince = System.currentTimeMillis();
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(layer.ordinal());
		ClientPlayNetworking.send(PacketRegistry.TRAVEL_PACKET_ID, buf);
		UltracraftClient.setTravelling(true);
		TitleHUD.Instance.setBigTitle(Text.translatable("title.entrance.layer-" + layer.ordinal()), 10f);
	}
	
	protected void selectLevel(Identifier id)
	{
		selectedLevel = id;
		if(Screen.hasShiftDown())
		{
			enterInstance("");
			return;
		}
		else if(Screen.hasAltDown())
		{
			enterInstance("private");
			return;
		}
		instanceButtons = initInstanceButtons();
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(id);
		ClientPlayNetworking.send(PacketRegistry.REQUEST_INSTANCES_PACKET_ID, buf);
	}
	
	/**
	 * pass in "" for a new instance, "private" for a new private instance
	 */
	protected void enterInstance(String instance)
	{
		awaitingFeedback = true;
		waitingSince = System.currentTimeMillis();
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeIdentifier(selectedLevel);
		buf.writeString(instance == null ? "" : instance);
		ClientPlayNetworking.send(PacketRegistry.ENTER_LEVEL_PACKET_ID, buf);
		UltraComponents.LEVEL_STATS.get(client.player).stopTimer(true);
	}
	
	protected void selectLayer(Identifier layer)
	{
		selectedLayer =	layer;
	}
	
	@Override
	public boolean shouldPause()
	{
		return !shouldClose;
	}
	
	public void setShouldClose()
	{
		shouldClose = true;
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
	
	public void setInstanceMap(Map<String, UUID> map)
	{
		instanceMap = map;
		if(selectedLevel != null)
			instanceButtons = initInstanceButtons();
	}
	
	List<ClickableWidget> initInstanceButtons()
	{
		instanceButtons.clear();
		List<ClickableWidget> buttons = new ArrayList<>();
		net.minecraft.util.math.random.Random rand = Random.create();
		List<Vector2i> usedPositions = new ArrayList<>();
		for (Map.Entry<String, UUID> entry : instanceMap.entrySet())
		{
			Vector2i pos = new Vector2i();
			for (int i = 0; i < 8; i++)
			{
				boolean blocked = false;
				pos = new Vector2i(rand.nextBetween(16, width - 64), rand.nextBetween(64, height - 128));
				for (Vector2i p : usedPositions)
				{
					if (pos.x - 2 < p.x + 34 && pos.x + 34 > p.x - 2 && pos.y + 50 > p.y - 2 && pos.y - 2 < pos.y + 50)
					{
						blocked = true;
						break;
					}
				}
				if(!blocked)
					break;
			}
			buttons.add(new LevelInstanceButton(pos.x, pos.y, entry.getKey(), entry.getValue(), this::enterInstance));
			usedPositions.add(pos);
		}
		buttons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.instance.new"), b -> enterInstance(""))
							.dimensions(width / 2 - 100, height - 54, 99, 20).build());
		buttons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.instance.new-private"), b -> enterInstance("private"))
							.dimensions(width / 2 + 1, height - 54, 99, 20).build());
		buttons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.back"), b -> {
			selectedLevel = null;
			instanceButtons.clear();
		}).dimensions(width / 2 - 100, height - 32, 200, 20).build());
		return buttons;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		boolean b;
		if(selectedLevel != null)
		{
			for (ClickableWidget widget : instanceButtons)
			{
				b = widget.mouseClicked(mouseX, mouseY, button);
				if(b)
					return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
