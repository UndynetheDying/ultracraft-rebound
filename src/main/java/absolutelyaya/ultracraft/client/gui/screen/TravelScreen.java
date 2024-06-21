package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.gui.widget.LevelButton;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.level.IUltraLevelComponent;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.data.LevelCollection;
import absolutelyaya.ultracraft.data.LevelCollectionManager;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

import static absolutelyaya.ultracraft.data.LevelDataManager.getLevelData;

public class TravelScreen extends AbstractTravelScreen
{
	final Screen parent;
	final boolean forced;
	float blinkTimer;
	TextRenderer textRenderer;
	List<ClickableWidget> buttons = new ArrayList<>(), layerButtons = new ArrayList<>(), levelButtons = new ArrayList<>();
	boolean customLevels;
	
	public TravelScreen(CustomLevelSelectScreen screen, boolean forced)
	{
		super(Text.of("travel"), null);
		textRenderer = MinecraftClient.getInstance().textRenderer;
		parent = screen;
		this.forced = forced;
		customLevels = true;
		openAnimTime = 1f;
	}
	
	public TravelScreen(boolean closeImmediately)
	{
		this(closeImmediately, false, null);
	}
	
	public TravelScreen(boolean closeImmediately, boolean forced, Identifier forcedDestination)
	{
		this(closeImmediately, forced, false, forcedDestination);
	}
	
	public TravelScreen(boolean closeImmediately, boolean forced, boolean noIntro, Identifier forcedDestination)
	{
		super(Text.of("travel"), forcedDestination);
		textRenderer = MinecraftClient.getInstance().textRenderer;
		shouldClose = closeImmediately;
		this.forced = forced;
		if(noIntro)
			openAnimTime = 1f;
		parent = null;
	}
	
	@Override
	protected void init()
	{
		super.init();
		initButtons();
	}
	
	public void initButtons()
	{
		if(forcedDestination != null)
			return;
		buttons.clear();
		if(selectedLayer == null)
			layerButtons.addAll(initLayerButtons());
		else
			levelButtons.addAll(initLevelButtons());
		buttons.addAll(layerButtons);
		buttons.addAll(levelButtons);
	}
	
