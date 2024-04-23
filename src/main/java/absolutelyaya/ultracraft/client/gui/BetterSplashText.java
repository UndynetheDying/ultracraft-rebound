package absolutelyaya.ultracraft.client.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

public class BetterSplashText extends SplashTextRenderer
{
	public static final byte ANIM_Wicked = 1;
	static final Random rand = Random.create();
	
	byte anim = 0;
	
	public BetterSplashText(Text text)
	{
		super(text.getString());
	}
	
	public BetterSplashText(SplashTextRenderer renderer)
	{
		super(Text.translatable(renderer.text).getString());
	}
	
	public BetterSplashText setAnim(byte anim)
	{
		this.anim = anim;
		return this;
	}
	
	@Override
	public void render(DrawContext context, int screenWidth, TextRenderer textRenderer, int alpha)
	{
		switch(anim)
		{
			case ANIM_Wicked -> renderWicked(context, screenWidth, textRenderer, alpha);
			default -> super.render(context, screenWidth, textRenderer, alpha);
		}
	}
	
	public void renderWicked(DrawContext context, int screenWidth, TextRenderer textRenderer, int alpha)
	{
		MatrixStack matrices = context.getMatrices();
		int layers = 4;
		for (int i = layers; i > 0; i--)
		{
			matrices.push();
			matrices.translate((float)screenWidth / 2f + 123f, 69f, 0f);
			matrices.translate(rand.nextBetween(-i, i) / 2.5f, rand.nextBetween(-i, i) / 2.5f, layers - i);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20f));
			context.setShaderColor(1f, 1f, 1f, 1f - ((float)i / layers));
			context.drawCenteredTextWithShadow(textRenderer, text, 0, -8, 16776960);
			context.setShaderColor(1f, 1f, 1f, 1f);
			matrices.pop();
		}
	}
	
	public static BetterSplashText fourtwenty()
	{
		return new BetterSplashText(Text.translatable("splash.ultracraft.fourtwenty"));
	}
	
	public static BetterSplashText wicked()
	{
		return new BetterSplashText(Text.translatable("splash.ultracraft.wicked")).setAnim(ANIM_Wicked);
	}
	
	public static BetterSplashText nosex()
	{
		return new BetterSplashText(Text.translatable("splash.ultracraft.none"));
	}
}
