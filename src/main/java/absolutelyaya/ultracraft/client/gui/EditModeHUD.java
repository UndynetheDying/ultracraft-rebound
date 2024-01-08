package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import absolutelyaya.ultracraft.components.player.IEditorComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class EditModeHUD
{
	public static EditModeHUD Instance;
	int maxWidth, maxHeight;
	
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
		context.fill(0, 0, maxWidth, maxHeight, 0x88000000);
		Text header = Text.of("Edit Mode Active");
		context.drawTextWithShadow(renderer, header, 2, 2, 0xffff00);
		maxWidth = renderer.getWidth(header) + 4;
		maxHeight = renderer.fontHeight + 4;
		BlockPos p;
		HashMap<String, BlockPos> focus = editor.getEditFocus();
		if((p = focus.get("room")) != null && player.getWorld().getBlockEntity(p) instanceof RoomBlockEntity room)
		{
			addLine(renderer, context, matrices, Text.of("Room: " + room.getID()), 0, 0xff8800);
			for (String key : focus.keySet())
			{
				BlockPos p2;
				if(!key.equals("room") && (p2 = focus.get(key)) != null && player.getWorld().getBlockEntity(p2) instanceof AbstractMappingBlockEntity child)
					addLine(renderer, context, matrices, Text.of(key + ": " + child.getID()), 1, 0xff8800);
			}
			addLine(renderer, context, matrices, Text.of("Children:"), 0, 0xffff00);
			for (BlockPos pos : room.getChildren())
			{
				if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity blockEntity)
					addLine(renderer, context, matrices, Text.of(blockEntity.getID()), 1, 0xffffff);
			}
			addLine(renderer, context, matrices, Text.of("Flags:"), 0, 0xffff00);
			for (String id : room.getFlags())
				addLine(renderer, context, matrices, Text.of(id), 1, room.checkFlag(id) ? 0x00cc00 : 0x880000);
		}
		else
			addLine(renderer, context, matrices, Text.of("No Room Selected"), 0, 0x888888);
		matrices.pop();
	}
	
	void addLine(TextRenderer renderer, DrawContext context, MatrixStack matrices, Text text, int indent, int color)
	{
		matrices.translate(0, renderer.fontHeight + 2, 0);
		context.drawTextWithShadow(renderer, text, 2 + indent * 5, 2, color);
		maxHeight += renderer.fontHeight + 2;
		maxWidth = Math.max(maxWidth, renderer.getWidth(text) + 5 * (indent + 1));
	}
}
