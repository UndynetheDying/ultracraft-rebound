package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.client.UltracraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class EditModeHUD
{
	public static EditModeHUD Instance;
	
	public EditModeHUD()
	{
		Instance = this;
	}
	
	public void render(DrawContext context, float tickDelta)
	{
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if(player == null || !UltracraftClient.isEditMode())
			return;
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();
		context.fill(width / 2 - 64, 0, width / 2 + 64, 64, 0x88000000);
		context.drawCenteredTextWithShadow(renderer, Text.of("Edit Mode Active"), width / 2, 2, 0xffff00);
	}
}
