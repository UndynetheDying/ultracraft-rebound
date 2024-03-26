package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.gui.widget.LevelButton;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.level.IUltraLevelComponent;
import absolutelyaya.ultracraft.data.LevelDataManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

import static absolutelyaya.ultracraft.data.LevelDataManager.getLevelData;

public class TravelScreen extends AbstractTravelScreen
{
	final boolean forced;
	float blinkTimer;
	TextRenderer textRenderer;
	List<ClickableWidget> buttons = new ArrayList<>(), layerButtons = new ArrayList<>(), levelButtons = new ArrayList<>();
	
	public TravelScreen(boolean closeImmediately)
	{
		this(closeImmediately, false);
	}
	
	public TravelScreen(boolean closeImmediately, boolean forced)
	{
		this(closeImmediately, forced, false);
	}
	
	public TravelScreen(boolean closeImmediately, boolean forced, boolean noIntro)
	{
		super(Text.of("travel"));
		textRenderer = MinecraftClient.getInstance().textRenderer;
		shouldClose = closeImmediately;
		this.forced = forced;
		if(noIntro)
			openAnimTime = 1f;
	}
	
	@Override
	protected void init()
	{
		super.init();
		initButtons();
	}
	
	public void initButtons()
	{
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
		List<ClickableWidget> buttons = new ArrayList<>();
		buttons.add(layerButton(0));
		ButtonWidget button;
		buttons.add(button = layerButton(1));
		IUltraLevelComponent global = UltraComponents.GLOBAL.get(client.player.getWorld().getLevelProperties());
		button.active = global.isAnyLimboDestinationUnlocked();
		buttons.add(button = layerButton(2));
		button.active = false;
		buttons.add(button = layerButton(3));
		button.active = false;
		if(!forced)
			buttons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.close"), b -> shouldClose = true)
								 	.dimensions(width / 2 - 50, height - 32, 100, 20).build());
		if(LevelDataManager.isCustomLevelsPresent())
		{
			Text t = Text.translatable("screen.ultracraft.travel.custom").append(Text.of("..."));
			buttons.add(ButtonWidget.builder(t, b -> client.setScreen(new CustomLevelSelectScreen(this)))
								.dimensions(12, height - 32, textRenderer.getWidth(t) + 30, 20).build());
		}
		return buttons;
	}
	
	List<ClickableWidget> initLevelButtons()
	{
		levelButtons.clear();
		List<ClickableWidget> buttons = new ArrayList<>();
		int spacing = 8;
		int x = 0;
		switch(selectedLayer)
		{
			case OVERWORLD ->
			{
				LevelButton freeroam = new LevelButton(x, height / 2 - 68, Text.translatable("level.ultracraft.0-F"),
						new Identifier(Ultracraft.MOD_ID, "textures/level/0_freeroam.png"), new Identifier(Ultracraft.MOD_ID, "dimension.overworld"),
						d -> travel(Layer.OVERWORLD));
				x += freeroam.getWidth() + spacing;
				LevelButton tutorial = new LevelButton(x, height / 2 - 68,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "tutorial")), this::selectLevel);
				centerButtons(x + tutorial.getWidth(), freeroam, tutorial);
				x = 0;
				LevelButton prelude1 = new LevelButton(x, height / 2,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "prelude1")), this::selectLevel);
				x += prelude1.getWidth() + spacing;
				LevelButton prelude2 = new LevelButton(x, height / 2,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "prelude2")), this::selectLevel);
				x += prelude2.getWidth() + spacing;
				LevelButton prelude3 = new LevelButton(x, height / 2,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "prelude3")), this::selectLevel);
				centerButtons(x + prelude3.getWidth(), prelude1, prelude2, prelude3);
				buttons.addAll(List.of(freeroam, tutorial, prelude1, prelude2, prelude3));
			}
			case LIMBO ->
			{
				LevelButton freeroam = new LevelButton(x, height / 2 - 68, Text.translatable("level.ultracraft.1-F"),
						new Identifier(Ultracraft.MOD_ID, "textures/level/1_freeroam.png"), new Identifier(Ultracraft.MOD_ID, "dimension.limbo"),
						d -> travel(Layer.LIMBO));
				centerButtons(x + freeroam.getWidth(), freeroam);
				LevelButton limbo1 = new LevelButton(x, height / 2,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "limbo1")), this::selectLevel);
				x += limbo1.getWidth() + spacing;
				LevelButton limbo2 = new LevelButton(x, height / 2,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "limbo2")), this::selectLevel);
				x += limbo2.getWidth() + spacing;
				LevelButton luna = new LevelButton(x, height / 2,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "luna")), ignored -> {});
				centerButtons(x + luna.getWidth(), limbo1, limbo2, luna);
				buttons.addAll(List.of(freeroam, limbo1, limbo2, luna));
			}
		}
		buttons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.back"), b -> {
			selectedLayer = null;
			layerButtons.addAll(initLayerButtons());
		}).dimensions(width / 2 - 50, height - 32, 100, 20).build());
		return buttons;
	}
	
	void centerButtons(int maxX, ClickableWidget... buttons)
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
		if(selectedLevel != null)
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
		if(blinkTimer < 0.5f && curLayer != null)
			context.drawTexture(TEXTURE, width / 2 - 50 - 16, height / 2 + (curLayer.ordinal() - 2) * 30 + 6,
					0, 60, 11, 8, 128, 128);
	}
	
	void renderLayerLevels(DrawContext context, int mouseX, int mouseY, float delta)
	{
		if(shouldClose)
			return;
		levelButtons.forEach(b -> b.render(context, mouseX, mouseY, delta));
		context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.layer" + selectedLayer.ordinal()),
				width / 2, 16, 0xffffffff);
	}
	
	@Override
	protected void selectLayer(int layer)
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
