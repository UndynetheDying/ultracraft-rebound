package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.cybergrind.CybergrindData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class CybergrindHUD
{
	public static CybergrindHUD Instance;
	static final Identifier ROULETTE_TEX = new Identifier(Ultracraft.MOD_ID, "textures/gui/sacrificial_roulette.png");
	final Random rand = Random.create();
	final MinecraftClient client;
	final String[] fakeNames = new String[] { "AbsoluteZaza", "SomethingWicked", "Flowerman", "Interloper", "Maurice", "xX_TheSize2_Xx", "Querbert" };
	
	int announcementSequenceStep, lastEnemies;
	float announcementSequenceTime, spinTime;
	List<Text> playerNames;
	Text resultName;
	
	public CybergrindHUD()
	{
		Instance = this;
		client = MinecraftClient.getInstance();
	}
	
	public void startAnnouncementSequence(Text resultName)
	{
		playerNames = new ArrayList<>();
		client.getServer().getPlayerManager().getPlayerList().forEach(p -> playerNames.add(p.getDisplayName()));
		while(rand.nextFloat() < 0.05f)
			playerNames.add(rand.nextInt(playerNames.size() - 1), Text.of(fakeNames[rand.nextInt(playerNames.size())]));
		
		announcementSequenceStep = 1;
		announcementSequenceTime = 0f;
		spinTime = 0f;
		this.resultName = resultName;
	}
	
	public void render(DrawContext context)
	{
		TextRenderer tRenderer = client.textRenderer;
		float deltaTime = client.getLastFrameDuration() / 30f;
		int width = context.getScaledWindowWidth();
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(client.player);
		
		if(announcementSequenceStep > 0)
			renderAnnouncementSequence(tRenderer, context, deltaTime, width);
		if(winged != null && winged.getCybergrindData() != null)
			renderStats(tRenderer, context, winged.getCybergrindData());
	}
	
	void renderStats(TextRenderer tRenderer, DrawContext context, CybergrindData data)
	{
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(2, 2, 0);
		context.drawText(tRenderer, Text.of("wave: " + data.getCurrentWave() + "/" + data.getWaves()), 0, 0, 0xffffffff, true);
		matrices.translate(0, tRenderer.fontHeight, 0);
		int enemies = lastEnemies;
		if(enemies != data.getEnemies())
			enemies += lastEnemies < data.getEnemies() ? 1 : -1;
		context.drawText(tRenderer, Text.of("enemies: " + enemies), 0, 0, 0xffffffff, true);
		lastEnemies = enemies;
		matrices.pop();
	}
	
	void renderAnnouncementSequence(TextRenderer tRenderer, DrawContext context, float deltaTime, int width)
	{
		MatrixStack matrices = context.getMatrices();
		announcementSequenceTime += deltaTime;
		matrices.push();
		float x = width / 2f - 48f;
		switch (announcementSequenceStep)
		{
			case 1 -> { //Move in frame while shaking - 4 seconds total
				matrices.translate(x, MathHelper.lerp(1f - announcementSequenceTime / 4f, 0, -48), 0);
				matrices.translate((rand.nextFloat() - 0.5f) * 2f, 0, 0);
				drawRoulette(tRenderer, context, 0f, 0f, 0f);
				if (announcementSequenceTime > 4f)
					advanceSequenceStep();
			}
			case 2 -> { //Flicker light on 1 second - 5.5 seconds total
				matrices.translate(x, 0, 0);
				drawRoulette(tRenderer, context, -0.25f + announcementSequenceTime + (announcementSequenceTime % 0.2f), 0f, 0f);
				if (announcementSequenceTime > 1.5f)
					advanceSequenceStep();
			}
			case 3 -> { //Spin Roulette 5.5 seconds - 11 seconds total
				matrices.translate(x, 0, 0);
				float spin = 5.5f - announcementSequenceTime;
				drawRoulette(tRenderer, context, 1f, 0f, spin);
				if (announcementSequenceTime > 5.5f)
				{
					advanceSequenceStep();
					MinecraftClient.getInstance().player.sendMessage(Text.translatable("message.ultracraft.cybergrind.chosen", resultName));
				}
			}
			case 4 -> { //Winner text sequence 4 seconds - 15 seconds total
				float letter = announcementSequenceTime / 1.5f * 7f; // each animation step lasts 1/7 seconds
				matrices.translate(x, 0, 0);
				drawRoulette(tRenderer, context, 1f, letter, -1f);
				if (announcementSequenceTime > 4f)
					advanceSequenceStep();
			}
			case 5 -> { //Move out of frame - 18 seconds total
				matrices.translate(x, MathHelper.lerp(announcementSequenceTime / 3f, 0, -48), 0);
				drawRoulette(tRenderer, context, 1f, 7.5f, -1f);
				if (announcementSequenceTime > 3f)
					advanceSequenceStep();
			}
			default -> announcementSequenceStep = 0;
		}
		matrices.pop();
	}
	
	void drawRoulette(TextRenderer tRenderer, DrawContext context, float brightness, float letter, float spinSpeed)
	{
		if(brightness <= 0.5f)
			RenderSystem.setShaderColor(0.25f, 0.15f, 0.25f, 1f);
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(0, 0, 16);
		context.drawTexture(ROULETTE_TEX, 0, 0, 0, 0, 96, 64, 128, 128);
		//Letters
		if(letter > 0)
			drawWinner(context, letter);
		//Names BG
		matrices.translate(0, 0, -32);
		context.drawTexture(ROULETTE_TEX, 10, 8, 0, 64, 76, 11, 128, 128);
		matrices.pop();
		//Names
		matrices.push();
		matrices.translate(12, 10, 0);
		if(spinSpeed != -1)
			drawNames(tRenderer, context, spinSpeed);
		else
			context.drawText(tRenderer, resultName, 0, 0, 0xffffffff, false);
		matrices.pop();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
	}
	
	void drawTextWithBlur(TextRenderer tRenderer, DrawContext context, Text text, float length)
	{
		float step = 1f / length;
		float[] col = RenderSystem.getShaderColor();
		for (int i = 0; i < Math.ceil(length) + 1; i++)
		{
			RenderSystem.setShaderColor(col[0], col[1], col[2], 1f - step * i);
			context.drawText(tRenderer, text, 0, i, 0xffffffff, false);
		}
	}
	
	void drawNames(TextRenderer tRenderer, DrawContext context, float spinSpeed)
	{
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		if(spinSpeed > 1)
			spinTime += spinSpeed / 30f;
		float height = tRenderer.fontHeight + 2;
		int lastName = ((int)Math.floor(spinTime)) % playerNames.size();
		float y = -(spinTime % 1f);
		if(spinSpeed <= 1)
		{
			lastName = (lastName + 1) % playerNames.size();
			y -= 1f - spinSpeed - 1f;
		}
		matrices.translate(0, y * height, 0);
		//last
		matrices.translate(0, -height, 0);
		drawTextWithBlur(tRenderer, context, playerNames.get(lastName), spinSpeed);
		//current
		matrices.translate(0, height, 0);
		drawTextWithBlur(tRenderer, context, playerNames.get((lastName + 1) % playerNames.size()), spinSpeed);
		//next
		if(spinSpeed > 1f)
		{
			matrices.translate(0, height, 0);
			drawTextWithBlur(tRenderer, context, playerNames.get((lastName + 2) % playerNames.size()), spinSpeed);
		}
		else
		{
			matrices.pop();
			matrices.push();
			matrices.translate(0, spinSpeed * height, 0);
			drawTextWithBlur(tRenderer, context, resultName, spinSpeed);
		}
		matrices.pop();
	}
	
	void drawWinner(DrawContext context, float time)
	{
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(28, 23, 0);
		int step = (int)Math.floor(time);
		int x = switch (step) {
			case 1 -> 8;
			case 2 -> 11;
			case 3 -> 18;
			case 4 -> 25;
			case 5 -> 31;
			case 6 -> 37;
			default -> 0;
		};
		int width = switch(step) {
			case 0 -> 7;
			case 1, 6 -> 2;
			case 2, 3 -> 6;
			case 4, 5 -> 5;
			default -> time % 2f > 1f ? 39 : 0;
		};
		context.drawTexture(ROULETTE_TEX, x, 0, x, 76, width, 8, 128, 128);
		matrices.pop();
	}
	
	void advanceSequenceStep()
	{
		announcementSequenceStep++;
		announcementSequenceTime = 0f;
	}
}
