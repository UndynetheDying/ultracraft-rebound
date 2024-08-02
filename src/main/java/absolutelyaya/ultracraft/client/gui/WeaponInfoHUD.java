package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.entity.projectile.JumpstartHookEntity;
import absolutelyaya.ultracraft.item.weapons.AbstractNailgunItem;
import absolutelyaya.ultracraft.item.weapons.AttractorNailgunItem;
import absolutelyaya.ultracraft.item.weapons.JumpstartNailgunItem;
import absolutelyaya.ultracraft.item.weapons.OverheatNailgunItem;
import absolutelyaya.ultracraft.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Vector3f;

public class WeaponInfoHUD
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/gui/weapon_info_hud");
	
	public void render(DrawContext context, float tickDelta)
	{
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null)
			return;
		ItemStack stack = player.getMainHandStack();
		if(stack.getItem() instanceof AbstractNailgunItem)
			renderNailgun(context, tickDelta, stack, player);
	}
	
	void renderNailgun(DrawContext context, float tickDelta, ItemStack stack, PlayerEntity player)
	{
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();
		int x = width - 93, y = height - 47;
		
		MatrixStack matrices = context.getMatrices();
		context.fill(x, y, x + 91, y + 31, 0x88000000);
		if(stack.getItem() instanceof AttractorNailgunItem attractor)
		{
			int c = 0x68e9f8;
			String text = String.valueOf(attractor.getNbt(stack, "nails"));
			matrices.push();
			matrices.translate(x + 47 - renderer.getWidth(text), y + 5, 0);
			matrices.scale(2, 2, 2);
			context.drawText(renderer, text, 0, 0, c, false);
			matrices.pop();
			float f = attractor.getNbt(stack, "magnets") / 3f;
			context.setShaderColor(0.4f, 0.4f, 0.4f, 1f);
			context.drawTexture(TEXTURE, x, y + 24, 0, 7, 91, 7, 128, 128);
			float partial = (1 - UltraComponents.WINGED.get(MinecraftClient.getInstance().player)
							  .getGunCooldownManager().getCooldownPercent(attractor, GunCooldownManager.SECONDARY)) * 0.33f;
			if(partial == 0.33f)
				partial = 0f;
			context.drawTexture(TEXTURE, x, y + 24, 0, 0, Math.round((f + partial) * 91), 7, 128, 128);
			context.setShaderColor(0.407f, 0.913f, 0.972f, 1f);
			context.drawTexture(TEXTURE, x, y + 24, 0, 0, Math.round(f * 91), 7, 128, 128);
		}
		else if(stack.getItem() instanceof OverheatNailgunItem overheat)
		{
			int heatsinks = overheat.getNbt(stack, "heatsinks");
			float f = overheat.getNbt(stack, "heat") / 100f;
			if(heatsinks > 0)
			{
				Vector3f c = new Vector3f(1f, 0.607f, 0.058f);
				context.setShaderColor(c.x, c.y, c.z, 1f);
				matrices.push();
				matrices.translate(x + 6, y + 6, 0);
				matrices.scale(2f, 2f, 2f);
				context.drawTexture(TEXTURE, 0, 0, 1, 17, Math.round(36 * f), 4, 128, 128);
				c = new Vector3f(0.4f, 0.4f, 0.4f).lerp(c, f);
				context.setShaderColor(c.x, c.y, c.z, 1f);
				matrices.translate(-2, -2, 0);
				context.drawTexture(TEXTURE, 0, 0, 38, 15, 43, 8, 128, 128);
				matrices.pop();
			}
			f = overheat.getNbt(stack, "heatsinks") / 2f;
			context.setShaderColor(0.4f, 0.4f, 0.4f, 1f);
			float partial = (1f - overheat.getNbt(stack, "heatsink_cd") / 160f) * 0.5f;
			context.drawTexture(TEXTURE, x, y + 21, 0, 24, Math.round((f + partial) * 91), 7, 128, 128);
			context.setShaderColor(0.156f, 0.874f, 0.325f, 1f);
			context.drawTexture(TEXTURE, x, y + 21, 0, 24, Math.round(f * 91), 7, 128, 128);
		}
		else if(stack.getItem() instanceof JumpstartNailgunItem nailgun)
		{
			IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
			GunCooldownManager cdm = winged.getGunCooldownManager();
			float distancePercent = -1;
			Text text;
			if(!cdm.isUsable(nailgun, GunCooldownManager.SECONDARY))
			{
				matrices.push();
				matrices.translate(x, y, 0);
				text = Text.translatable("screen.ultracraft.jumpstart-nailgun-hud.recharging");
				context.drawText(renderer, text, 91 / 2 - renderer.getWidth(text) / 2, 1, 0xffffff, false);
				float partial = 1f - cdm.getCooldownPercent(nailgun, GunCooldownManager.SECONDARY);
				matrices.scale(1.5f, 1.5f, 1.5f);
				context.drawTexture(TEXTURE, 22, 5, (int)(Math.floor(partial * 8f) * 16), 48, 16, 16, 128, 128);
				matrices.pop();
				return;
			}
			JumpstartHookEntity hook = winged.getHook();
			matrices.push();
			matrices.translate(x, y, 0);
			if(hook == null)
			{
				HitResult hit = MinecraftClient.getInstance().crosshairTarget;
				if(hit instanceof EntityHitResult eHit && !hit.getType().equals(HitResult.Type.MISS) && eHit.getEntity() instanceof LivingEntity)
				{
					text = Text.translatable("screen.ultracraft.jumpstart-nailgun-hud.ready");
					distancePercent = Math.min(player.distanceTo(eHit.getEntity()) / (JumpstartHookEntity.MAX_DISTANCE + 0.1f), 1f);
				}
				else
					text = Text.translatable("screen.ultracraft.jumpstart-nailgun-hud.no-target");
				context.drawText(renderer, text, 91 / 2 - renderer.getWidth(text) / 2, 1, 0x9c9c9c, false);
			}
			else
			{
				text = Text.translatable("screen.ultracraft.jumpstart-nailgun-hud.distance", String.format("%.2f", hook.getDistance()));
				distancePercent = Math.min(hook.getDistancePercent(), 1f);
				int c = ColorUtil.getAsHex(new Vector3f(1f, 1f, 1f).lerp(new Vector3f(1f, 0f, 0f), distancePercent));
				context.drawText(renderer, text, 91 / 2 - renderer.getWidth(text) / 2, 1, c, false);
			}
			matrices.push();
			matrices.translate(1, 9.5, 0);
			matrices.scale(2f, 2f, 2f);
			context.drawTexture(TEXTURE, 2, 0, 41,  33, 41, 8, 128, 128);
			if(distancePercent != -1)
			{
				context.setShaderColor(0.9f, 0.1f, 0.1f, 1f);
				context.drawTexture(TEXTURE, 2, 0, 0,  33, (int)(41 * Math.min(distancePercent, 1f)), 8, 128, 128);
				context.setShaderColor(1f, 1f, 1f, 1f);
			}
			if(winged.isHasHookedEntity() && hook != null)
			{
				context.setShaderColor(0.9f, 0.1f, 0.1f, 1f);
				context.drawTexture(TEXTURE, 2, 7, 0,  41, 43, 3, 128, 128);
				context.setShaderColor(1f, 1f, 1f, 1f);
				context.drawTexture(TEXTURE, 2, 7, 0,  41, (int)(43 * Math.min(hook.getChargePercent(), 1f)), 3, 128, 128);
			}
			context.drawTexture(TEXTURE, 2, 0, 41,  41, 41, 8, 128, 128);
			matrices.pop();
			if(hook != null && hook.getDistancePercent() > 1f)
			{
				matrices.push();
				matrices.translate(31, 5.5, 0);
				matrices.scale(2f, 2f, 2f);
				context.drawTexture(TEXTURE, 2, 0, 83 + (player.age % 4 >= 2 ? 0 : 11),  33, 11, 11, 128, 128);
				matrices.pop();
			}
			matrices.pop();
		}
		context.setShaderColor(1f, 1f, 1f, 1f);
	}
}
