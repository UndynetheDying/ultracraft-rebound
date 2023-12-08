package absolutelyaya.ultracraft.client.gui.screen;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.WidgetAccessor;
import absolutelyaya.ultracraft.client.gui.widget.ConfigWidget;
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
	static NbtCompound rules;
	List<ConfigWidget<?>> ruleWidgets = new ArrayList<>();
	float curScroll, desiredScroll;
	CheckboxWidget simplistic;
	
	public ServerConfigScreen(NbtCompound rules)
	{
		super(Text.translatable("screen.ultracraft.server.config-menu.title"));
		ServerConfigScreen.rules = rules;
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
		addRule(config.projboost, pos, ProjectileBoostSetting.values(), 0);
		addRule(config.hivel, pos, Setting.values(), 1);
		addRule(config.timestop, pos, new String[] { Setting.FORCE_ON.toString(), Setting.FORCE_OFF.toString() }, 2);
		addRule(config.disableHandswap, pos, ConfigWidget.ValueType.BOOL, 3);
		//addRule(config.hivelJumpBoost, pos, ConfigWidget.ValueType.INT, 4);
		//addRule(config.slamStorage, pos, ConfigWidget.ValueType.BOOL, 5);
		//addRule(config.hivelFallDamage, pos, ConfigWidget.ValueType.BOOL, 6);
		//addRule(config.hivelDrowning, pos, ConfigWidget.ValueType.BOOL, 7);
		addRule(config.bloodHeal, pos, RegenSetting.values(), 8);
		//addRule(config.hivelSpeed, pos, ConfigWidget.ValueType.FLOAT, 9);
		//addRule(config.hivelGravity, pos, ConfigWidget.ValueType.FLOAT, 10);
		addRule(config.effectivelyViolent, pos, ConfigWidget.ValueType.BOOL, 11);
		addRule(config.explosionBlockBreaking, pos, ConfigWidget.ValueType.BOOL, 12);
		addRule(config.smSafeLedges, pos, ConfigWidget.ValueType.BOOL, 13);
		addRule(config.parryChaining, pos, ConfigWidget.ValueType.BOOL, 14);
		addRule(config.tntPriming, pos, ConfigWidget.ValueType.BOOL, 15);
		addRule(config.revolverDamage, pos, ConfigWidget.ValueType.FLOAT, 16);
		//addRule(config.iFrames, pos, ConfigWidget.ValueType.INT, 17);
		addRule(config.terminalProtection, pos, ConfigWidget.ValueType.BOOL, 18);
		addRule(config.graffiti, pos, GraffitiSetting.values(), 19);
		addRule(config.flamethrowerGrief, pos, ConfigWidget.ValueType.BOOL, 20);
		addRule(config.shotgunDamage, pos, ConfigWidget.ValueType.FLOAT, 21);
		addRule(config.nailgunDamage, pos, ConfigWidget.ValueType.FLOAT, 22);
		addRule(config.hellObserverInterval, pos, ConfigWidget.ValueType.INT, 23);
		//addRule(GameruleRegistry.START_WITH_PIERCER, pos, GameRuleWidget.ValueType.BOOL);
		addRule(config.bloodSaturation, pos, ConfigWidget.ValueType.BOOL, 24);
		
		boolean b = false;
		if(simplistic != null)
			b = simplistic.isChecked();
		simplistic = addDrawableChild(new CheckboxWidget(width / 2 + 130, height - 30, 60, 20,
				Text.translatable("screen.ultracraft.server.config-menu.simplistic"), b));
	}
	
	<K extends ConfigEntry<?>> void addRule(K key, Vector2i pos, String[] values, int iconIdx)
	{
		ruleWidgets.add(addDrawableChild(new ConfigWidget<>(rules, pos, key, values, iconIdx)));
	}
	
	<K extends ConfigEntry<?>> void addRule(K key, Vector2i pos, Enum<?>[] values, int iconIdx)
	{
		ruleWidgets.add(addDrawableChild(new ConfigWidget<>(rules, pos, key, values, iconIdx)));
	}
	
	<K extends ConfigEntry<?>> void addRule(K key, Vector2i pos, ConfigWidget.ValueType valueType, int iconIdx)
	{
		ruleWidgets.add(addDrawableChild(new ConfigWidget<>(rules, pos, key, valueType, iconIdx)));
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
		for (ConfigWidget<?> widget : ruleWidgets)
			if(widget.isRule(rule))
				widget.stateUpdate();
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
	
	public static NbtCompound getRules()
	{
		return rules;
	}
}