	List<ClickableWidget> initLayerButtons()
	{
		layerButtons.clear();
		IUltraLevelComponent global = UltraComponents.GLOBAL.get(client.player.getWorld().getLevelProperties());
		List<ClickableWidget> buttons = new ArrayList<>();
		int idx = 0;
		if(!customLevels)
		{
			for (Layer layer : Layer.values())
			{
				ButtonWidget button;
				buttons.add(button = layerButton(layer.id, layer.ordinal()));
				button.active = global.isAnyDestinationInLayerUnlocked(layer.id);
			}
		}
		else
		{
			Map<Identifier, LevelCollection> allLayers = LevelCollectionManager.getAllCustomLayers();
			for (Identifier id : allLayers.keySet())
			{
				ButtonWidget button;
				buttons.add(button = layerButton(id, idx));
				button.active = global.isAnyDestinationInLayerUnlocked(id);
				idx++;
			}
		}
		if(!forced)
			buttons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.close"), b -> {
				shouldClose = !customLevels || parent == null;
				if(!shouldClose)
					client.setScreen(parent);
			}).dimensions(width / 2 - 50, height - 32, 100, 20).build());
		if(LevelDataManager.isCustomLevelsPresent())
		{
			Text t = Text.translatable(customLevels ? "screen.ultracraft.travel.back" : "screen.ultracraft.travel.custom").append(Text.of("..."));
			buttons.add(ButtonWidget.builder(t, b -> client.setScreen(customLevels ? parent : new CustomLevelSelectScreen(this)))
								.dimensions(12, height - 32, textRenderer.getWidth(t) + 30, 20).build());
		}
		return buttons;
	}
	
	List<ClickableWidget> initLevelButtons()
	{
		levelButtons.clear();
		List<ClickableWidget> buttons = new ArrayList<>();
		int spacing = 8;
		buttons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.back"), b -> {
			selectedLayer = null;
			layerButtons.addAll(initLayerButtons());
		}).dimensions(width / 2 - 50, height - 32, 100, 20).build());
		LevelCollection collection = LevelCollectionManager.getLevelCollection(selectedLayer);
		if(collection == null)
		{
			Ultracraft.LOGGER.warn("tried to open Level Selection Screen for Layer that doesn't exist ({})", selectedLayer);
			return buttons;
		}
		if(collection.isBuiltin())
			addBuiltinLevelButtons(buttons, spacing);
		else
			addCustomLevelButtons(buttons, spacing);
		return buttons;
	}
	
	void addBuiltinLevelButtons(List<ClickableWidget> buttons, int spacing)
	{
		Layer layer = Layer.fromIdentifier(selectedLayer);
		int x = 0;
		if(layer == null)
			return;
		switch(layer)
		{
			case OVERWORLD ->
			{
				LevelButton freeroam = new LevelButton(x, height / 2 - 68, Text.translatable("level.ultracraft.0-F"),
						Ultracraft.identifier("textures/level/0_freeroam.png"), Ultracraft.identifier("dimension.overworld"),
						d -> travel(Layer.OVERWORLD));
				x += freeroam.getWidth() + spacing;
				LevelButton tutorial = new LevelButton(x, height / 2 - 68,
						getLevelData(Ultracraft.identifier("tutorial")), this::selectLevel);
				centerButtons(x + tutorial.getWidth(), freeroam, tutorial);
				x = 0;
				LevelButton prelude1 = new LevelButton(x, height / 2,
						getLevelData(Ultracraft.identifier("prelude1")), this::selectLevel);
				x += prelude1.getWidth() + spacing;
				LevelButton prelude2 = new LevelButton(x, height / 2,
						getLevelData(Ultracraft.identifier("prelude2")), this::selectLevel);
				x += prelude2.getWidth() + spacing;
				LevelButton prelude3 = new LevelButton(x, height / 2,
						getLevelData(Ultracraft.identifier("prelude3")), this::selectLevel);
				centerButtons(x + prelude3.getWidth(), prelude1, prelude2, prelude3);
				buttons.addAll(List.of(freeroam, tutorial, prelude1, prelude2, prelude3));
			}
			case LIMBO ->
			{
				LevelButton freeroam = new LevelButton(x, height / 2 - 68, Text.translatable("level.ultracraft.1-F"),
						Ultracraft.identifier("textures/level/1_freeroam.png"), Ultracraft.identifier("dimension.limbo"),
						d -> travel(Layer.LIMBO));
				centerButtons(x + freeroam.getWidth(), freeroam);
				LevelButton limbo1 = new LevelButton(x, height / 2,
						getLevelData(Ultracraft.identifier("limbo1")), this::selectLevel);
				x += limbo1.getWidth() + spacing;
				LevelButton limbo2 = new LevelButton(x, height / 2,
						getLevelData(Ultracraft.identifier("limbo2")), this::selectLevel);
				x += limbo2.getWidth() + spacing;
				LevelButton luna = new LevelButton(x, height / 2,
						getLevelData(Ultracraft.identifier("luna")), ignored -> {});
				centerButtons(x + luna.getWidth(), limbo1, limbo2, luna);
				buttons.addAll(List.of(freeroam, limbo1, limbo2, luna));
			}
		}
	}
	
	void addCustomLevelButtons(List<ClickableWidget> buttons, int spacing)
	{
		if(client == null)
			return;
		int x = 0, y = 0, maxWidth = (int)(client.getWindow().getScaledWidth() * 0.9f);
		LevelCollection layer = LevelCollectionManager.getLevelCollection(selectedLayer);
		if (layer == null)
			return;
		List<Identifier> ids = layer.getAllLevels();
		List<ClickableWidget> line = new ArrayList<>();
		for (Identifier id : ids)
		{
			LevelButton button = new LevelButton(x, height / 2 + y * (64 + spacing) - 32, getLevelData(id), this::selectLevel);
			buttons.add(button);
			line.add(button);
			x += button.getWidth() + spacing;
			if (x > maxWidth)
			{
				centerButtons(x, line);
				line.clear();
				x = 0;
				y++;
			}
		}
		centerButtons(x, line);
	}
	
	void centerButtons(int maxX, ClickableWidget... buttons)
	{
		for (ClickableWidget button : buttons)
			button.setX(button.getX() + (width - maxX) / 2);
	}
	
	void centerButtons(int maxX, List<ClickableWidget> buttons)
	{
		for (ClickableWidget button : buttons)
			button.setX(button.getX() + (width - maxX) / 2);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		if(awaitingFeedback)
		{
			context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.waiting"), width / 2, 16, 0xffffffff);
			super.render(context, mouseX, mouseY, delta);
			return;
		}
		super.render(context, mouseX, mouseY, delta);
		if(openAnimTime < 1f)
			buttons.forEach(b -> b.setAlpha(openAnimTime));
		else if(forcedDestination != null)
		{
			selectLevel(forcedDestination);
			ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(client.player);
			String curInstance = levelStats.getCurrentLevelInstance();
			if(curInstance != null)
			{
				LevelManager.LevelInstance inst = LevelManager.Instance.getInstance(curInstance);
				enterInstance(inst != null && inst.isPrivate() ? "private" : "");
			}
			else
				enterInstance("");
		}
		if(selectedLevel != null || forcedDestination != null)
			return;
		if(selectedLayer != null)
			renderLayerLevels(context, mouseX, mouseY, delta);
		else
			renderLayerSelection(context, mouseX, mouseY, delta);
	}
	
	void renderLayerSelection(DrawContext context, int mouseX, int mouseY, float delta)
	{
		blinkTimer = (blinkTimer + delta / 20f) % 1f;
		
		if(shouldClose)
			return;
		layerButtons.forEach(b -> b.render(context, mouseX, mouseY, delta));
		context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.title"), width / 2, 16, 0xffffffff);
		if(curLayer == null || customLevels)
			return;
		Layer layer = Layer.fromIdentifier(curLayer);
		if(blinkTimer < 0.5f && layer != null)
			context.drawTexture(TEXTURE, width / 2 - 50 - 16, height / 2 + (layer.ordinal() - 2) * 30 + 6,
					0, 60, 11, 8, 128, 128);
	}
	
	void renderLayerLevels(DrawContext context, int mouseX, int mouseY, float delta)
	{
		if(shouldClose)
			return;
		levelButtons.forEach(b -> b.render(context, mouseX, mouseY, delta));
		LevelCollection layer = LevelCollectionManager.getLevelCollection(selectedLayer);
		if(layer == null)
			return;
		context.drawCenteredTextWithShadow(textRenderer, layer.getTitleText(), width / 2, 16, 0xffffffff);
	}
	
	@Override
	protected void selectLayer(Identifier layer)
	{
		super.selectLayer(layer);
		levelButtons.addAll(initLevelButtons());
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(selectedLevel != null)
			return super.mouseClicked(mouseX,mouseY,button);
		if(awaitingFeedback)
			return false;
		boolean b = super.mouseClicked(mouseX, mouseY, button);
		if(b)
			return true;
		if(selectedLayer == null)
		{
			for (ClickableWidget widget : layerButtons)
			{
				b = widget.mouseClicked(mouseX, mouseY, button);
				if(b)
					return true;
			}
		}
		else
		{
			for (ClickableWidget widget : levelButtons)
			{
				b = widget.mouseClicked(mouseX, mouseY, button);
				if(b)
					return true;
			}
		}
		return false;
	}
}
