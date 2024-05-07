package absolutelyaya.ultracraft.command;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.block.mapping.FlagBindable;
import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import absolutelyaya.ultracraft.components.player.IEditorComponent;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@SuppressWarnings("SameReturnValue")
public class EditModeCommands
{
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(literal("edit").requires(source -> source.hasPermissionLevel(2))
									.executes(EditModeCommands::executeToggleEditMode)
									.then(literal("ping").executes(EditModeCommands::ping)
												  .then(literal("clear").executes(EditModeCommands::executePingClear)))
									.then(literal("name").then(key().then(argument("name", string()).executes(EditModeCommands::rename))))
									.then(literal("area").then(key().executes(EditModeCommands::editArea)))
									.then(literal("flag")
												  .then(literal("add").then(argument("id", string()).executes(EditModeCommands::addFlag)))
												  .then(literal("remove").then(flag(false).executes(EditModeCommands::removeFlag)))
												  .then(literal("set").then(flag(false).then(argument("state", bool()).executes(EditModeCommands::setFlag))))
												  .then(literal("bind").then(key().then(flag(true).executes(EditModeCommands::bindFlag)))))
									.then(literal("reparent").then(key().executes(EditModeCommands::rebindParent)))
									.then(literal("config")
												  .then(literal("flySpeed").then(argument("speed", floatArg()).executes(EditModeCommands::setFlySpeed)))
												  .then(literal("noClip").executes(EditModeCommands::executeToggleNoClip))
												  .then(literal("showAreaOwner").executes(EditModeCommands::executeToggleShowAreaOwner))
												  .then(literal("ghost").executes(EditModeCommands::executeToggleGhost)))
									.then(literal("attribute").then(literal("set").then(key().then(argument("attribute", string()).suggests(EditModeCommands::suggestAttibutes).then(argument("value", string()).executes(EditModeCommands::setAttribute)))))));
	}
	
	private static int executeToggleEditMode(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		editor.toggleEditMode();
		editor.sync();
		boolean b = editor.isActive();
		context.getSource().sendFeedback(() -> Text.translatable(b ? "command.ultracraft.edit.enable" : "command.ultracraft.edit.disable", player.getName().getString()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int ping(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		List<BlockPos> roomBlocks = new ArrayList<>();
		List<BlockPos> orphans = new ArrayList<>();
		BlockPos center = player.getBlockPos();
		World world = player.getWorld();
		for (int x = -64; x < 64; x++)
		{
			for (int y = -64; y < 64; y++)
			{
				for (int z = -64; z < 64; z++)
				{
					BlockPos pos = center.add(x, y, z);
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if(blockEntity instanceof RoomBlockEntity room)
					{
						context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.ping.room-found", room.getID(), x, y, z));
						roomBlocks.add(pos);
					}
					else if(blockEntity instanceof AbstractMappingBlockEntity block)
					{
						if(block.getParent() == null || !(world.getBlockEntity(block.getParent()) instanceof RoomBlockEntity))
						{
							context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.ping.orphan-found", block.getFocusKey(), block.getID(), x, y, z));
							orphans.add(pos);
						}
					}
				}
			}
		}
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(roomBlocks.size());
		for (BlockPos pos : roomBlocks)
			buf.writeBlockPos(pos);
		buf.writeInt(orphans.size());
		for (BlockPos pos : orphans)
			buf.writeBlockPos(pos);
		ServerPlayNetworking.send(player, PacketRegistry.EDIT_PING_PACKET_ID, buf);
		if(roomBlocks.isEmpty())
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.ping.no-results"));
		if(!orphans.isEmpty())
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.ping.orphan-count", orphans.size() ));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executePingClear(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(0);
		buf.writeInt(0);
		ServerPlayNetworking.send(player, PacketRegistry.EDIT_PING_PACKET_ID, buf);
		context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.ping.clear"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int rename(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		String key = context.getArgument("key", String.class);
		String name = context.getArgument("name", String.class);
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity e)
		{
			e.setID(name);
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.name.success"));
		}
		else
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.name.fail", key));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int editArea(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		String key = context.getArgument("key", String.class);
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		BlockPos pos = editor.getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity block)
		{
			if(!block.isAreaModifiable())
			{
				context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.area.not-resizeable", key));
				return Command.SINGLE_SUCCESS;
			}
			editor.setEditAreaStep(2);
			editor.setEditAreaCore(pos);
			editor.sync();
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.area.begin"));
		}
		else
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.nothing-focused", key));
		return Command.SINGLE_SUCCESS;
	}
	
	private static BlockPos getSelectedRoom(PlayerEntity player)
	{
		return UltraComponents.EDITOR.get(player).getEditFocus("room");
	}
	
	private static int addFlag(CommandContext<ServerCommandSource> context)
	{
		PlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		BlockPos roomPos = getSelectedRoom(player);
		if(roomPos == null)
		{
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.no-room"));
			return Command.SINGLE_SUCCESS;
		}
		String id = context.getArgument("id", String.class);
		if(player.getWorld().getBlockEntity(roomPos) instanceof RoomBlockEntity room)
		{
			room.registerFlag(id);
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.flag.add", id));
		}
		else
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.room-but-no-room"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int removeFlag(CommandContext<ServerCommandSource> context)
	{
		PlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		BlockPos roomPos = getSelectedRoom(player);
		if(roomPos == null)
		{
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.no-room"));
			return Command.SINGLE_SUCCESS;
		}
		String flag = context.getArgument("flag", String.class);
		if(player.getWorld().getBlockEntity(roomPos) instanceof RoomBlockEntity room)
			context.getSource().sendMessage(Text.translatable(room.removeFlag(flag) ? "command.ultracraft.edit.flag.remove.success" : "command.ultracraft.edit.flag.remove.nothing-there", flag));
		else
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.room-but-no-room"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setFlag(CommandContext<ServerCommandSource> context)
	{
		PlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		BlockPos roomPos = getSelectedRoom(player);
		if(roomPos == null)
		{
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.no-room"));
			return Command.SINGLE_SUCCESS;
		}
		String flag = context.getArgument("flag", String.class);
		boolean state = context.getArgument("state", Boolean.class);
		if(player.getWorld().getBlockEntity(roomPos) instanceof RoomBlockEntity room)
		{
			if(room.setFlag(flag, state))
				context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.flag.set.success", flag, state));
			else
				context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.flag.set.fail", flag));
		}
		else
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.room-but-no-room"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int bindFlag(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		String key = context.getArgument("key", String.class);
		String flag = context.getArgument("flag", String.class);
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof FlagBindable e)
		{
			e.bindFlag(flag);
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.flag.bind", flag, key));
		}
		else
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.nothing-focused"));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int rebindParent(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		String key = context.getArgument("key", String.class);
		if(key.equals("room"))
		{
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.rebind.room"));
			return Command.SINGLE_SUCCESS;
		}
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity entity)
		{
			UltraComponents.EDITOR.get(player).setRebindingParent(pos);
			BlockPos lastParent = entity.getParent();
			entity.setParent(null);
			if(player.getWorld().getBlockEntity(lastParent) instanceof RoomBlockEntity room)
				room.removeChild(pos);
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.rebind.start", key));
		}
		else
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.nothing-focused"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setFlySpeed(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		float speed = context.getArgument("speed", Float.class);
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		editor.setFlySpeed(speed);
		editor.sync();
		context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.config.speed", speed));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeToggleNoClip(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		boolean b = editor.toggleNoClip();
		editor.sync();
		context.getSource().sendMessage(Text.translatable(b ? "command.ultracraft.edit.config.no-clip.on" : "command.ultracraft.edit.config.no-clip.off"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeToggleShowAreaOwner(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		boolean b = editor.toggleShowAreaOwner();
		editor.sync();
		context.getSource().sendMessage(Text.translatable(b ? "command.ultracraft.edit.config.show-area-owner.on" : "command.ultracraft.edit.config.show-area-owner.off"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeToggleGhost(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		boolean b = editor.toggleGhost();
		editor.sync();
		context.getSource().sendMessage(Text.translatable(b ? "command.ultracraft.edit.config.ghost.on" : "command.ultracraft.edit.config.ghost.off"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static CompletableFuture<Suggestions> suggestAttibutes(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return builder.buildFuture();
		String key = context.getArgument("key", String.class);
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity e)
			e.getAttributes().forEach(builder::suggest);
		return builder.buildFuture();
	}
	
	private static int setAttribute(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return Command.SINGLE_SUCCESS;
		String key = context.getArgument("key", String.class);
		String attribute = context.getArgument("attribute", String.class);
		String value = context.getArgument("value", String.class);
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity e)
		{
			List<String> attributes = e.getAttributes();
			if(!attributes.contains(attribute))
			{
				context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.attribute.not-found", attribute, key));
				return Command.SINGLE_SUCCESS;
			}
			try
			{
				e.setAttribute(attribute, value);
			}
			catch (AbstractMappingBlockEntity.AttributeParseException exception)
			{
				if(exception.getExpectedDataType().equals("identifier"))
					context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.attribute.identifier-parse-failed"));
				else
					context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.attribute.parse-failed", exception.getExpectedDataType()));
				return Command.SINGLE_SUCCESS;
			}
			catch (NumberFormatException exception)
			{
				context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.attribute.number-parse-failed"));
				return Command.SINGLE_SUCCESS;
			}
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.attribute.set", attribute, value, key));
		}
		else
			context.getSource().sendMessage(Text.translatable("command.ultracraft.edit.nothing-focused", key));
		return Command.SINGLE_SUCCESS;
	}
	
	private static RequiredArgumentBuilder<ServerCommandSource, String> key()
	{
		return argument("key", string()).suggests(EditModeCommands::suggestKeys);
	}
	
	private static CompletableFuture<Suggestions> suggestKeys(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return builder.buildFuture();
		UltraComponents.EDITOR.get(player).getEditFocus().keySet().forEach(builder::suggest);
		return builder.buildFuture();
	}
	
	private static RequiredArgumentBuilder<ServerCommandSource, String> flag(boolean allowGlobal)
	{
		return argument("flag", string()).suggests(allowGlobal ? EditModeCommands::suggestLocalOrGlobalFlags : EditModeCommands::suggestLocalFlags);
	}
	
	private static CompletableFuture<Suggestions> suggestLocalOrGlobalFlags(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return builder.buildFuture();
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		World world = player.getWorld();
		BlockPos targetPos = editor.getEditFocus(context.getArgument("key", String.class));
		boolean global = targetPos != null && world.getBlockEntity(targetPos) instanceof FlagBindable flagged && flagged.isGlobal();
		if(!global)
		{
			BlockPos pos = editor.getEditFocus("room");
			if(pos != null && world.getBlockEntity(pos) instanceof RoomBlockEntity e)
				e.getFlags().forEach(builder::suggest);
		}
		else
			UltraComponents.DIMENSION_DATA.get(world).getAllFlags().keySet().forEach(builder::suggest);
		return builder.buildFuture();
	}
	
	private static CompletableFuture<Suggestions> suggestLocalFlags(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		if(player == null)
			return builder.buildFuture();
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		World world = player.getWorld();
		BlockPos pos = editor.getEditFocus("room");
		if(pos != null && world.getBlockEntity(pos) instanceof RoomBlockEntity e)
			e.getFlags().forEach(builder::suggest);
		return builder.buildFuture();
	}
}
