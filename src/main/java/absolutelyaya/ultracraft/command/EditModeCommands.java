package absolutelyaya.ultracraft.command;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.LevelBlock;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class EditModeCommands
{
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(literal("editmode").requires(source -> source.hasPermissionLevel(2))
									.then(literal("edit").executes(EditModeCommands::executeToggleEditMode))
									.then(literal("ping").requires(i -> UltraComponents.WINGED_ENTITY.get(i.getPlayer()).isEditMode()).executes(EditModeCommands::executePing)));
	}
	
	private static int executeToggleEditMode(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		IWingedPlayerComponent winged = UltraComponents.WINGED_ENTITY.get(player);
		winged.toggleEditMode();
		boolean b = winged.isEditMode();
		context.getSource().sendFeedback(() -> Text.of(player.getName() + (b ? " has entered edit mode" : " has left edit mode")), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executePing(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		List<BlockPos> levelBlocks = new ArrayList<>();
		BlockPos center = player.getBlockPos();
		World world = player.getWorld();
		for (int x = -64; x < 64; x++)
		{
			for (int y = -64; y < 64; y++)
			{
				for (int z = -64; z < 64; z++)
				{
					if(world.getBlockState(center.add(x, y, z)).getBlock() instanceof LevelBlock level)
					{
						context.getSource().sendMessage(Text.of("Level found: '" + level.getId() + "' at " + x + " " + y + " " + z));
						levelBlocks.add(center.add(x, y, z));
					}
				}
			}
		}
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(levelBlocks.size());
		for (BlockPos pos : levelBlocks)
			buf.writeBlockPos(pos);
		ServerPlayNetworking.send(player, PacketRegistry.EDIT_PING_PACKET_ID, buf);
		if(levelBlocks.size() == 0)
			context.getSource().sendMessage(Text.of("no levels :("));
		return Command.SINGLE_SUCCESS;
	}
}
