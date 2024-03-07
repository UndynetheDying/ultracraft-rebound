package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.util.RenderingUtil;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.*;

public class CreditsScreen extends Screen
{
	static final Style ROLE_STYLE = Style.EMPTY.withUnderline(true);
	static final Style NAME_STYLE = Style.EMPTY.withColor(Formatting.GOLD);
	static final Identifier TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/gui/credits.png");
	static final Identifier BG = new Identifier(Ultracraft.MOD_ID, "textures/block/flesh1.png");
	static final List<Text> supporters = new ArrayList<>();
	static boolean initializedSupporters;
	final Screen parent;
	final List<ContributorElement> contributors = new ArrayList<>();
	final LocalizerElement localizers;
	float time;
	
	public CreditsScreen(Screen parent)
	{
		super(Text.translatable("screen.ultracraft.credits.title"));
		this.parent = parent;
		contributors.add(new ContributorElement("dev", "Absolutelyaya"));
		contributors.add(new ContributorElement("build", "Talon_MC", "AshenWulf"));
		contributors.add(new ContributorElement("sound", "8BitBunny"));
		contributors.add(new ContributorElement("test", "Talon_MC", "AshenWulf", "Athanes", "Marmalude"));
		HashMap<String, List<String>> localizerMap = new HashMap<>();
		localizerMap.put("LOLCAT", new ArrayList<>() { { add("Doggochleb"); } });
		localizers = new LocalizerElement(localizerMap);
		//init Supporter list and keep it; no need to fetch the list every time the screen is opened
		if(!initializedSupporters)
		{
			JsonObject supporterList = Ultracraft.fetchSupporterList();
			if(supporterList == null)
				return;
			List<Text> formerSupporters = new ArrayList<>();
			supporterList.entrySet().forEach(entry -> {
				JsonObject element = entry.getValue().getAsJsonObject();
				int show = 0;
				if(element.has("show"))
					show = element.get("show").getAsInt();
				if(element.has("name") && show > 0)
				{
					if(show == 2)
						formerSupporters.add(Text.of(element.get("name").getAsString()).getWithStyle(Style.EMPTY.withColor(Formatting.GRAY)).get(0));
					else
						supporters.add(Text.of(element.get("name").getAsString()));
				}
			});
			Collections.shuffle(supporters);
			Collections.shuffle(formerSupporters);
			supporters.addAll(formerSupporters);
			initializedSupporters = true;
		}
	}
	
