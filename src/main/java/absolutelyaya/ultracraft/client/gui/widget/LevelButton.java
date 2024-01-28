package absolutelyaya.ultracraft.client.gui.widget;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.level.IUltraLevelComponent;
import absolutelyaya.ultracraft.dimension.LevelData;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.util.RenderingUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector4f;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

public class LevelButton extends ClickableWidget
{
	static final TextRenderer tRenderer;
	public final Identifier preview, destination;
	final Text description, author;
	final String authorLink, parTime;
	final Consumer<Identifier> action;
	boolean isUnlocked, isUnimplemented, isHidden;
	
	public LevelButton(int x, int y, LevelData data, Consumer<Identifier> action)
	{
		super(x, y, 96, 64, data.getTitle());
		description = data.getDescription();
		if(data.getBuiltin())
			author = Text.of("");
		else
			author = Text.translatable("screen.ultracraft.level.author",
					data.getAuthor().getString().length() > 0 ? data.getAuthor() : Text.translatable("level.ultracaft.author.unknown"));
		String authorLink = data.getAuthorLink();
		if(authorLink.length() > 0)
		{
			try
			{
				new URL(authorLink);
			}
			catch (MalformedURLException e)
			{
				authorLink = "";
				Ultracraft.LOGGER.warn("authorLink for level '" + data.getID() + "' is an invalid URL! (" + data.getAuthorLink() + ")");
			}
		}
		this.authorLink = authorLink;
		preview = data.getThumbnail();
		destination = data.getID();
		parTime = data.getParTimeString();
		isHidden = data.isHidden();
		isUnimplemented = data.isUnimplemented();
		this.action = action;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null)
		{
			isUnlocked = false;
			return;
		}
		IUltraLevelComponent global = UltraComponents.GLOBAL.get(player.getWorld().getLevelProperties());
		isUnlocked = global.isDestinationUnlocked(destination);
		if(!isUnlocked)
			setMessage(Text.of(getMessage().getString().replaceAll("[^ ]", "?")));
		calcDimensions();
	}
	
	public LevelButton(int x, int y, Text title, Identifier preview, Identifier destination, Consumer<Identifier> action)
	{
		super(x, y, 96, 64, title);
		description = author = Text.of("");
		authorLink = parTime = "";
		this.preview = preview;
		this.destination = destination;
		this.action = action;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null)
		{
			isUnlocked = false;
			return;
		}
		IUltraLevelComponent global = UltraComponents.GLOBAL.get(player.getWorld().getLevelProperties());
		isUnlocked = global.isDestinationUnlocked(destination);
		if(!isUnlocked)
			setMessage(Text.of(getMessage().getString().replaceAll("[^ ]", "?")));
		calcDimensions();
	}
	
	void calcDimensions()
	{
		width = Math.max(96, Math.max(tRenderer.getWidth(getMessage()), tRenderer.getWidth(author))) + 8;
		height = 64 + (author.getString().length() > 0 ? tRenderer.fontHeight : 0) + (authorLink.length() > 0 ? 2 : 0);
		setX(getX() - width / 2);
	}
	
	@Override
	protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
	{
		if(isHidden && !isUnlocked)
			return;
		if(isUnimplemented && isUnlocked)
		{
			renderUnimplemented(context);
			return;
		}
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getX(), getY(), 0f);
		context.fill(0, 0, width, height, 0x88000000);
		if(isHovered() && isUnlocked && alpha > 0.5)
		{
			context.fill(-1, -1, 0, height + 1, 0xffffffff);
			context.fill(width - 1, -1, width, height + 1, 0xffffffff);
			context.fill(0, -1, width - 1, 0, 0xffffffff);
			context.fill(0, height, width - 1, height + 1, 0xffffffff);
		}
		List<Text> t = getMessage().getWithStyle(Style.EMPTY.withUnderline(true));
		if(t.size() > 0)
			context.drawText(tRenderer, t.get(0),
					(width - tRenderer.getWidth(t.get(0))) / 2, 2, 0xffffffff, true);
		RenderSystem.setShaderTexture(0, isUnlocked ? preview : LevelManager.PLACEHOLDER_THUMB);
		RenderingUtil.drawTexture(matrices.peek().getPositionMatrix(), new Vector4f(width / 2f - 32, 14, 64, 48), 0,
				new Vec2f(480, 320), new Vector4f(0, 0, 480, -320), alpha);
		t = Text.of(author.getString()).getWithStyle(Style.EMPTY.withUnderline(authorLink.length() > 0));
		if(t.size() > 0)
		{
			context.drawText(tRenderer, t.get(0),
					(width - tRenderer.getWidth(t.get(0))) / 2, 64, 0xffffffff, true); //TODO: color blue when hovered IF authorLink exists
		}
		matrices.pop();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}
	
	void renderUnimplemented(DrawContext context)
	{
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getX(), getY(), 0f);
		context.fill(0, 0, width, height, 0x88000000);
		Text t = Text.translatable("level.ultracraft.unimplemented");
		context.drawText(tRenderer, t, (width - tRenderer.getWidth(t)) / 2, height / 2 - 6, 0xffffffff, true);
		matrices.pop();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}
	
	@Override
	protected boolean clicked(double mouseX, double mouseY)
	{
		boolean b = super.clicked(mouseX, mouseY);
		//TODO: open authorLink
		if(b && isUnlocked && !isUnimplemented && !isHidden && alpha > 0.5)
			action.accept(destination);
		return b;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		boolean b = super.mouseClicked(mouseX, mouseY, button);
		//TODO: open authorLink
		if(b && isUnlocked && !isUnimplemented && !isHidden && alpha > 0.5)
			action.accept(destination);
		return b;
	}
	
	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder)
	{
		builder.put(NarrationPart.TITLE, getMessage());
	}
	
	static {
		tRenderer = MinecraftClient.getInstance().textRenderer;
	}
}
