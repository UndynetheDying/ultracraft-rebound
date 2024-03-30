package absolutelyaya.ultracraft.client.gui.widget;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.level.IUltraLevelComponent;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.data.LevelData;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.util.RenderingUtil;
import absolutelyaya.ultracraft.util.TimeUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import org.joml.Vector4f;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;

public class LevelButton extends ClickableWidget
{
	final static String[] RANKS = new String[] { "P", "§4S", "§6A", "§eB", "§aC", "§bD", "§9E", "§8F" };
	static final Identifier UPDATE_MARKER_TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/particle/shock.png");
	static final TextRenderer tRenderer;
	public final Identifier preview, destination;
	final Text description, author;
	final String authorLink;
	final Consumer<Identifier> action;
	final int version;
	boolean isUnlocked, isUnimplemented, isHidden, hasMusic, hasRankingData;
	float hoverAnim, animTime;
	
	public LevelButton(int x, int y, LevelData data, Consumer<Identifier> action)
	{
		super(x, y, 96, 64, data.getTitleText());
		description = data.getDescriptionText();
		if(data.getBuiltin())
			author = Text.of("");
		else
			author = Text.translatable("screen.ultracraft.level.author",
					data.getAuthorText().getString().length() > 0 ? data.getAuthorText() : Text.translatable("level.ultracaft.author.unknown"));
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
		isHidden = data.isHidden();
		isUnimplemented = data.isUnimplemented();
		version = data.getVersion();
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
		hasRankingData = data.hasFullRankingData();
		hasMusic = data.hasMusic();
		calcDimensions();
	}
	
	public LevelButton(int x, int y, Text title, Identifier preview, Identifier destination, Consumer<Identifier> action)
	{
		super(x, y, 96, 64, title);
		description = author = Text.of("");
		authorLink = "";
		this.preview = preview;
		this.destination = destination;
		version = -1;
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
		width = Math.max(96, Math.max(tRenderer.getWidth(getMessage()), tRenderer.getWidth(author))) + 8; // min == 104
		height = 64 + (author.getString().length() > 0 ? tRenderer.fontHeight : 0) + (authorLink.length() > 0 ? 2 : 0);
	}
	
