package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.client.gui.widget.LevelButton;
import absolutelyaya.ultracraft.dimension.LevelManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class CustomLevelSelectScreen extends AbstractTravelScreen
{
	final Screen parent;
	List<LevelButton> levelButtons = new ArrayList<>();
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
		addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ultracraft.travel.close"), b -> client.setScreen(parent))
							.dimensions(width / 2 - 50, height - 32, 100, 20).build());
		levelButtons = new ArrayList<>();
		listHeight = 32;
		LevelManager.getAllCustomLevels().forEach((id, data) -> {
			LevelButton b = new LevelButton(width / 2, listHeight, data, this::enterLevel);
			levelButtons.add(b);
			maxScroll = listHeight - 32;
			listHeight += b.getHeight() + 8;
		});
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ultracraft.travel.custom"), width / 2, 16, 0xffffffff);
		super.render(context, mouseX, mouseY, delta);
		MatrixStack matrices = context.getMatrices();
		curListoffset = MathHelper.lerp(delta, curListoffset, targetListOffset);
		matrices.push();
		matrices.translate(0, curListoffset, -10);
		for (LevelButton button : levelButtons)
		{
			int y = (int)(button.getY() + curListoffset);
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
		for (LevelButton lb : levelButtons)
			if(lb.mouseClicked(mouseX, mouseY - curListoffset, button))
				return true;
		return false;
	}
	
	@Override
	public void close()
	{
		client.setScreen(shouldClose ? null : parent);
	}
}
