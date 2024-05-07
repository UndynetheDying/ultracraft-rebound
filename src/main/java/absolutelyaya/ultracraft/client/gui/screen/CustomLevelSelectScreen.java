package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.client.gui.widget.LevelButton;
import absolutelyaya.ultracraft.data.LevelCollectionManager;
import absolutelyaya.ultracraft.data.LevelDataManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class CustomLevelSelectScreen extends AbstractTravelScreen
{
	final Screen parent;
	List<ClickableWidget> levelButtons = new ArrayList<>();
	float curListoffset, targetListOffset;
	int listHeight, maxScroll;
	
	public CustomLevelSelectScreen(Screen parent)
	{
		super(Text.translatable("screen.ultracraft.travel.custom"));
		this.parent = parent;
		if(parent != null)
			openAnimTime = 1f;
	}
	
	@Override
	protected void init()
	{
		super.init();
		levelButtons = new ArrayList<>();
		levelButtons.add(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.close"), b -> client.setScreen(parent))
							.dimensions(width / 2 - 50, height - 32, 100, 20).build());
		listHeight = 32;
		LevelDataManager.getAllCustomLevels().forEach((id, data) -> {
			LevelButton b = new LevelButton(width / 2, listHeight, data, this::selectLevel);
			b.selfCenter();
			levelButtons.add(b);
			listHeight += b.getHeight() + 8;
		});
		maxScroll = listHeight - 32;
		if(LevelCollectionManager.isCustomLayersPresent())
		{
			Text t = Text.translatable("screen.ultracraft.travel.custom-layers").append(Text.of("..."));
			levelButtons.add(ButtonWidget.builder(t, b -> client.setScreen(new TravelScreen(this, (parent instanceof TravelScreen screen && screen.forced))))
								.dimensions(12, height - 32, textRenderer.getWidth(t) + 30, 20).build());
		}
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		if(awaitingFeedback)
		{
			context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.waiting"), width / 2, 16, 0xffffffff);
			super.render(context, mouseX, mouseY, delta);
			return;
		}
		super.render(context, mouseX, mouseY, delta);
		if(selectedLevel != null)
			return;
		context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.custom"), width / 2, 16, 0xffffffff);
		MatrixStack matrices = context.getMatrices();
		curListoffset = MathHelper.lerp(delta, curListoffset, targetListOffset);
		matrices.push();
		matrices.translate(0, curListoffset, -10);
		for (ClickableWidget button : levelButtons)
		{
			int y = (int)(button.getY() + curListoffset);
			if(button instanceof LevelButton)
				button.setAlpha(Math.min((y - 12) / 20f, 1f) - MathHelper.clamp(Math.max(y - height + 125, 0) / 20f, 0f, 1f));
			button.render(context, mouseX, mouseY - (int)curListoffset, delta);
		}
		matrices.pop();
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		targetListOffset = (float)MathHelper.clamp(targetListOffset + amount * -10, -maxScroll, 0);
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		boolean b = super.mouseClicked(mouseX, mouseY, button);
		if(b)
			return true;
		for (ClickableWidget i : levelButtons)
			if(i.mouseClicked(mouseX, mouseY - curListoffset, button))
				return true;
		return false;
	}
	
	@Override
	public void close()
	{
		client.setScreen(shouldClose ? null : parent);
	}
}
