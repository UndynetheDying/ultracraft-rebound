package absolutelyaya.ultracraft;

import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.api.CybergrindInitializer;
import absolutelyaya.ultracraft.command.Commands;
import absolutelyaya.ultracraft.command.EditModeCommands;
import absolutelyaya.ultracraft.command.WhitelistCommand;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IEditorComponent;
import absolutelyaya.ultracraft.components.player.IWingDataComponent;
import absolutelyaya.ultracraft.config.CybergrindConfig;
import absolutelyaya.ultracraft.config.HivelConfig;
import absolutelyaya.ultracraft.config.ServerConfig;
import absolutelyaya.ultracraft.config.Setting;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.data.StyleBonusManager;
import absolutelyaya.ultracraft.data.TerminalScreensaverManager;
import absolutelyaya.ultracraft.data.UltraRecipeManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.dimension.UltraDimensions;
import absolutelyaya.ultracraft.cybergrind.CybergrindManager;
import absolutelyaya.ultracraft.item.AbstractNailgunItem;
import absolutelyaya.ultracraft.item.AbstractRevolverItem;
import absolutelyaya.ultracraft.recipe.RecipeSerializers;
import absolutelyaya.ultracraft.registry.*;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Ultracraft implements ModInitializer
{
    public static final String MOD_ID = "ultracraft";
    public static final Logger LOGGER = LogUtils.getLogger();
    static final String SUPPORTER_LIST = "https://raw.githubusercontent.com/absolutelyaya/absolutelyaya/main/cool-people.json";
    public static String VERSION;
	public static boolean DYN_LIGHTS, SERVER_SIDE;
	static int freezeTicks;
    static Map<UUID, Integer> supporterCache = new HashMap<>(), supporterCacheAdditions = new HashMap<>();
    static ServerConfig config;
    static HivelConfig hivelConfig;
    
    @Override
    public void onInitialize()
    {
        ParticleRegistry.init();
        EntityRegistry.register();
        BlockRegistry.registerBlocks();
        FluidRegistry.register();
        BlockEntityRegistry.register();
        ItemRegistry.register();
        PacketRegistry.registerC2S();
        TagRegistry.register();
        SoundRegistry.register();
        GameruleRegistry.register();
        RecipeSerializers.register();
        CriteriaRegistry.register();
        StatusEffectRegistry.register();
        ScreenHandlerRegistry.registerServer();
        StatisticRegistry.register();
        StructureRegistry.register();
        new UltraRecipeManager();
        new TerminalScreensaverManager();
        new StyleBonusManager();
        new LevelManager();
        
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            new UltraDimensions(server);
            UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
                IEditorComponent editor = UltraComponents.EDITOR.get(player);
                if(editor.isActive() && hand.equals(Hand.MAIN_HAND))
                    return editor.useBlock(player, hitResult.getBlockPos());
                return UltraDimensions.Instance.onBlockInteract(player, world, hand, hitResult);
            });
            
            AttackBlockCallback.EVENT.register(((player, world, hand, pos, direction) -> UltraDimensions.Instance.onAttackBlock(player, world, hand, pos, direction)));
            //UseItemCallback.EVENT.register(((player, world, hand) -> UltraDimensions.Instance.onUseItem(player, world, hand)));
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            LevelManager.Instance.onServerStop();
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            Commands.register(dispatcher);
            WhitelistCommand.register(dispatcher);
            EditModeCommands.register(dispatcher);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickFreeze();
            ServerHitscanHandler.tickSchedule();
            supporterCache.putAll(supporterCacheAdditions);
            supporterCacheAdditions.clear();
            supporterCache.forEach((uuid, i) -> {
                if(i > 0)
                    supporterCache.put(uuid, i - 1);
            });
            UltraDimensions.Instance.tickManagers();
            CybergrindManager.Instance.tick();
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> rechargeWeapons(newPlayer));
        
        ServerPlayConnectionEvents.JOIN.register((networkHandler, sender, server) -> {
            ServerPlayerEntity player = networkHandler.player;
            config.syncAll(player);
            UltraRecipeManager.sync(player);
            LevelDataManager.sync(player);
            Setting hivel = config.hivel.getValue();
            if(!hivel.equals(Setting.FREE))
            {
                IWingDataComponent wings = UltraComponents.WING_DATA.get(player);
                wings.setActive(hivel.equals(Setting.FORCE_ON));
                wings.sync();
            }
        });
        ServerPlayConnectionEvents.INIT.register(((handler, server) -> {
            ServerPlayerEntity player = handler.player;
            //detect first spawn; probably a scuffed way to do this, but hey, it works :3
            if(player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME)) == 0)
                if(player.getWorld().getGameRules().getBoolean(GameruleRegistry.START_WITH_PIERCER))
                    player.giveItemStack(ItemRegistry.PIERCE_REVOLVER.getDefaultStack());
        }));
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            loadConfig(server);
            new CybergrindManager(server);
        });
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, handler) -> {
            loadConfig(server);
            server.getPlayerManager().getPlayerList().forEach(player -> {
                UltraRecipeManager.sync(player);
                LevelDataManager.sync(player);
            });
        });
        
        SERVER_SIDE = FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER);
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> VERSION = modContainer.getMetadata().getVersion().getFriendlyString());
        FabricLoader.getInstance().getModContainer("lambdynlights").ifPresent(container -> DYN_LIGHTS = true);
        LOGGER.info("Ultracraft initialized.");
    }
    
    void loadConfig(MinecraftServer server)
    {
        if(config == null)
            config = new ServerConfig(server);
        else
            config.load(server);
        config.syncAll(server);
        if(hivelConfig == null)
            hivelConfig = new HivelConfig(server);
        else
            hivelConfig.load(server);
        hivelConfig.syncAll(server);
        if(CybergrindConfig.INSTANCE == null)
            new CybergrindConfig(server);
        else
            CybergrindConfig.INSTANCE.load(server);
        CybergrindConfig.clearCosts();
        for (CybergrindInitializer initializer : FabricLoader.getInstance().getEntrypoints("cybergrind", CybergrindInitializer.class))
            initializer.registerEnemyCosts(CybergrindConfig.INSTANCE);
        CybergrindConfig.freeze();
    }
    
    public static boolean isTimeFrozen()
    {
        return freezeTicks > 0;
    }
    
    public static void freeze(ServerPlayerEntity player, int ticks)
    {
        if(player != null)
        {
            boolean freezeDisabled = player.getServer().isRemote() && ServerConfig.INSTANCE.timestop.getValue().equals(Setting.FORCE_OFF);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(ticks);
            buf.writeBoolean(freezeDisabled);
            if(freezeDisabled)
            {
                ServerPlayNetworking.send(player, PacketRegistry.FREEZE_PACKET_ID, buf);
                return;
            }
            for (ServerPlayerEntity p : ((ServerWorld)player.getWorld()).getPlayers())
                ServerPlayNetworking.send(p, PacketRegistry.FREEZE_PACKET_ID, buf);
        }
        freezeTicks += ticks;
        LOGGER.info("Stopping time for " + ticks + " ticks.");
    }
    
    public static void freeze(ServerWorld world, int ticks)
    {
        if(world != null)
        {
            boolean freezeDisabled = world.getServer().isRemote() && ServerConfig.INSTANCE.timestop.getValue().equals(Setting.FORCE_OFF);
            if(freezeDisabled)
                return;
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(ticks);
            buf.writeBoolean(false);
            for (ServerPlayerEntity p : world.getPlayers())
                ServerPlayNetworking.send(p, PacketRegistry.FREEZE_PACKET_ID, buf);
        }
        freezeTicks += ticks;
        LOGGER.info("Stopping time for " + ticks + " ticks.");
    }
    
    public static void cancelFreeze(ServerWorld world)
    {
        if(world != null)
        {
            boolean freezeDisabled = world.getServer().isRemote() && ServerConfig.INSTANCE.timestop.getValue().equals(Setting.FORCE_OFF);
            if(freezeDisabled)
                return;
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(-1);
            buf.writeBoolean(false);
            for (ServerPlayerEntity p : world.getPlayers())
                ServerPlayNetworking.send(p, PacketRegistry.FREEZE_PACKET_ID, buf);
        }
        freezeTicks = 0;
        LOGGER.info("Forcefully Unstopped time.");
    }
    
    public static void tickFreeze()
    {
        if(freezeTicks > 0)
            freezeTicks--;
    }
    
    public static boolean checkSupporter(UUID uuid, boolean client)
    {
        int i;
        if(supporterCache.containsKey(uuid) && (i = supporterCache.get(uuid)) != 0)
            return i == -1;
        boolean supporter = false;
        JsonObject json = fetchSupporterList();
        if(json == null)
        {
            Ultracraft.LOGGER.error("[ULTRACRAFT] Failed to fetch Supporters.");
            supporterCache.put(uuid, 600);
            return supporter;
        }
        supporter = JsonHelper.hasElement(json, uuid.toString());
        if(supporter && client)
        {
            Ultracraft.LOGGER.info("[ULTRACRAFT] " + uuid + " has been verified as a Supporter!");
            supporterCache.put(uuid, -1);
        }
        else
            supporterCache.put(uuid, 600); //if not a supporter, only check again after 30 seconds
        return supporter;
    }
    
    public static JsonObject fetchSupporterList()
    {
        try
        {
            URL url = new URL(SUPPORTER_LIST);
            return JsonHelper.deserialize(new InputStreamReader(url.openStream()));
        }
        catch (IOException e)
        {
            Ultracraft.LOGGER.error("[ULTRACRAFT] Failed to fetch Supporters.", e);
        }
        return null;
    }
    
    public static void screenshake(PlayerEntity player, float strength)
    {
        if(player.getWorld().isClient && player instanceof WingedPlayerEntity winged)
            winged.addScreenshake(strength);
        else if(player instanceof ServerPlayerEntity serverPlayer)
        {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeFloat(strength);
            ServerPlayNetworking.send(serverPlayer, PacketRegistry.SCREENSHAKE_PACKET_ID, buf);
        }
    }
    
    public static void rechargeWeapons(PlayerEntity player)
    {
        player.getInventory().main.forEach(stack -> {
            Item item = stack.getItem();
            if(item instanceof AbstractRevolverItem revolver)
            {
                revolver.setNbt(stack, "coins", revolver.getNbtDefault("coins"));
                revolver.setNbt(stack, "charges", revolver.getNbtDefault("charges"));
            }
            else if (item instanceof AbstractNailgunItem nailgun && nailgun.getNbt(stack, "nails") < 100)
                nailgun.setNbt(stack, "nails", 100);
        });
    }
}
