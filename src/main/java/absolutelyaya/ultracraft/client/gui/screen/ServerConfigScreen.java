package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.WidgetAccessor;
import absolutelyaya.ultracraft.client.gui.widget.GameRuleWidget;
import absolutelyaya.ultracraft.config.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class ServerConfigScreen extends Screen
{
	public static ServerConfigScreen INSTANCE;
	final NbtCompound rules;
	List<GameRuleWidget<?>> ruleWidgets = new ArrayList<>();
	float curScroll, desiredScroll;
	CheckboxWidget simplistic;
	
	public ServerConfigScreen(NbtCompound rules)
	{
		super(Text.translatable("screen.ultracraft.server.config-menu.title"));
		this.rules = rules;
		INSTANCE = this;
	}
	
	@Override
	protected void init()
	{
		super.init();
		ruleWidgets.forEach(this::remove);
		ruleWidgets.clear();
		Vector2i pos = new Vector2i(width / 2 - 100, 40);
		ServerConfig config = ServerConfig.INSTANCE;
		addRule(config.projboost, pos, ProjectileBoostSetting.values());
		addRule(config.hivel, pos, Setting.values());
		addRule(config.timestop, pos, new String[] { Setting.FORCE_ON.toString(), Setting.FORCE_OFF.toString() });
		addRule(config.disableHandswap, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.hivelJumpBoost, pos, GameRuleWidget.ValueType.INT);
		addRule(config.slamStorage, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.hivelFallDamage, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.hivelDrowning, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.bloodHeal, pos, RegenSetting.values());
		addRule(config.hivelSpeed, pos, GameRuleWidget.ValueType.INT);
		addRule(config.hivelGravity, pos, GameRuleWidget.ValueType.INT);
		addRule(config.effectivelyViolent, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.explosionBlockBreaking, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.smSafeLedges, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.parryChaining, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.tntPriming, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.revolverDamage, pos, GameRuleWidget.ValueType.INT);
		addRule(config.iFrames, pos, GameRuleWidget.ValueType.INT);
		addRule(config.terminalProtection, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.graffiti, pos, GraffitiSetting.values());
		addRule(config.flamethrowerGrief, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.shotgunDamage, pos, GameRuleWidget.ValueType.INT);
		addRule(config.nailgunDamage, pos, GameRuleWidget.ValueType.INT);
		addRule(config.hellObserverInterval, pos, GameRuleWidget.ValueType.INT);
		//addRule(GameruleRegistry.START_WITH_PIERCER, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.bloodSaturation, pos, GameRuleWidget.ValueType.BOOL);
		
		boolean b = false;
		if(simplistic != null)
			b = simplistic.isChecked();
		simplistic = addDrawableChild(new CheckboxWidget(width / 2 + 130, height - 30, 60, 20,
				Text.translatable("screen.ultracraft.server.config-menu.simplistic"), b));
	}
	
	<K extends ConfigEntry<?>> void addRule(K key, Vector2i pos, String[] values)
	{
		ruleWidgets.add(addDrawableChild(new GameRuleWidget<>(rules, pos, key, values, ruleWidgets.size())));
	}
	
	<K extends ConfigEntry<?>> void addRule(K key, Vector2i pos, Enum<?>[] values)
	{
		ruleWidgets.add(addDrawableChild(new GameRuleWidget<>(rules, pos, key, values, ruleWidgets.size())));
	}
	
	<K extends ConfigEntry<?>> void addRule(K key, Vector2i pos, GameRuleWidget.ValueType valueType)
	{
		ruleWidgets.add(addDrawableChild(new GameRuleWidget<>(rules, pos, key, valueType, ruleWidgets.size())));
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		renderBackground(context);
		curScroll = MathHelper.lerp(delta / 2f, curScroll, desiredScroll);
		ruleWidgets.forEach(w -> {
			((WidgetAccessor)w).setOffset(new Vector2i(0, Math.round(curScroll)));
			w.render(context, mouseX, mouseY, delta, simplistic.isChecked());
		});
		simplistic.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xffffffff);
	}
	
	@Override
	public void renderBackground(DrawContext context)
	{
		super.renderBackground(context);
		context.fill(0, 0, width, height, 0x44000000);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
		context.drawTexture(simplistic.isChecked() ? new Identifier(Ultracraft.MOD_ID, "textures/gui/simplistic_bg.png") : OPTIONS_BACKGROUND_TEXTURE,
				width /2 - 125, 0, 0, 0.0f, 0.0f, 250, height, 32, 32);
		context.fill(width / 2 - 125, -1, width / 2 - 124, height + 1, 0xaaffffff);
		context.fill(width / 2 + 125, -1, width / 2 + 124, height + 1, 0xaa000000);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		desiredScroll = MathHelper.clamp(desiredScroll + (float)amount * 15f, -36 * (ruleWidgets.size() - 3), 0);
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
	
	public <T extends ConfigEntry<?>> void onExternalRuleUpdate(T rule, String value)
	{
		rules.putString(rule.getId(), value);
		ruleWidgets.forEach(GameRuleWidget::stateUpdate);
	}
	
	@Override
	public boolean shouldPause()
	{
		return false;
	}
	
	@Override
	public void close()
	{
		super.close();
		INSTANCE = null;
	}
}
