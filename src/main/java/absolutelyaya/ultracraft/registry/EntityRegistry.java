package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.demon.*;
import absolutelyaya.ultracraft.entity.husk.FilthEntity;
import absolutelyaya.ultracraft.entity.husk.GreaterFilthEntity;
import absolutelyaya.ultracraft.entity.husk.SchismEntity;
import absolutelyaya.ultracraft.entity.husk.StrayEntity;
import absolutelyaya.ultracraft.entity.machine.*;
import absolutelyaya.ultracraft.entity.other.*;
import absolutelyaya.ultracraft.entity.projectile.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class EntityRegistry
{
	public static final EntityType<FilthEntity> FILTH = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "filth"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(FilthEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).trackRangeChunks(8).build());
	public static final EntityType<StrayEntity> STRAY = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "stray"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(StrayEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).trackRangeChunks(8).build());
	public static final EntityType<SchismEntity> SCHISM = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "schism"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(SchismEntity::new).dimensions(EntityDimensions.fixed(0.6f, 1.95f)).trackRangeChunks(8).build());
	public static final EntityType<MaliciousFaceEntity> MALICIOUS_FACE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "malicious_face"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(MaliciousFaceEntity::new).dimensions(EntityDimensions.fixed(1.5F, 1.5F)).trackRangeChunks(8).build());
	public static final EntityType<CerberusEntity> CERBERUS = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "cerberus"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(CerberusEntity::new).dimensions(EntityDimensions.fixed(1.75F, 4F)).trackRangeChunks(8).build());
	public static final EntityType<HideousMassEntity> HIDEOUS_MASS = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "hideous_mass"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(HideousMassEntity::new).dimensions(EntityDimensions.fixed(8F, 7F)).trackRangeChunks(8).build());
	public static final EntityType<RetaliationEntity> RETALIATION = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "retaliation"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(RetaliationEntity::new).dimensions(EntityDimensions.fixed(1F, 2F)).trackRangeChunks(8).disableSummon().build());
	public static final EntityType<SwordsmachineEntity> SWORDSMACHINE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "swordsmachine"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(SwordsmachineEntity::new).dimensions(EntityDimensions.fixed(0.6F, 2.5F)).trackRangeChunks(8).build());
	public static final EntityType<DestinyBondSwordsmachineEntity> DESTINY_SWORDSMACHINE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "destiny_swordsmachine"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(DestinyBondSwordsmachineEntity::new).dimensions(EntityDimensions.fixed(0.6F, 2.5F)).trackRangeChunks(8).build());
	public static final EntityType<DroneEntity> DRONE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "drone"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(DroneEntity::new).dimensions(EntityDimensions.fixed(0.7F, 0.8F)).trackRangeChunks(8).build());
	public static final EntityType<StreetCleanerEntity> STREET_CLEANER = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "streetcleaner"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(StreetCleanerEntity::new).dimensions(EntityDimensions.fixed(0.6F, 2F)).trackRangeChunks(8).build());
	public static final EntityType<V2Entity> V2 = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "v2"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(V2Entity::new).dimensions(EntityDimensions.fixed(0.6F, 2F)).trackRangeChunks(8).build());
	public static final EntityType<RodentEntity> RODENT = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "rodent"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(RodentEntity::new).dimensions(EntityDimensions.fixed(0.25F, 0.25F)).trackRangeChunks(8).build());
	public static final EntityType<GreaterFilthEntity> GREATER_FILTH = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "greaterfilth"),
			FabricEntityTypeBuilder.create(SpawnGroup.MONSTER).entityFactory(GreaterFilthEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).trackRangeChunks(8).build());
	
	public static final EntityType<HellBulletEntity> HELL_BULLET = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "hell_bullet"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(HellBulletEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().build());
	public static final EntityType<CerberusBallEntity> CERBERUS_BALL = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "cerberus_ball"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(CerberusBallEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().build());
	public static final EntityType<ShotgunPelletEntity> SHOTGUN_PELLET = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "shotgun_pellet"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(ShotgunPelletEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSummon().disableSaving().build());
	public static final EntityType<CancerBulletEntity> CANCER_BULLET = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "cancer_bullet"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(CancerBulletEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().build());
	public static final EntityType<EjectedCoreEntity> EJECTED_CORE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "ejected_core"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(EjectedCoreEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSummon().disableSaving().build());
	public static final EntityType<ThrownMachineSwordEntity> THROWN_MACHINE_SWORD = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "thrown_machinesword"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(ThrownMachineSwordEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(4).trackedUpdateRate(20).disableSummon().build());
	public static final EntityType<ThrownCoinEntity> THROWN_COIN = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "thrown_coin"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(ThrownCoinEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().build());
	public static final EntityType<FlameProjectileEntity> FLAME = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "flame"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(FlameProjectileEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().disableSummon().build());
	public static final EntityType<HideousMortarEntity> MORTAR = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "mortar"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(HideousMortarEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.75f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().build());
	public static final EntityType<HarpoonEntity> HARPOON = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "harpoon"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(HarpoonEntity::new).dimensions(EntityDimensions.fixed(0.35f, 0.35f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().build());
	public static final EntityType<ThrownSoapEntity> SOAP = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "soap"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(ThrownSoapEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)
					).trackRangeChunks(5).trackedUpdateRate(1).build());
	public static final EntityType<MagnetEntity> MAGNET = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "magnet"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(MagnetEntity::new).dimensions(EntityDimensions.fixed(0.35f, 0.35f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().build());
	//only tracking interval 3 due to the guaranteed large amount of these and it's not like they're parriable anyways
	public static final EntityType<NailEntity> NAIL = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "nail"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(NailEntity::new).dimensions(EntityDimensions.fixed(0.125f, 0.125f)
					).trackRangeChunks(5).trackedUpdateRate(3).disableSaving().build());
	public static final EntityType<BeamProjectileEntity> BEAM = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "beam"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(BeamProjectileEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)
					).trackRangeChunks(5).trackedUpdateRate(1).disableSaving().build());
	
	public static final EntityType<ShockwaveEntity> SHOCKWAVE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "shockwave"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(ShockwaveEntity::new).trackRangeChunks(5).disableSaving().build());
	public static final EntityType<VerticalShockwaveEntity> VERICAL_SHOCKWAVE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "vertical_shockwave"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(VerticalShockwaveEntity::new).trackRangeChunks(5).disableSaving().build());
	public static final EntityType<InterruptableCharge> INTERRUPTABLE_CHARGE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "interruptable_charge"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(InterruptableCharge::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f))
					.disableSummon().disableSaving().trackRangeChunks(5).build());
	public static final EntityType<BackTank> BACK_TANK = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "back_tank"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(BackTank::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f))
					.disableSummon().disableSaving().trackRangeChunks(5).build());
	public static final EntityType<SoulOrbEntity> SOUL_ORB = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "soul_orb"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(SoulOrbEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(5).build());
	public static final EntityType<BloodOrbEntity> BLOOD_ORB = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "blood_orb"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(BloodOrbEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(5).build());
	public static final EntityType<StainedGlassWindow> STAINED_GLASS_WINDOW = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "stained_glass_window"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(StainedGlassWindow::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)
					).trackRangeChunks(5).build());
	public static final EntityType<ProgressionItemEntity> PROGRESSION_ITEM = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(Ultracraft.MOD_ID, "progression_item"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC).entityFactory(ProgressionItemEntity::new).dimensions(EntityDimensions.fixed(0.25f, 0.25f)
					).trackRangeChunks(5).build());
	
	public static final TagKey<EntityType<?>> PROJBOOSTABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "projboostable"));
	public static final TagKey<EntityType<?>> LIVING = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "living"));
	public static final TagKey<EntityType<?>> NON_LIVING = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "non-living"));
	public static final TagKey<EntityType<?>> PROJECTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "projectile"));
	public static final TagKey<EntityType<?>> HUSKS = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "husks"));
	public static final TagKey<EntityType<?>> MACHINES = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "machines"));
	public static final TagKey<EntityType<?>> DEMONS = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "demons"));
	public static final TagKey<EntityType<?>> ANGELS = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "angels"));
	public static final TagKey<EntityType<?>> BIG_FUNIS = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "funis"));
	public static final TagKey<EntityType<?>> STREETCLEANER_DODGE = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "streetcleaner_dodge"));
	public static final TagKey<EntityType<?>> STREETCLEANER_COUNTER = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "streetcleaner_counter"));
	public static final TagKey<EntityType<?>> EXPLOSION_AFFECTED_PROJECTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(Ultracraft.MOD_ID, "explosion_affected_projectiles"));
	
	public static void register()
	{
		FabricDefaultAttributeRegistry.register(FILTH, FilthEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(STRAY, StrayEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(SCHISM, SchismEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(MALICIOUS_FACE, MaliciousFaceEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(CERBERUS, CerberusEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(HIDEOUS_MASS, HideousMassEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(RETALIATION, RetaliationEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(SWORDSMACHINE, SwordsmachineEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(DESTINY_SWORDSMACHINE, DestinyBondSwordsmachineEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(DRONE, DroneEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(STREET_CLEANER, StreetCleanerEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(V2, V2Entity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(RODENT, RodentEntity.getDefaultAttributes());
		FabricDefaultAttributeRegistry.register(GREATER_FILTH, GreaterFilthEntity.getDefaultAttributes());
		
		SpawnRestriction.register(FILTH, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		SpawnRestriction.register(STRAY, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		SpawnRestriction.register(SCHISM, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		SpawnRestriction.register(MALICIOUS_FACE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		SpawnRestriction.register(CERBERUS, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		SpawnRestriction.register(HIDEOUS_MASS, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		SpawnRestriction.register(SWORDSMACHINE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		SpawnRestriction.register(DRONE, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
		SpawnRestriction.register(STREET_CLEANER, SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
	}
}
