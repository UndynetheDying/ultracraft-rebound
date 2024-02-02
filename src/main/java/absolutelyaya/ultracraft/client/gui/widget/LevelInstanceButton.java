package absolutelyaya.ultracraft.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.function.Consumer;

public class LevelInstanceButton extends ClickableWidget
{
	final TextRenderer tRenderer;
	//final ServerPlayerEntity owner;
	final PlayerListEntry ownerPlayerListEntry;
	final Consumer<String> action;
	final String id;
	
	public LevelInstanceButton(int x, int y, String id, UUID owner, Consumer<String> action)
	{
		super(x, y, 32, 32, Text.of(""));
		MinecraftClient client = MinecraftClient.getInstance();
		this.id = id;
		if(client.player.networkHandler.getPlayerUuids().contains(owner))
		{
			ownerPlayerListEntry = client.player.networkHandler.getPlayerListEntry(owner);
			Text name = ownerPlayerListEntry.getDisplayName();
			setMessage(name == null ? Text.of(ownerPlayerListEntry.getProfile().getName()) : name);
		}
		else
		{
			setMessage(Text.translatable("screen.ultracraft.travel.instance.unowned"));
			ownerPlayerListEntry = null;
		}
		tRenderer = client.textRenderer;
		setWidth(Math.max(tRenderer.getWidth(getMessage()), 32));
		height += tRenderer.fontHeight + 2;
		this.action = action;
	}
	
	@Override
	protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
	{
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(getX(), getY(), 0);
		context.fill(-2, -2, width + 2, 36 + tRenderer.fontHeight, 0x88000000);
		if(hovered)
			context.drawBorder(-2, -2, width + 4, 38 + tRenderer.fontHeight, 0xffffffff);
		Text name = getMessage();
		context.drawText(tRenderer, name, (width - tRenderer.getWidth(name)) / 2, 0, 0xffffffff, true);
		matrices.translate(0, tRenderer.fontHeight + 2, 0);
		if (ownerPlayerListEntry != null)
			PlayerSkinDrawer.draw(context, ownerPlayerListEntry.getSkinTexture(), width / 2 - 16, 0, 32, true, false);
		matrices.pop();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(hovered)
		{
			action.accept(id);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder)
	{
		builder.put(NarrationPart.TITLE, getMessage());
	}
}