	public void selfCenter()
	{
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
		MinecraftClient client = MinecraftClient.getInstance();
		animTime += delta / 5f;
		ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(client.player);
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getX(), getY(), 0f);
		context.fill(0, 0, width, height, levelStats.getBestRank(destination) == 0 ? 0x88f49f00 : 0x88000000);
		hoverAnim = MathHelper.lerp(delta / 5f, hoverAnim, isHovered() && isUnlocked && alpha > 0.5 ? 1f : 0f);
		if(isHovered() && isUnlocked && alpha > 0.5)
			context.drawBorder(-1, -1, width + 2, height + 2, 0xffffffff);
		//update marker
		if(isUnlocked && levelStats.getLastPlayedLevelVersion(destination) < version)
		{
			matrices.push();
			matrices.translate(width, 0, 0);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(25f * (float)Math.sin(animTime)));
			matrices.scale(1.25f, 1.25f, 1f);
			context.setShaderColor(1f, 1f, 1f, 1f);
			context.drawTexture(UPDATE_MARKER_TEXTURE, -6, -6, 0, 0, 13, 13, 13, 13);
			matrices.pop();
		}
		if(hoverAnim > 0f)
		{
			RenderSystem.setShaderColor(1f, 1f, 1f, (hoverAnim - 0.5f) * 2f);
			matrices.push();
			matrices.translate(0f, 0f, Math.max(hoverAnim * 10f - 7.5f, 0f));
			//time-panel
			if(hasRankingData)
			{
				Text header = Text.translatable("screen.ultracraft.level.time-header");
				long time = levelStats.getBestTime(destination, true);
				Text ppb = Text.translatable("screen.ultracraft.level.ppb-time", time == -1 ? "-" : TimeUtil.milliToString(time));
				time = levelStats.getBestTime(destination, false);
				Text pb = Text.translatable("screen.ultracraft.level.pb-time", time == -1 ? "-" : TimeUtil.milliToString(time));
				int timeBoxWidth = Math.max(Math.max(tRenderer.getWidth(ppb), tRenderer.getWidth(pb)), tRenderer.getWidth(header)) + 6;
				matrices.push();
				matrices.translate((-timeBoxWidth - 8) * hoverAnim, 0f, 0f);
				context.fill(0, 0, timeBoxWidth, tRenderer.fontHeight * 3 + 11, 0xff000000);
				context.drawBorder(0, 0, timeBoxWidth, tRenderer.fontHeight * 3 + 11, 0xffffffff);
				context.drawText(tRenderer, header.getWithStyle(Style.EMPTY.withUnderline(true)).get(0),
						3, 3, 0xffffffff, true);
				context.drawText(tRenderer, ppb, 3, 7 + tRenderer.fontHeight, 0xffffffff, true);
				context.drawText(tRenderer, pb, 3, 9 + tRenderer.fontHeight * 2, 0xffffffff, true);
				matrices.pop();
			}
			int descBoxHeight = 0;
			//description-panel
			if(!description.getString().isEmpty())
			{
				matrices.push();
				int descBoxWidth = 128;
				descBoxHeight = tRenderer.getWrappedLinesHeight(description, descBoxWidth) + 8;
				matrices.translate((width + 8) * hoverAnim, 0f, 0f);
				context.fill(0, 0, descBoxWidth, descBoxHeight, 0xff000000);
				context.drawBorder(0, 0, descBoxWidth, descBoxHeight, 0xffffffff);
				for (OrderedText line : tRenderer.wrapLines(description, descBoxWidth - 6))
				{
					context.drawText(tRenderer, line, 3, 3, 0xffffffff, true);
					matrices.translate(0, tRenderer.fontHeight + 1, 0f);
				}
				matrices.pop();
			}
			//music hint
			if(hasMusic)
			{
				matrices.push();
				if(descBoxHeight > 0)
					matrices.translate(0, descBoxHeight + 2, 0);
				Text t = Text.translatable("screen.ultracraft.level.music-hint");
				int boxWidth = tRenderer.getWidth(t) + 6, boxHeight = tRenderer.fontHeight + 5;
				matrices.translate((width + 8) * hoverAnim, 0f, 0f);
				context.fill(0, 0, boxWidth, boxHeight, 0xff000000);
				context.drawBorder(0, 0, boxWidth, boxHeight, 0xffffffff);
				context.drawText(tRenderer, t, 3, 3, 0xffffffff, true);
				matrices.pop();
			}
			matrices.pop();
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		}
		List<Text> t = getMessage().getWithStyle(Style.EMPTY.withUnderline(true));
		if(t.size() > 0)
			context.drawText(tRenderer, t.get(0),
					(width - tRenderer.getWidth(t.get(0))) / 2, 2, 0xffffffff, true);
		RenderSystem.setShaderTexture(0, isUnlocked ? preview : LevelDataManager.PLACEHOLDER_THUMB);
		RenderingUtil.drawTexture(matrices.peek().getPositionMatrix(), new Vector4f(width / 2f - 36, 14, 72, 48), 0,
				new Vec2f(480, 320), new Vector4f(0, 0, 480, -320), alpha);
		t = Text.of(author.getString()).getWithStyle(Style.EMPTY.withUnderline(authorLink.length() > 0));
		if(t.size() > 0)
		{
			context.drawText(tRenderer, t.get(0),
					(width - tRenderer.getWidth(t.get(0))) / 2, 64,
					authorLink.isEmpty() || isHoveringAuthorLink(mouseX, mouseY) ? 0xffffffff : 0x3972bd, true);
		}
		//Best Rank
		int rank = levelStats.getBestRank(destination);
		if(rank > -1)
		{
			Text text = Text.of(RANKS[Math.min(rank, RANKS.length)]);
			context.drawText(tRenderer, text, width / 2 + 38, 54, 0xffffffff, rank != 0);
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
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		boolean b = super.mouseClicked(mouseX, mouseY, button);
		if(b && isUnlocked && !isUnimplemented && !isHidden && alpha > 0.5)
		{
			if(isHoveringAuthorLink(mouseX, mouseY))
			{
				try
				{
					URL url = new URL(authorLink);
					MinecraftClient client = MinecraftClient.getInstance();
					client.setScreen(new ConfirmLinkScreen((confirmed) -> {
						if (confirmed)
							Util.getOperatingSystem().open(url);
						client.currentScreen.close();
					}, url.toString(), false));
				}
				catch (MalformedURLException e)
				{
					throw new RuntimeException(e);
				}
			}
			action.accept(destination);
		}
		return b;
	}
	
	boolean isHoveringAuthorLink(double mouseX, double mouseY)
	{
		if(authorLink == null || authorLink.isEmpty())
			return false;
		return mouseX > getX() && mouseX < getX() + width && mouseY > getY() + height - tRenderer.fontHeight && mouseY < getY() + height;
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
