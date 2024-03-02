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
												  .then(literal("remove").then(flag().executes(EditModeCommands::removeFlag)))
												  .then(literal("set").then(flag().then(argument("state", bool()).executes(EditModeCommands::setFlag))))
												  .then(literal("bind").then(key().then(flag().executes(EditModeCommands::bindFlag)))))
									.then(literal("reparent").then(key().executes(EditModeCommands::rebindParent)))
									.then(literal("config")
												  .then(literal("flySpeed").then(argument("speed", floatArg()).executes(EditModeCommands::setFlySpeed)))
												  .then(literal("noClip").executes(EditModeCommands::executeToggleNoClip))
												  .then(literal("showAreaOwner").executes(EditModeCommands::executeToggleShowAreaOwner)))
									.then(literal("attribute").then(literal("set").then(key().then(argument("attribute", string()).suggests(EditModeCommands::suggestAttibutes).then(argument("value", string()).executes(EditModeCommands::setAttribute)))))));
	}
	
	private static int executeToggleEditMode(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		editor.toggleEditMode();
		editor.sync();
		boolean b = editor.isActive();
		context.getSource().sendFeedback(() -> Text.of(player.getName().getString() + (b ? " has entered edit mode" : " has left edit mode")), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int ping(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
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
						context.getSource().sendMessage(Text.of("Room found: '" + room.getID() + "' at " + x + " " + y + " " + z));
						roomBlocks.add(pos);
					}
					else if(blockEntity instanceof AbstractMappingBlockEntity block)
					{
						if(block.getParent() == null || !(world.getBlockEntity(block.getParent()) instanceof RoomBlockEntity))
						{
							context.getSource().sendMessage(Text.of("Orphan " + block.getFocusKey() + " found: '" +
																			block.getID() + "' at " + x + " " + y + " " + z));
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
		if(roomBlocks.size() == 0)
			context.getSource().sendMessage(Text.of("No Rooms :("));
		if(orphans.size() > 0)
			context.getSource().sendMessage(Text.of(orphans.size() + " Orphans found."));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executePingClear(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeInt(0);
		buf.writeInt(0);
		ServerPlayNetworking.send(player, PacketRegistry.EDIT_PING_PACKET_ID, buf);
		context.getSource().sendMessage(Text.of("Ping Results cleared"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int rename(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		String key = context.getArgument("key", String.class);
		String name = context.getArgument("name", String.class);
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity e)
		{
			e.setID(name);
			context.getSource().sendMessage(Text.of("Renamed successfully"));
		}
		else
			context.getSource().sendMessage(Text.of("Nothing focused with key '" + key + "'"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int editArea(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		String key = context.getArgument("key", String.class);
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		BlockPos pos = editor.getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity block)
		{
			if(!block.isAreaModifiable())
			{
				context.getSource().sendMessage(Text.of("Area of key '" + key + "' is not resizeable"));
				return Command.SINGLE_SUCCESS;
			}
			editor.setEditAreaStep(2);
			editor.setEditAreaCore(pos);
			editor.sync();
			context.getSource().sendMessage(Text.of("Ready to change Area; Right Click 2 Blocks"));
		}
		else
			context.getSource().sendMessage(Text.of("Nothing focused with key '" + key + "'"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static BlockPos getSelectedRoom(PlayerEntity player)
	{
		return UltraComponents.EDITOR.get(player).getEditFocus("room");
	}
	
	private static int addFlag(CommandContext<ServerCommandSource> context)
	{
		PlayerEntity player = context.getSource().getPlayer();
		BlockPos roomPos = getSelectedRoom(player);
		if(roomPos == null)
		{
			context.getSource().sendMessage(Text.of("No Room Selected"));
			return Command.SINGLE_SUCCESS;
		}
		String id = context.getArgument("id", String.class);
		if(player.getWorld().getBlockEntity(roomPos) instanceof RoomBlockEntity room)
		{
			room.registerFlag(id);
			context.getSource().sendMessage(Text.of("Flag '" + id + "' registered"));
		}
		else
			context.getSource().sendMessage(Text.of("Error: focused room pos is not a room"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int removeFlag(CommandContext<ServerCommandSource> context)
	{
		PlayerEntity player = context.getSource().getPlayer();
		BlockPos roomPos = getSelectedRoom(player);
		if(roomPos == null)
		{
			context.getSource().sendMessage(Text.of("No Room Selected"));
			return Command.SINGLE_SUCCESS;
		}
		String flag = context.getArgument("flag", String.class);
		if(player.getWorld().getBlockEntity(roomPos) instanceof RoomBlockEntity room)
			context.getSource().sendMessage(Text.of("Flag '" + flag + "' " + (room.removeFlag(flag) ? "removed" : "wasn't there to begin with")));
		else
			context.getSource().sendMessage(Text.of("Error: focused room pos is not a room"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setFlag(CommandContext<ServerCommandSource> context)
	{
		PlayerEntity player = context.getSource().getPlayer();
		BlockPos roomPos = getSelectedRoom(player);
		if(roomPos == null)
		{
			context.getSource().sendMessage(Text.of("No Room Selected"));
			return Command.SINGLE_SUCCESS;
		}
		String flag = context.getArgument("flag", String.class);
		boolean state = context.getArgument("state", Boolean.class);
		if(player.getWorld().getBlockEntity(roomPos) instanceof RoomBlockEntity room)
		{
			if(room.setFlag(flag, state))
				context.getSource().sendMessage(Text.of("Flag '" + flag + "' set to " + state));
			else
				context.getSource().sendMessage(Text.of("Flag '" + flag + "' doesn't exist"));
		}
		else
			context.getSource().sendMessage(Text.of("Error: focused room pos is not a room"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int bindFlag(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		String key = context.getArgument("key", String.class);
		String flag = context.getArgument("flag", String.class);
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof FlagBindable e)
		{
			e.bindFlag(flag);
			context.getSource().sendMessage(Text.of("Bound Flag '" + flag + "' to focused key '" + key + "'"));
		}
		else
			context.getSource().sendMessage(Text.of("Nothing focused with key '" + key + "'"));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int rebindParent(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		String key = context.getArgument("key", String.class);
		if(key.equals("room"))
		{
			context.getSource().sendMessage(Text.of("Rooms don't have Parents."));
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
			context.getSource().sendMessage(Text.of("Rebinding Parent of key '" + key + "'"));
		}
		else
			context.getSource().sendMessage(Text.of("Nothing focused with key '" + key + "'"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int setFlySpeed(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		float speed = context.getArgument("speed", Float.class);
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		editor.setFlySpeed(speed);
		editor.sync();
		context.getSource().sendMessage(Text.of(("Edit mode Fly Speed set to " + speed)));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeToggleNoClip(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		editor.toggleNoClip();
		editor.sync();
		boolean b = editor.isNoClip();
		context.getSource().sendMessage(Text.of((b ? " activated NoClip in edit mode" : " deactivated NoClip in edit mode")));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeToggleShowAreaOwner(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		editor.toggleShowAreaOwner();
		editor.sync();
		boolean b = editor.isShowAreaOwner();
		context.getSource().sendMessage(Text.of((b ? " lines between areas and owners are now shown" : " lines between areas and owners are now hidden")));
		return Command.SINGLE_SUCCESS;
	}
	
	private static CompletableFuture<Suggestions> suggestAttibutes(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		String key = context.getArgument("key", String.class);
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity e)
			e.getAttributes().forEach(builder::suggest);
		return builder.buildFuture();
	}
	
	private static int setAttribute(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		String key = context.getArgument("key", String.class);
		String attribute = context.getArgument("attribute", String.class);
		String value = context.getArgument("value", String.class);
		BlockPos pos = UltraComponents.EDITOR.get(player).getEditFocus(key);
		if(pos != null && player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity e)
		{
			List<String> attributes = e.getAttributes();
			if(!attributes.contains(attribute))
			{
				context.getSource().sendMessage(Text.of("Attribute '" + attribute + "' not found on focused key '" + key + "'"));
				return Command.SINGLE_SUCCESS;
			}
			e.setAttribute(attribute, value);
			context.getSource().sendMessage(Text.of("Set Attribute '" + attribute + "' to '" + value + "' on focused key '" + key + "'"));
		}
		else
			context.getSource().sendMessage(Text.of("Nothing focused with key '" + key + "'"));
		return Command.SINGLE_SUCCESS;
	}
	
	private static RequiredArgumentBuilder<ServerCommandSource, String> key()
	{
		return argument("key", string()).suggests(EditModeCommands::suggestKeys);
	}
	
	private static CompletableFuture<Suggestions> suggestKeys(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		UltraComponents.EDITOR.get(player).getEditFocus().keySet().forEach(builder::suggest);
		return builder.buildFuture();
	}
	
	private static RequiredArgumentBuilder<ServerCommandSource, String> flag()
	{
		return argument("flag", string()).suggests(EditModeCommands::suggestFlags);
	}
	
	private static CompletableFuture<Suggestions> suggestFlags(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
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
}
