package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.item.AbstractNailgunItem;
import absolutelyaya.ultracraft.item.AttractorNailgunItem;
import absolutelyaya.ultracraft.item.OverheatNailgunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

public class WeaponInfoHUD
{
	static final Identifier TEXTURE = Ultracraft.identifier("textures/gui/weapon_info_hud.png");
	
	public void render(DrawContext context, float tickDelta)
	{
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null)
			return;
		ItemStack stack = player.getMainHandStack();
		if(stack.getItem() instanceof AbstractNailgunItem)
			renderNailgun(context, tickDelta, stack);
	}
	
	void renderNailgun(DrawContext context, float tickDelta, ItemStack stack)
	{
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();
		int x = width - 93, y = height - 47;
		
		context.fill(x, y, x + 91, y + 31, 0x88000000);
		if(stack.getItem() instanceof AttractorNailgunItem attractor)
		{
			int c = 0x68e9f8;
			String text = String.valueOf(attractor.getNbt(stack, "nails"));
			context.getMatrices().push();
			context.getMatrices().translate(x + 47 - renderer.getWidth(text), y + 5, 0);
			context.getMatrices().scale(2, 2, 2);
			context.drawText(renderer, text, 0, 0, c, false);
			context.getMatrices().pop();
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
				context.getMatrices().push();
				context.getMatrices().translate(x + 6, y + 6, 0);
				context.getMatrices().scale(2f, 2f, 2f);
				context.drawTexture(TEXTURE, 0, 0, 1, 17, Math.round(36 * f), 4, 128, 128);
				c = new Vector3f(0.4f, 0.4f, 0.4f).lerp(c, f);
				context.setShaderColor(c.x, c.y, c.z, 1f);
				context.getMatrices().translate(-2, -2, 0);
				context.drawTexture(TEXTURE, 0, 0, 38, 15, 43, 8, 128, 128);
				context.getMatrices().pop();
			}
			f = overheat.getNbt(stack, "heatsinks") / 2f;
			context.setShaderColor(0.4f, 0.4f, 0.4f, 1f);
			float partial = (1f - overheat.getNbt(stack, "heatsink_cd") / 160f) * 0.5f;
			context.drawTexture(TEXTURE, x, y + 21, 0, 24, Math.round((f + partial) * 91), 7, 128, 128);
			context.setShaderColor(0.156f, 0.874f, 0.325f, 1f);
			context.drawTexture(TEXTURE, x, y + 21, 0, 24, Math.round(f * 91), 7, 128, 128);
		}
		context.setShaderColor(1f, 1f, 1f, 1f);
	}
}
