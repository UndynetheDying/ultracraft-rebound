package absolutelyaya.ultracraft.client.gui.widget;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class LevelButton extends ClickableWidget
{
	public Identifier preview;
	Runnable action;
	
	public LevelButton(int x, int y, int width, int height, Text title, String previewTexture, Runnable action)
	{
		super(x, y, width, height, title);
		preview = new Identifier(Ultracraft.MOD_ID, "textures/level/" + previewTexture + ".png");
		this.action = action;
	}
	
	@Override
	protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
	{
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getX(), getY(), 0f);
		context.fill(0, 0, width, height, 0x88000000);
		if(isHovered())
		{
			context.fill(-1, -1, 0, height + 1, 0xffffffff);
			context.fill(width - 1, -1, width, height + 1, 0xffffffff);
			context.fill(0, -1, width - 1, 0, 0xffffffff);
			context.fill(0, height, width - 1, height + 1, 0xffffffff);
		}
		List<Text> t = getMessage().getWithStyle(Style.EMPTY.withUnderline(true));
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		if(t.size() > 0)
			context.drawText(renderer, t.get(0),
					(width - renderer.getWidth(t.get(0))) / 2, 2, 0xffffff, true);
		context.drawTexture(preview, width / 2 - 32, 14, 64, 48, 0, 0, 480, 320, 480, 320);
		matrices.pop();
	}
	
	@Override
	protected boolean clicked(double mouseX, double mouseY)
	{
		boolean b = super.clicked(mouseX, mouseY);
		if(b)
			action.run();
		return b;
	}
	
	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder)
	{
		builder.put(NarrationPart.TITLE, getMessage());
	}
}
