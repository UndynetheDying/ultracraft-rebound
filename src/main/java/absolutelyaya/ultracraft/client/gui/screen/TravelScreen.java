package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.gui.widget.LevelButton;
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
		super(Text.of("travel"));
		textRenderer = MinecraftClient.getInstance().textRenderer;
		shouldClose = closeImmediately;
		this.forced = forced;
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
		buttons.add(layerButton(1));
		ButtonWidget button;
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
				LevelButton freeroam = new LevelButton(x, height / 2 - 32, Text.translatable("level.ultracraft.0-F"),
						new Identifier(Ultracraft.MOD_ID, "textures/level/0_freeroam.png"), new Identifier(Ultracraft.MOD_ID, "dimension.overworld"),
						d -> travel(Layer.OVERWORLD));
				buttons.add(new LevelButton(x, height / 2 - 100,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "tutorial")), this::selectLevel));
				x += freeroam.getWidth() + spacing;
				LevelButton prelude1 = new LevelButton(x, height / 2 - 32,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "prelude1")), this::selectLevel);
				x += prelude1.getWidth() + spacing;
				LevelButton prelude2 = new LevelButton(x, height / 2 - 32,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "prelude2")), this::selectLevel);
				x += prelude2.getWidth() + spacing;
				LevelButton prelude3 = new LevelButton(x, height / 2 - 32,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "prelude3")), this::selectLevel);
				buttons.addAll(List.of(freeroam, prelude1, prelude2, prelude3));
			}
			case LIMBO ->
			{
				LevelButton limbo1 = new LevelButton(x, height / 2 - 32,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "limbo1")), this::selectLevel);
				x += limbo1.getWidth() + spacing;
				LevelButton freeroam = new LevelButton(x, height / 2 - 32, Text.translatable("level.ultracraft.1-F"),
						new Identifier(Ultracraft.MOD_ID, "textures/level/1_freeroam.png"), new Identifier(Ultracraft.MOD_ID, "dimension.limbo"),
						d -> travel(Layer.LIMBO));
				x += freeroam.getWidth() + spacing;
				LevelButton limbo2 = new LevelButton(x, height / 2 - 32,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "limbo2")), this::selectLevel);
				x += limbo1.getWidth() + spacing;
				buttons.add(new LevelButton(x, height / 2 - 32,
						getLevelData(new Identifier(Ultracraft.MOD_ID, "luna")), ignored -> {}));
				buttons.addAll(List.of(freeroam, limbo1, limbo2));
			}
		}
		int offset = 0;
		for (ClickableWidget button : buttons)
		{
			offset += Math.max(button.getWidth() - 104, 0) / 2;
			button.setX(button.getX() + (width - x) / 2 + offset);
		}
		buttons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.back"), b -> {
			selectedLayer = null;
			layerButtons.addAll(initLayerButtons());
		}).dimensions(width / 2 - 50, height - 32, 100, 20).build());
		return buttons;
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
