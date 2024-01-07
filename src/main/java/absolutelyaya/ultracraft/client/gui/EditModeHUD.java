package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import absolutelyaya.ultracraft.components.player.IEditorComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class EditModeHUD
{
	public static EditModeHUD Instance;
	
	public EditModeHUD()
	{
		Instance = this;
	}
	
	public void render(DrawContext context, float tickDelta)
	{
		PlayerEntity player = MinecraftClient.getInstance().player;
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		if(!editor.isActive())
			return;
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		int width = context.getScaledWindowWidth();
		int height = context.getScaledWindowHeight();
		context.fill(width / 2 - 64, 0, width / 2 + 64, 64, 0x88000000);
		context.drawCenteredTextWithShadow(renderer, Text.of("Edit Mode Active"), width / 2, 2, 0xffff00);
		BlockPos p;
		if((p = editor.getEditFocus("room")) != null && player.getWorld().getBlockEntity(p) instanceof RoomBlockEntity level)
		{
			matrices.translate(0, renderer.fontHeight + 2, 0);
			context.drawCenteredTextWithShadow(renderer, Text.of("Room: " + level.getID()), width / 2, 2, 0xffff00);
		}
		matrices.pop();
	}
}