	@Override
	protected void init()
	{
		super.init();
		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.back"), button -> close())
								 .dimensions((width - 32) / 2 - 100, height - 36 - 20, 200, 20).build());
		addDrawableChild(new InfoWidget(width - 31, 36, Text.translatable("screen.ultracraft.credits.supporter-info")));
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		MatrixStack matrices = context.getMatrices();
		//bg
		int marginH = 16, marginV = 32;
		matrices.push();
		time += MinecraftClient.getInstance().getLastFrameDuration() / 10f;
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, BG);
		RenderSystem.setShaderColor(0.8f, 0.6f, 0.6f, 0.8f);
		for (int y = -1; y < height / 32 + 1; y++)
		{
			//if(Math.random() > 0.9f)
			for (int x = -1; x < width / 32 + 1; x++)
			{
				RenderingUtil.drawTexture(matrices.peek().getPositionMatrix(),
						new Vector4f(x * 32 + (float)Math.sin((time + y) / 2.4475f) * 8f, y * 32 + (float)Math.cos((time + x) / 4.887f) * 8f, 32, 32),
						new Vec2f(16, 16), new Vector4f(0, 0, 16, 16));
			}
		}
		matrices.pop();
		
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		context.fill(marginH, marginV, width - marginH, height - marginV, 0xff000000);
		context.drawBorder(marginH, marginV, width - marginH * 2, height - marginV * 2, 0xffffffff);
		super.render(context, mouseX, mouseY, delta);
		
		matrices.push();
		matrices.translate(marginH, marginV, 0);
		context.drawTexture(TEXTURE, width / 4 - marginH, 4, 0, 0, 0, 64, 16, 96, 96);
		context.drawTexture(TEXTURE, (int)(width * 0.62), height / 3 - 24, 0, 0, 48, 64, 48, 96, 96);
		
		//Contributors
		matrices.push();
		matrices.translate(4, 24, 0);
		matrices.push();
		int lastHeight = 0;
		for (int i = 0; i < contributors.size(); i++)
		{
			if(i % 2 == 1)
				matrices.translate((width - marginH) / 3f, 0, 0);
			else if(i > 0)
			{
				matrices.translate((width - marginH) / -3f, textRenderer.fontHeight + lastHeight, 0);
				lastHeight = 0;
			}
			int h = contributors.get(i).render(context, textRenderer);
			if(h > lastHeight)
				lastHeight = h;
		}
		if(contributors.size() % 2 == 0)
			matrices.translate((width - marginH) / -3f, 0, 0);
		matrices.translate(4, textRenderer.fontHeight * 3 + lastHeight, 0);
		localizers.render(context, textRenderer);
		matrices.pop();
		matrices.pop();
		
		//Supporters
		matrices.push();
		matrices.translate((int)((width - marginH) * 0.8f), 0, 0);
		context.drawTexture(TEXTURE, - 40, 4, 0, 16, 80, 19, 96, 96);
		matrices.translate(0, 24, 0);
		supporters.forEach(i -> {
			matrices.translate(0, textRenderer.fontHeight, 0);
			context.drawText(textRenderer, i, - textRenderer.getWidth(i) / 2, 0, 0xffffffff, true);
		});
		matrices.pop();
		matrices.pop();
	}
	
	static class ContributorElement
	{
		final Text role;
		final List<Text> names = new ArrayList<>();
		
		public ContributorElement(String role, String... names)
		{
			this.role = Text.translatable("screen.ultracraft.credits." + role).getWithStyle(ROLE_STYLE).get(0);
			for (String name : names)
				this.names.add(Text.of(name).getWithStyle(NAME_STYLE).get(0));
		}
		
		public int render(DrawContext context, TextRenderer tRender)
		{
			int height = tRender.fontHeight + 1;
			MatrixStack matrices = context.getMatrices();
			matrices.push();
			context.drawText(tRender, role, 0, 0, 0xffffffff, true);
			matrices.translate(16, 2, 0);
			for (Text name : names)
			{
				matrices.translate(0, tRender.fontHeight + 1, 0);
				height += tRender.fontHeight + 1;
				context.drawText(tRender, name, 0, 0, 0xffffffff, true);
			}
			matrices.pop();
			return height;
		}
	}
	
	static class LocalizerElement
	{
		final Text role;
		final HashMap<Text, List<Text>> entries = new HashMap<>();
		
		public LocalizerElement(HashMap<String, List<String>> entries)
		{
			this.role = Text.translatable("screen.ultracraft.credits.localization").getWithStyle(ROLE_STYLE).get(0);
			for (Map.Entry<String, List<String>> entry : entries.entrySet())
			{
				List<Text> names = new ArrayList<>();
				entry.getValue().forEach(i -> names.add(Text.of(i).getWithStyle(NAME_STYLE).get(0)));
				this.entries.put(Text.of(entry.getKey()).getWithStyle(ROLE_STYLE.withColor(Formatting.GRAY)).get(0), names);
			}
		}
		
		public void render(DrawContext context, TextRenderer tRender)
		{
			MatrixStack matrices = context.getMatrices();
			context.drawText(tRender, role, 0, 0, 0xffffffff, true);
			matrices.translate(8, tRender.fontHeight + 3, 0);
			for (Map.Entry<Text, List<Text>> entry : entries.entrySet())
			{
				context.drawText(tRender, entry.getKey(), 0, 0, 0xffffffff, true);
				matrices.translate(8, 1, 0);
				entry.getValue().forEach(i -> {
					matrices.translate(0, tRender.fontHeight + 1, 0);
					context.drawText(tRender, i, 0, 0, 0xffffffff, true);
				});
				matrices.translate(-8, 0, 0);
			}
			matrices.translate(-8, tRender.fontHeight * 2, 0);
		}
	}
	
	static class InfoWidget extends ClickableWidget
	{
		public InfoWidget(int x, int y, Text tooltip)
		{
			super(x, y, 10, 10, Text.empty());
			setTooltip(WiderTooltip.of(tooltip));
		}
		
		@Override
		protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
		{
			context.drawTexture(TEXTURE, getX(), getY(), 0, 1, 36, 10, 10, 96, 96);
		}
		
		@Override
		protected void appendClickableNarrations(NarrationMessageBuilder builder)
		{
		
		}
		
		static class WiderTooltip extends Tooltip
		{
			private List<OrderedText> lines;
			Text content;
			
			public WiderTooltip(Text content, @Nullable Text narration)
			{
				super(content, narration);
				this.content = content;
			}
			
			public static WiderTooltip of(Text content) {
				return new WiderTooltip(content, content);
			}
			
			@Override
			public List<OrderedText> getLines(MinecraftClient client)
			{
				if (lines == null)
					lines = client.textRenderer.wrapLines(content, 320);
				return lines;
			}
		}
	}
}
