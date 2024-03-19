package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.data.LevelData;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.util.TimeUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class LevelRankingScreen extends AbstractTravelScreen
{
	final static String[] RANKS = new String[] { "P", "§4S", "§6A", "§eB", "§aC", "§bD", "§9E", "§8F" };
	ILevelStatsComponent levelStats;
	Text title;
	int timeRank = -1, killRank = -1, styleRank = -1, finalRank = -1;
	ButtonWidget nextLevelButton;
	float anim = 0f;
	
	public LevelRankingScreen()
	{
		super(Text.of("ranking"));
	}
	
	@Override
	protected void init()
	{
		super.init();
		levelStats = UltraComponents.LEVEL_STATS.get(client.player);
		LevelData data = LevelDataManager.getLevelData(levelStats.getCurrentLevel());
		title = Text.translatable(data.getTitleKey() + ".title");
		if(data.hasFullRankingData())
		{
			long time = levelStats.getLastStoppedTimer();
			timeRank = data.getRankForTime(time);
			killRank = data.getRankForKills(levelStats.getKills());
			styleRank = data.getRankForStyle(levelStats.getStyle());
			finalRank = (int)Math.ceil((timeRank + killRank + styleRank) / 3f);
			if(levelStats.getDeaths() > 0)
				finalRank++;
			levelStats.setBestRank(levelStats.getCurrentLevel(), finalRank);
			if(time != -1)
				levelStats.setBestTime(levelStats.getCurrentLevel(), finalRank == 0, time);
		}
		
		instanceButtons.clear();
		instanceButtons.add(addDrawable(ButtonWidget.builder(Text.translatable("screen.ultracraft.ranking.select"),
				press -> client.setScreen(new TravelScreen(false, true, true)))
							.dimensions(width / 2 - 92, height / 2 + 75, 75, 20).build()));
		nextLevelButton = addDrawable(ButtonWidget.builder(Text.translatable("screen.ultracraft.ranking.next"),
				press -> {
					selectLevel(data.getNextLevel());
					String curInstance = levelStats.getCurrentLevelInstance();
					if(curInstance != null)
					{
						LevelManager.LevelInstance inst = LevelManager.Instance.getInstance(curInstance);
						enterInstance(inst != null && inst.isPrivate() ? "private" : "");
					}
					else
						enterInstance("");
		}).dimensions(width / 2 + 17, height / 2 + 75, 75, 20).build());
		instanceButtons.add(nextLevelButton);
		refreshNextLevelButton();
	}
	
	public void refreshNextLevelButton()
	{
		LevelData data = LevelDataManager.getLevelData(levelStats.getCurrentLevel());
		Identifier nextLevel = data.getNextLevel();
		if(nextLevel == null)
		{
			nextLevelButton.active = false;
			return;
		}
		boolean unlocked = UltraComponents.GLOBAL.get(client.world.getLevelProperties()).isDestinationUnlocked(nextLevel);
		nextLevelButton.active = unlocked && !data.isUnimplemented();
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		anim += client.getLastFrameDuration() / 20f;
		super.render(context, mouseX, mouseY, delta);
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		//title
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		matrices.push();
		matrices.translate((width - textRenderer.getWidth(title) * 2f) / 2f, height / 2f - 90, 0f);
		matrices.scale(2f, 2f, 2f);
		context.drawText(textRenderer, title, 0, 0, 0xffffffff, true);
		matrices.pop();
		//panels
		matrices.push();
		matrices.translate(width / 2f - 92, height / 2f - 59, 0f);
		//time
		Text time = Text.of(TimeUtil.milliToString(levelStats.getLastStoppedTimer()));
		drawPanel(context, Text.translatable("screen.ultracraft.ranking.time"), time, true, timeRank, 0.5f, 0.1f);
		//kills
		matrices.translate(0, 25, 0);
		Text kills = Text.of(String.valueOf(levelStats.getKills()));
		drawPanel(context, Text.translatable("screen.ultracraft.ranking.kills"), kills, true, killRank, 0.6f, 0.1f);
		//style
		matrices.translate(0, 25, 0);
		Text style = Text.of(String.valueOf(levelStats.getStyle()));
		drawPanel(context, Text.translatable("screen.ultracraft.ranking.style"), style, true, styleRank, 0.7f, 0.1f);
		//restarts
		matrices.translate(0, 25, 0);
		int restarts = levelStats.getDeaths();
		Text restartsText = Text.translatable("screen.ultracraft.ranking." + (restarts == 0 ? "no-restarts" : "restarts"), restarts);
		Text damagedText = Text.translatable(restarts == 0 && levelStats.isUndamaged() ? "screen.ultracraft.ranking.no-damage" : "");
		drawPanel(context, restartsText, damagedText, false, -1, 0.8f, 0.1f);
		//secrets
		matrices.translate(0, 25, 0);
		drawPanel(context, Text.translatable("screen.ultracraft.ranking.secrets"), Text.translatable("screen.ultracraft.ranking.secrets-tba"),
				false, -1, 0.9f, 0.1f);
		matrices.pop();
		//BIG final rank
		float alpha = MathHelper.clamp(anim - 1f, 0f, 0.25f) * 4f;
		RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
		matrices.push();
		if(finalRank > -1 && finalRank < RANKS.length)
		{
			context.fill(width / 2 + 26, height / 2 - 59, width / 2 + 90, height / 2 + 6, finalRank == 0 ? 0xffffa200 : 0x88000000);
			matrices.translate(width / 2f + 16 + 24, height / 2f - 59 + 8, 0);
			float scale = 6f + (1f - alpha);
			matrices.scale(scale, scale, scale);
			context.drawText(textRenderer, RANKS[finalRank], 0, 0, 0xffffffff, finalRank > 0);
		}
		else
		{
			context.fill(width / 2 + 26, height / 2 - 59, width / 2 + 90, height / 2 + 6, 0x88000000);
			Text t = Text.translatable("screen.ultracraft.timer.no-ranking");
			matrices.translate(width / 2f + 58 - textRenderer.getWidth(t) / 2f, height / 2f - 59 + 28, 0);
			context.drawText(textRenderer, t, 0, 0, 0xffffffff, true);
		}
		matrices.pop();
		RenderSystem.enableBlend();
		matrices.pop();
	}
	
	void drawPanel(DrawContext context, Text header, Text value, boolean indentValue, int rank, float appearDelay, float appearTime)
	{
		if(openAnimTime < appearDelay)
			return;
		RenderSystem.setShaderColor(1f, 1f, 1f, Math.min((anim - appearDelay) * appearTime * (1f / appearTime), 1f));
		if(rank > -1)
			rank += 1;
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		context.fill(0, 0, 115, 24, 0x88000000);
		matrices.translate(2, 2, 0);
		context.drawText(textRenderer, header, 0, 0, 0xffffffff, true);
		matrices.push();
		matrices.translate(13, textRenderer.fontHeight + 2, 0);
		if(indentValue)
			matrices.translate(13, 0, 0);
		context.drawText(textRenderer, value, 0, 0, 0xffffffff, true);
		matrices.pop();
		if(rank > -1)
		{
			matrices.translate(115 - 18, 2, 0);
			matrices.scale(2, 2, 2);
			context.drawText(textRenderer, Text.of(RANKS[Math.min(rank, RANKS.length)]), 0, 0, 0xffffffff, true);
		}
		matrices.pop();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		for (ClickableWidget widget : instanceButtons)
		{
			boolean b = widget.mouseClicked(mouseX, mouseY, button);
			if(b)
				return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
