package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.*;
import absolutelyaya.ultracraft.block.mapping.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockEntityRegistry
{
	public static final BlockEntityType<PedestalBlockEntity> PEDESTAL;
	public static final BlockEntityType<CerberusBlockEntity> CERBERUS;
	public static final BlockEntityType<TerminalBlockEntity> TERMINAL;
	public static final BlockEntityType<HellObserverBlockEntity> HELL_OBSERVER;
	public static final BlockEntityType<HellSpawnerBlockEntity> HELL_SPAWNER;
	public static final BlockEntityType<SkyBlockEntity> SKY;
	
	public static final BlockEntityType<RoomBlockEntity> MAP_ROOM;
	public static final BlockEntityType<TriggerBlockEntity> MAP_TRIGGER;
	public static final BlockEntityType<RedstoneListenerBlockEntity> MAP_REDSTONE;
	public static final BlockEntityType<RedstoneReceiverBlockEntity> MAP_RECEIVER;
	public static final BlockEntityType<GlobalRedstoneListenerBlockEntity> MAP_GLOBAL_REDSTONE;
	public static final BlockEntityType<GlobalRedstoneReceiverBlockEntity> MAP_GLOBAL_RECEIVER;
	public static final BlockEntityType<DoorListenerBlockEntity> MAP_DOOR;
	public static final BlockEntityType<SpawnListenerBlockEntity> MAP_SPAWNER;
	public static final BlockEntityType<EnemyTriggerBlockEntity> MAP_ENEMY_TRIGGER;
	public static final BlockEntityType<ExplosionListenerBlockEntity> MAP_EXPLOSION;
	public static final BlockEntityType<SoundListenerBlockEntity> MAP_SOUND;
	public static final BlockEntityType<CheckpointBlockEntity> MAP_CHECKPOINT;
	public static final BlockEntityType<ProgressionTriggerBlockEntity> MAP_PROGRESSION;
	public static final BlockEntityType<TitleTriggerBlockEntity> MAP_TITLE;
	public static final BlockEntityType<TitleListenerBlockEntity> MAP_TITLE_LISTENER;
	public static final BlockEntityType<GlobalTitleListenerBlockEntity> MAP_GLOBAL_TITLE;
	public static final BlockEntityType<TimerBlockEntity> MAP_TIMER;
	public static final BlockEntityType<LevelUnlockBlockEntity> MAP_LEVEL;
	public static final BlockEntityType<ForceTravelBlockEntity> MAP_TRAVEL;
	public static final BlockEntityType<DamageBlockEntity> MAP_DAMAGE;
	public static final BlockEntityType<CybergrindBlockEntity> MAP_CYBERGRIND;
	public static final BlockEntityType<LightBlockEntity> MAP_LIGHT;
	public static final BlockEntityType<MusicTriggerBlockEntity> MAP_MUSIC;
	public static final BlockEntityType<MusicListenerBlockEntity> MAP_MUSIC_LISTENER;
	
	public static void register() {
	}
	
	static
	{
		PEDESTAL = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "pedestal"),
				FabricBlockEntityTypeBuilder.create(PedestalBlockEntity::new, BlockRegistry.PEDESTAL).build());
		CERBERUS = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "cerberus_block"),
				FabricBlockEntityTypeBuilder.create(CerberusBlockEntity::new, BlockRegistry.CERBERUS).build());
		TERMINAL = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "terminal"),
				FabricBlockEntityTypeBuilder.create(TerminalBlockEntity::new, BlockRegistry.TERMINAL_DISPLAY).build());
		HELL_OBSERVER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "hell_observer"),
				FabricBlockEntityTypeBuilder.create(HellObserverBlockEntity::new, BlockRegistry.HELL_OBSERVER).build());
		HELL_SPAWNER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "hell_spawner"),
				FabricBlockEntityTypeBuilder.create(HellSpawnerBlockEntity::new, BlockRegistry.HELL_SPAWNER).build());
		SKY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "sky_block"),
				FabricBlockEntityTypeBuilder.create(SkyBlockEntity::new, BlockRegistry.SKY_BLOCK).build());
		
		MAP_ROOM = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_room"),
				FabricBlockEntityTypeBuilder.create(RoomBlockEntity::new, BlockRegistry.MAP_ROOM).build());
		MAP_TRIGGER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_trigger"),
				FabricBlockEntityTypeBuilder.create(TriggerBlockEntity::new, BlockRegistry.MAP_TRIGGER).build());
		MAP_REDSTONE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_redstone"),
				FabricBlockEntityTypeBuilder.create(RedstoneListenerBlockEntity::new, BlockRegistry.MAP_REDSTONE).build());
		MAP_RECEIVER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_receiver"),
				FabricBlockEntityTypeBuilder.create(RedstoneReceiverBlockEntity::new, BlockRegistry.MAP_RECEIVER).build());
		MAP_GLOBAL_REDSTONE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_global_redstone"),
				FabricBlockEntityTypeBuilder.create(GlobalRedstoneListenerBlockEntity::new, BlockRegistry.MAP_GLOBAL_REDSTONE).build());
		MAP_GLOBAL_RECEIVER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_global_receiver"),
				FabricBlockEntityTypeBuilder.create(GlobalRedstoneReceiverBlockEntity::new, BlockRegistry.MAP_GLOBAL_RECEIVER).build());
		MAP_DOOR = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_door"),
				FabricBlockEntityTypeBuilder.create(DoorListenerBlockEntity::new, BlockRegistry.MAP_DOOR).build());
		MAP_SPAWNER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_spawner"),
				FabricBlockEntityTypeBuilder.create(SpawnListenerBlockEntity::new, BlockRegistry.MAP_SPAWNER).build());
		MAP_ENEMY_TRIGGER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_enemy_trigger"),
				FabricBlockEntityTypeBuilder.create(EnemyTriggerBlockEntity::new, BlockRegistry.MAP_ENEMY_TRIGGER).build());
		MAP_EXPLOSION = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_explosion"),
				FabricBlockEntityTypeBuilder.create(ExplosionListenerBlockEntity::new, BlockRegistry.MAP_EXPLOSION).build());
		MAP_SOUND = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_sound"),
				FabricBlockEntityTypeBuilder.create(SoundListenerBlockEntity::new, BlockRegistry.MAP_SOUND).build());
		MAP_CHECKPOINT = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_checkpoint"),
				FabricBlockEntityTypeBuilder.create(CheckpointBlockEntity::new, BlockRegistry.MAP_CHECKPOINT).build());
		MAP_PROGRESSION = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_progression"),
				FabricBlockEntityTypeBuilder.create(ProgressionTriggerBlockEntity::new, BlockRegistry.MAP_PROGRESSION).build());
		MAP_TITLE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_title"),
				FabricBlockEntityTypeBuilder.create(TitleTriggerBlockEntity::new, BlockRegistry.MAP_TITLE).build());
		MAP_TITLE_LISTENER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_title_listener"),
				FabricBlockEntityTypeBuilder.create(TitleListenerBlockEntity::new, BlockRegistry.MAP_TITLE_LISTENER).build());
		MAP_GLOBAL_TITLE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_global_title"),
				FabricBlockEntityTypeBuilder.create(GlobalTitleListenerBlockEntity::new, BlockRegistry.MAP_GLOBAL_TITLE).build());
		MAP_TIMER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_timer"),
				FabricBlockEntityTypeBuilder.create(TimerBlockEntity::new, BlockRegistry.MAP_TIMER).build());
		MAP_LEVEL = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_level"),
				FabricBlockEntityTypeBuilder.create(LevelUnlockBlockEntity::new, BlockRegistry.MAP_LEVEL).build());
		MAP_TRAVEL = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_travel"),
				FabricBlockEntityTypeBuilder.create(ForceTravelBlockEntity::new, BlockRegistry.MAP_TRAVEL).build());
		MAP_DAMAGE = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_damage"),
				FabricBlockEntityTypeBuilder.create(DamageBlockEntity::new, BlockRegistry.MAP_DAMAGE).build());
		MAP_CYBERGRIND = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_cybergrind"),
				FabricBlockEntityTypeBuilder.create(CybergrindBlockEntity::new, BlockRegistry.MAP_CYBERGRIND).build());
		MAP_LIGHT = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_light"),
				FabricBlockEntityTypeBuilder.create(LightBlockEntity::new, BlockRegistry.MAP_LIGHT).build());
		MAP_MUSIC = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_music"),
				FabricBlockEntityTypeBuilder.create(MusicTriggerBlockEntity::new, BlockRegistry.MAP_MUSIC).build());
		MAP_MUSIC_LISTENER = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Ultracraft.MOD_ID, "map_music_listener"),
				FabricBlockEntityTypeBuilder.create(MusicListenerBlockEntity::new, BlockRegistry.MAP_MUSIC_LISTENER).build());
	}
}
