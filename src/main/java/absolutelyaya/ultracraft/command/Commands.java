package absolutelyaya.ultracraft.command;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.level.IUltraLevelComponent;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.components.world.IDimensionDataComponent;
import absolutelyaya.ultracraft.config.ServerConfig;
import absolutelyaya.ultracraft.config.Setting;
import absolutelyaya.ultracraft.data.LevelData;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.data.StyleBonusManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.cybergrind.CybergrindManager;
import absolutelyaya.ultracraft.entity.machine.DestinyBondSwordsmachineEntity;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.BossBarCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.command.argument.DimensionArgumentType.dimension;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.command.argument.IdentifierArgumentType.identifier;
import static net.minecraft.command.argument.TextArgumentType.text;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands
{
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
	{
		dispatcher.register(literal("ultracraft")
			.then(literal("info").requires(ServerCommandSource::isExecutedByPlayer).executes(Commands::executeInfo))
			.then(literal("config").requires(ServerCommandSource::isExecutedByPlayer).requires(source -> source.hasPermissionLevel(2)).executes(Commands::executeConfig))
			.then(literal("block").requires(ServerCommandSource::isExecutedByPlayer).then(argument("target", player()).executes(Commands::executeBlock)))
			.then(literal("unblock").requires(ServerCommandSource::isExecutedByPlayer).then(argument("target", player()).executes(Commands::executeUnblock)))
			.then(literal("time").requires(source -> source.hasPermissionLevel(2))
				.then(literal("freeze").then(argument("ticks", integer(1)).executes(Commands::executeFreeze)))
				.then(literal("unfreeze").executes(Commands::executeUnfreeze)))
			.then(literal("debug").requires(source -> source.hasPermissionLevel(2))
				.then(literal("ricoshot_warn").then(argument("pos", Vec3ArgumentType.vec3()).executes(Commands::executeDebugRicoshotWarn)))
				.then(literal("screenshake").then(argument("strength", FloatArgumentType.floatArg()).executes(Commands::executeDebugScreenshake)))
				.then(literal("levels").requires(ServerCommandSource::isExecutedByPlayer)
					.then(literal("instance-everything-several-times").then(literal("confirm").executes(Commands::executeDebugLevelInstancing)).executes(Commands::executeDebugLevelInstancingWarning))
					.then(literal("destroy-all-instances").executes(Commands::executeDebugLevelDestruction)))
				.then(literal("cybergrind")
					.then(literal("start").executes(Commands::executeDebugStartCybergrind)
						.then(argument("target", player()).then(argument("waves", integer(0)).executes(Commands::executeDebugCybergrindSpecific).then(literal("verbose").executes(Commands::executeDebugCybergrindSpecificVerbose)))))
					.then(literal("end").executes(Commands::executeDebugEndCybergrind)))
				.then(literal("flag").then(argument("dimension", dimension())
					.then(literal("set").then(argument("id", string()).suggests(Commands::globalFlagForWorldProvider).then(argument("value", integer()).executes(Commands::executeDebugSetGlobalFlag))))
					.then(literal("get").then(argument("id", string()).suggests(Commands::globalFlagForWorldProvider).executes(Commands::executeDebugGetGlobalFlag)))))
				.then(literal("records").then(literal("reset").then(argument("target", players())
					.then(argument("level", identifier()).suggests(Commands::levelIdProvider)
						.then(literal("time").executes(Commands::executeDebugResetBestTime))
						.then(literal("rank").executes(Commands::executeDebugResetBestRank)))
					.then(literal("all")
						.then(literal("time").executes(Commands::executeDebugResetAllBestTimes))
						.then(literal("rank").executes(Commands::executeDebugResetAllBestRanks))))))
				.then(literal("clearLikelyPerTickDamageTypes").executes(Commands::executeDebugClearLikelyPerTickDamageTypes)))
			.then(literal("progression").requires(source -> source.hasPermissionLevel(2))
				.then(argument("list", string()).suggests(Commands::progressionListTypeProvider)
					.then(literal("list").then(argument("target", player()).executes(Commands::executeProgressionList)))
					.then(literal("grant").then(argument("target", players()).then(argument("entry", identifier()).suggests(Commands::progressionOptionsProvider).executes(Commands::executeProgressionGrant))))
					.then(literal("grant-all").then(argument("target", players()).executes(Commands::executeProgressionGrantAll)))
					.then(literal("revoke").then(argument("target", players()).then(argument("entry", identifier()).suggests(Commands::progressionListProvider).executes(Commands::executeProgressionRevoke)))))
				.then(literal("reset").then(argument("target", players()).executes(Commands::executeProgressionReset))))
			.then(literal("ultrabossbar").requires(source -> source.hasPermissionLevel(2)).then(argument("id", identifier()).executes(Commands::executeUltraBossbar)))
			.then(literal("style").then(argument("target", players()).then(argument("entry", identifier()).suggests(Commands::styleBonusProvider).executes(Commands::executeStyle))))
			.then(literal("title").then(argument("target", players()).then(argument("type", string()).suggests((context, builder) -> CommandSource.suggestMatching(List.of("large", "box"), builder)).then(argument("text", text()).executes(Commands::executeTitle))))));
		dispatcher.register(literal("ultrasummon").requires(source -> source.hasPermissionLevel(2)).then(argument("type", string()).suggests((context, builder) -> CommandSource.suggestMatching(List.of("\"tundra//agony\""), builder)).then(argument("pos", Vec3ArgumentType.vec3()).then(argument("yaw", DoubleArgumentType.doubleArg()).executes(Commands::executeSpecialSpawn)))));
	}
	
	static int executeInfo(CommandContext<ServerCommandSource> context)
	{
		ServerPlayNetworking.send(context.getSource().getPlayer(), PacketRegistry.WORLD_INFO_PACKET_ID, new PacketByteBuf(Unpooled.buffer()));
		return Command.SINGLE_SUCCESS;
	}
	
	static int executeConfig(CommandContext<ServerCommandSource> context)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeNbt(ServerConfig.INSTANCE.getAsNBT());
		ServerPlayNetworking.send(context.getSource().getPlayer(), PacketRegistry.OPEN_SERVER_CONFIG_MENU_PACKET_ID, buf);
		return Command.SINGLE_SUCCESS;
	}
	
	static int executeBlock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
		if(context.getSource().getPlayer().equals(target))
		{
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.block.self"), false);
			return Command.SINGLE_SUCCESS;
		}
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeUuid(target.getUuid());
		ServerPlayNetworking.send(context.getSource().getPlayer(), PacketRegistry.BLOCK_PLAYER_PACKET_ID, buf);
		return Command.SINGLE_SUCCESS;
	}
	
	static int executeUnblock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
		if(context.getSource().getPlayer().equals(target))
		{
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.block.self"), false);
			return Command.SINGLE_SUCCESS;
		}
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeUuid(target.getUuid());
		ServerPlayNetworking.send(context.getSource().getPlayer(), PacketRegistry.UNBLOCK_PLAYER_PACKET_ID, buf);
		return Command.SINGLE_SUCCESS;
	}
	
	static int executeFreeze(CommandContext<ServerCommandSource> context)
	{
		int ticks = context.getArgument("ticks", Integer.class);
		String senderName = context.getSource().getPlayer().getName().getString();
		if(ServerConfig.INSTANCE.timestop.getValue().equals(Setting.FORCE_OFF))
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.time-freeze.fail"), false);
		else
		{
			Ultracraft.freeze(context.getSource().getWorld(), ticks);
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.time-freeze.success", senderName, ticks), true);
		}
		return Command.SINGLE_SUCCESS;
	}
	
	static int executeUnfreeze(CommandContext<ServerCommandSource> context)
	{
		String senderName = context.getSource().getPlayer().getName().getString();
		if(Ultracraft.isTimeFrozen())
		{
			Ultracraft.cancelFreeze(context.getSource().getWorld());
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.time-unfreeze.success", senderName), true);
		}
		else
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.time-unfreeze.fail"), false);
		return Command.SINGLE_SUCCESS;
	}
	
	static int executeDebugRicoshotWarn(CommandContext<ServerCommandSource> context)
	{
		Vec3d v = context.getArgument("pos", PosArgument.class).toAbsolutePos(context.getSource());
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVector3f(v.toVector3f());
		buf.writeUuid(context.getSource().getPlayer().getUuid());
		ServerPlayNetworking.send(context.getSource().getPlayer(), PacketRegistry.RICOCHET_WARNING_PACKET_ID, buf);
		return Command.SINGLE_SUCCESS;
	}
	
	static int executeSpecialSpawn(CommandContext<ServerCommandSource> context)
	{
		Vec3d pos = context.getArgument("pos", PosArgument.class).toAbsolutePos(context.getSource());
		double yaw = context.getArgument("yaw", Double.class);
		
		String type = context.getArgument("type", String.class);
		TriFunction<World, Vec3d, Float, List<Entity>> spawnConsumer = switch(type)
		{
			case "tundra//agony" -> DestinyBondSwordsmachineEntity::spawn;
			default -> {
				context.getSource().sendError(Text.of("Invalid type: '" + type));
				yield null;
			}
		};
		if(spawnConsumer != null)
			spawnConsumer.apply(context.getSource().getWorld(), pos, (float)yaw);
		return Command.SINGLE_SUCCESS;
	}
	
	static CompletableFuture<Suggestions> progressionListTypeProvider(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		return builder.suggest("unlocked").suggest("obtained").suggest("level").buildFuture();
	}
	
	static List<Identifier> getProgressionList(ServerPlayerEntity target, String type)
	{
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(target);
		return switch(type)
		{
			case "unlocked" -> progression.getUnlockedList();
			case "obtained" -> progression.getOwnedList();
			case "level" -> {
				IUltraLevelComponent global = UltraComponents.GLOBAL.get(target.getWorld().getLevelProperties());
				yield global.getUnlockedDestinationList();
			}
			default -> new ArrayList<>();
		};
	}
	
	private static CompletableFuture<Suggestions> progressionListProvider(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException
	{
		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
		String list = context.getArgument("list", String.class);
		getProgressionList(target, list).forEach(i -> builder.suggest(i.toString()));
		return builder.buildFuture();
	}
	
	private static CompletableFuture<Suggestions> progressionOptionsProvider(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException
	{
		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
		String list = context.getArgument("list", String.class);
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(target);
		switch(list)
		{
			case "unlocked", "obtained" -> progression.getAllGearEntries().forEach(id -> builder.suggest(id.toString()));
			case "level" -> levelIdProvider(context, builder);
			default -> new ArrayList<>();
		}
		return builder.buildFuture();
	}
	
	private static int executeProgressionList(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
		String type = context.getArgument("list", String.class);
		StringBuilder builder = new StringBuilder(Text.translatable("command.ultracraft.progression.list-prefix", target.getName(), type).getString());
		List<Identifier> list = getProgressionList(target, type);
		for (int i = 0; i < list.size(); i++)
		{
			builder.append(i % 2 == 0 ? "§6" : "§e");
			builder.append(list.get(i));
			if(i < list.size() - 1)
				builder.append(", ");
		}
		if(list.size() == 0)
			builder.append(Text.translatable("command.ultracraft.progression.list-empty").getString());
		context.getSource().sendFeedback(() -> Text.of(builder.toString()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeProgressionGrant(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		String type = context.getArgument("list", String.class);
		Identifier entry = IdentifierArgumentType.getIdentifier(context, "entry");
		for (ServerPlayerEntity target : targets)
		{
			IProgressionComponent progression = UltraComponents.PROGRESSION.get(target);
			switch(type)
			{
				case "unlocked" -> progression.unlock(entry);
				case "obtained" -> progression.obtain(entry);
				case "level" -> {
					IUltraLevelComponent global = UltraComponents.GLOBAL.get(target.getWorld().getLevelProperties());
					global.unlockDestination(entry);
				}
				default -> {
					context.getSource().sendError(Text.translatable("command.ultracraft.progression.invalid_list"));
					return Command.SINGLE_SUCCESS;
				}
			}
			progression.sync();
		}
		int size = targets.size();
		if(size == 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.progression.grant-success", entry, type,
					((ServerPlayerEntity)targets.toArray()[0]).getName()), true);
		else
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.progression.grant-multi-success", entry, type, size), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeProgressionRevoke(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		String type = context.getArgument("list", String.class);
		Identifier entry = IdentifierArgumentType.getIdentifier(context, "entry");
		for (ServerPlayerEntity target : targets)
		{
			IProgressionComponent progression = UltraComponents.PROGRESSION.get(target);
			switch (type)
			{
				case "unlocked" -> progression.lock(entry);
				case "obtained" -> progression.disown(entry);
				case "level" -> {
					IUltraLevelComponent global = UltraComponents.GLOBAL.get(target.getWorld().getLevelProperties());
					global.lockDestination(entry);
				}
				default ->
				{
					context.getSource().sendError(Text.translatable("command.ultracraft.progression.invalid_list"));
					return Command.SINGLE_SUCCESS;
				}
			}
			progression.sync();
		}
		int size = targets.size();
		if(size == 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.progression.revoke-success", entry, type,
					((ServerPlayerEntity)targets.toArray()[0]).getName()), true);
		else
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.progression.revoke-multi-success", entry, type, size), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeProgressionReset(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		for (ServerPlayerEntity target : targets)
		{
			IProgressionComponent progression = UltraComponents.PROGRESSION.get(target);
			progression.reset();
			progression.sync();
		}
		int size = targets.size();
		if(size == 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.progression.reset-success",
					((ServerPlayerEntity)targets.toArray()[0]).getName()), true);
		else
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.progression.reset-multi-success", size), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeProgressionGrantAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		String type = context.getArgument("list", String.class);
		for (ServerPlayerEntity target : targets)
		{
			IProgressionComponent progression = UltraComponents.PROGRESSION.get(target);
			switch(type)
			{
				case "unlocked" -> progression.unlockAll();
				case "obtained" -> progression.obtainAll();
				case "level" -> {
					IUltraLevelComponent global = UltraComponents.GLOBAL.get(target.getWorld().getLevelProperties());
					global.unlockAllDestinations();
				}
				default -> {
					context.getSource().sendError(Text.translatable("command.ultracraft.progression.invalid_list"));
					return Command.SINGLE_SUCCESS;
				}
			}
			progression.sync();
		}
		int size = targets.size();
		if(size == 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.progression.grant-all-success", type,
					((ServerPlayerEntity)targets.toArray()[0]).getName()), true);
		else
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.progression.grant-all-multi-success", type, size), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeUltraBossbar(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		CommandBossBar bossBar = BossBarCommand.getBossBar(context);
		bossBar.setStyle(ClassTinkerers.getEnum(BossBar.Style.class, "ULTRA"));
		context.getSource().sendFeedback(() -> Text.translatable("commands.bossbar.set.style.success", bossBar.toHoverableText()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugScreenshake(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		Ultracraft.screenshake(player, context.getArgument("strength", Float.class));
		return Command.SINGLE_SUCCESS;
	}
	
	private static CompletableFuture<Suggestions> styleBonusProvider(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		StyleBonusManager.getBonuses().keySet().forEach(i -> builder.suggest(i.toString()));
		return builder.buildFuture();
	}
	
	private static int executeStyle(CommandContext<ServerCommandSource> context)
	{
		ServerPlayerEntity player = context.getSource().getPlayer();
		Identifier entry = IdentifierArgumentType.getIdentifier(context, "entry");
		if(!StyleBonusManager.getBonuses().containsKey(entry))
		{
			context.getSource().sendError(Text.translatable("command.ultracraft.style.notfound", entry));
			return Command.SINGLE_SUCCESS;
		}
		UltraComponents.STYLE.get(player).styleBonusGet(StyleBonusManager.getBonuses().get(entry));
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.style.success", entry, player.getName()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeTitle(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		String type = context.getArgument("type", String.class);
		Text text = context.getArgument("text", Text.class);
		for (ServerPlayerEntity player : targets)
		{
			IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
			if(type.equals("large"))
				winged.sendBigTitle(text);
			else
				winged.sendBoxTitle(text);
			if(targets.size() == 1)
				context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.title.success-single", type, player.getName()), true);
		}
		if(targets.size() > 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.title.success-multiple", type, targets.size()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugLevelInstancingWarning(CommandContext<ServerCommandSource> context)
	{
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level-instance.warn"), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugLevelInstancing(CommandContext<ServerCommandSource> context)
	{
		int levels = LevelDataManager.levels.size() + LevelDataManager.customLevels.size();
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level-instance.start", levels, levels * 8), true);
		LevelManager.Instance.debugInstanceEverythingALot();
		FabricDimensions.teleport(context.getSource().getPlayer(), LevelManager.Instance.getWorld(),
				new TeleportTarget(new Vec3d(0, 64, 0), Vec3d.ZERO, 0f, 0f));
		context.getSource().getPlayer();
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level-instance.finish"), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugLevelDestruction(CommandContext<ServerCommandSource> context)
	{
		LevelManager.Instance.destroyAllInstances();
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level-instance.destroy"), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugStartCybergrind(CommandContext<ServerCommandSource> context)
	{
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.cybergrind.start"), true);
		CybergrindManager.Instance.startCybergrind();
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugEndCybergrind(CommandContext<ServerCommandSource> context)
	{
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.cybergrind.end"), true);
		CybergrindManager.Instance.endCybergrind();
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugCybergrindSpecific(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
		int waves = context.getArgument("waves", Integer.class);
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.cybergrind.start"), true);
		CybergrindManager.Instance.startCybergrind(target, waves);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugCybergrindSpecificVerbose(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
		int waves = context.getArgument("waves", Integer.class);
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.cybergrind.start"), true);
		CybergrindManager.Instance.startCybergrind(target, waves).setVerbose();
		return Command.SINGLE_SUCCESS;
	}
	
	private static IDimensionDataComponent getTargetDimensionData(MinecraftServer server, Identifier id)
	{
		ServerWorld target = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, id));
		return UltraComponents.DIMENSION_DATA.get(target);
	}
	
	private static CompletableFuture<Suggestions> globalFlagForWorldProvider(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		Identifier dimension = context.getArgument("dimension", Identifier.class);
		IDimensionDataComponent dimensionData = getTargetDimensionData(context.getSource().getServer(), dimension);
		dimensionData.getAllFlags().keySet().forEach(builder::suggest);
		return builder.buildFuture();
	}
	
	private static int executeDebugSetGlobalFlag(CommandContext<ServerCommandSource> context)
	{
		Identifier dimension = context.getArgument("dimension", Identifier.class);
		IDimensionDataComponent dimensionData = getTargetDimensionData(context.getSource().getServer(), dimension);
		String flag = context.getArgument("id", String.class);
		int value = context.getArgument("value", Integer.class);
		dimensionData.setFlag(flag, value);
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.globalflag.set", flag, dimension, value), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugGetGlobalFlag(CommandContext<ServerCommandSource> context)
	{
		Identifier dimension = context.getArgument("dimension", Identifier.class);
		IDimensionDataComponent dimensionData = getTargetDimensionData(context.getSource().getServer(), dimension);
		String flag = context.getArgument("id", String.class);
		int value = dimensionData.getFlag(flag);
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.globalflag.get", flag, dimension, value), false);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugResetBestRank(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		Identifier levelId = IdentifierArgumentType.getIdentifier(context, "level");
		targets.forEach(target -> {
			UltraComponents.LEVEL_STATS.get(target).setBestRank(levelId, -1);
			UltraComponents.LEVEL_STATS.sync(target);
		});
		int size = targets.size();
		if(size > 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level.rank-reset.success-multiple", levelId, size), true);
		else if(size > 0)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level.rank-reset.success", levelId, ((ServerPlayerEntity)targets.toArray()[0]).getDisplayName()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugResetBestTime(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		Identifier levelId = IdentifierArgumentType.getIdentifier(context, "level");
		targets.forEach(target -> {
			UltraComponents.LEVEL_STATS.get(target).resetBestTime(levelId);
			UltraComponents.LEVEL_STATS.sync(target);
		});
		int size = targets.size();
		if(size > 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level.time-reset.success-multiple", levelId, size), true);
		else if(size > 0)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level.time-reset.success", levelId, ((ServerPlayerEntity)targets.toArray()[0]).getDisplayName()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugResetAllBestRanks(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		targets.forEach(target -> {
			LevelDataManager.getAllLevels().keySet().forEach(id -> UltraComponents.LEVEL_STATS.get(target).setBestRank(id, -1));
			UltraComponents.LEVEL_STATS.sync(target);
		});
		int size = targets.size();
		if(size > 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level.rank-reset-all.success-multiple", size), true);
		else if(size > 0)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level.rank-reset-all.success", ((ServerPlayerEntity)targets.toArray()[0]).getDisplayName()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static int executeDebugResetAllBestTimes(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
	{
		Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
		targets.forEach(target -> {
			LevelDataManager.getAllLevels().keySet().forEach(id -> UltraComponents.LEVEL_STATS.get(target).resetBestTime(id));
			UltraComponents.LEVEL_STATS.sync(target);
		});
		int size = targets.size();
		if(size > 1)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level.time-reset-all.success-multiple", size), true);
		else if(size > 0)
			context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.level.time-reset-all.success", ((ServerPlayerEntity)targets.toArray()[0]).getDisplayName()), true);
		return Command.SINGLE_SUCCESS;
	}
	
	private static CompletableFuture<Suggestions> levelIdProvider(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)
	{
		LevelDataManager.getAllLevels().keySet().forEach(id ->
		{
			LevelData data = LevelDataManager.getLevelData(id);
			if(!data.isUnimplemented())
				builder.suggest(id.toString());
		});
		builder.suggest("ultracraft:dimension.prelude");
		builder.suggest("ultracraft:dimension.limbo");
		return builder.buildFuture();
	}
	
	private static int executeDebugClearLikelyPerTickDamageTypes(CommandContext<ServerCommandSource> context)
	{
		Ultracraft.clearLikelyPerTickDamageTypes();
		context.getSource().sendFeedback(() -> Text.translatable("command.ultracraft.debug.clear-likely-per-tick-damage-types.success"), true);
		return Command.SINGLE_SUCCESS;
	}
}
