package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.CerberusBlock;
import absolutelyaya.ultracraft.block.SkyBlockEntity;
import absolutelyaya.ultracraft.block.TerminalBlockEntity;
import absolutelyaya.ultracraft.entity.other.BloodOrbEntity;
import absolutelyaya.ultracraft.entity.other.SoulOrbEntity;
import absolutelyaya.ultracraft.entity.other.StainedGlassWindow;
import absolutelyaya.ultracraft.entity.projectile.CancerBulletEntity;
import absolutelyaya.ultracraft.entity.projectile.CerberusBallEntity;
import absolutelyaya.ultracraft.entity.projectile.HellBulletEntity;
import absolutelyaya.ultracraft.entity.projectile.ThrownSoapEntity;
import absolutelyaya.ultracraft.item.SkullItem;
import absolutelyaya.ultracraft.item.*;
import absolutelyaya.ultracraft.item.weapons.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class ItemRegistry
{
	//Misc
	public static final SkullItem BLUE_SKULL = Registry.register(Registries.ITEM,
			Ultracraft.identifier("blue_skull"), new SkullItem(new FabricItemSettings()));
	public static final SkullItem RED_SKULL = Registry.register(Registries.ITEM,
			Ultracraft.identifier("red_skull"), new SkullItem(new FabricItemSettings()));
	public static final Item HELL_BULLET = Registry.register(Registries.ITEM,
			Ultracraft.identifier("hell_bullet"), new HellBulletItem(new FabricItemSettings().fireproof().maxCount(25)));
	public static final Item CERBERUS_BALL = Registry.register(Registries.ITEM,
			Ultracraft.identifier("cerberus_ball"), new HellBulletItem(new FabricItemSettings().fireproof().maxCount(21)));
	public static final Item CANCER_BULLET = Registry.register(Registries.ITEM,
			Ultracraft.identifier("cancer_bullet"), new HellBulletItem(new FabricItemSettings().fireproof().maxCount(7)));
	public static final Item EJECTED_CORE = Registry.register(Registries.ITEM,
			Ultracraft.identifier("ejected_core"), new HellBulletItem(new FabricItemSettings().maxCount(0)));
	public static final Item NAIL = Registry.register(Registries.ITEM,
			Ultracraft.identifier("nail"), new Item(new FabricItemSettings().maxCount(0)));
	public static final Item BLOOD_BUCKET = Registry.register(Registries.ITEM,
			Ultracraft.identifier("blood_bucket"), new BucketItem(FluidRegistry.STILL_BLOOD,
					new FabricItemSettings().maxCount(1).recipeRemainder(Items.BUCKET)));
	public static final CoinItem COIN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("coin"), new CoinItem(new FabricItemSettings()));
	public static final KillerFishItem KILLERFISH = Registry.register(Registries.ITEM,
			Ultracraft.identifier("killerfish"), new KillerFishItem(new FabricItemSettings()));
	public static final Item BLOOD_RAY = Registry.register(Registries.ITEM,
			Ultracraft.identifier("bloodray"), new Item(new FabricItemSettings()
						.food(new FoodComponent.Builder().hunger(4).alwaysEdible()
							  .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 500, 0), 1f).build())));
	public static final DroneMaskItem DRONE_MASK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("drone_mask"), new DroneMaskItem(new FabricItemSettings()));
	public static final Item MINCED_MEAT = Registry.register(Registries.ITEM,
			Ultracraft.identifier("mincedmeat"), new SpecialItem(new FabricItemSettings()
						.food(new FoodComponent.Builder().hunger(12).saturationModifier(6f).build()))
								.putLore(new String[] { "item.ultracraft.mincedmeat.lore" }, new String[] { "item.ultracraft.mincedmeat.hiddenlore" }));
	public static final Item HELL_MASS = Registry.register(Registries.ITEM,
			Ultracraft.identifier("hell_mass"), new Item(new FabricItemSettings()));
	public static final Item PLACEHOLDER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("placeholder"),
			new Item(new FabricItemSettings().food(new FoodComponent.Builder().alwaysEdible().hunger(-1).build())));
	public static final LumpFishItem LUMPFISH = Registry.register(Registries.ITEM,
			Ultracraft.identifier("lumpfish"), new LumpFishItem(new FabricItemSettings().maxCount(64)));
	public static final BlahajItem BLAHAJ = Registry.register(Registries.ITEM,
			Ultracraft.identifier("blahaj"), new BlahajItem(new FabricItemSettings().maxCount(1)));
	public static final DecorativeMauriceItem DECORATIVE_MAURICE = Registry.register(Registries.ITEM,
			Ultracraft.identifier("maurice_deco"), new DecorativeMauriceItem(new FabricItemSettings().maxCount(1)));
	
	//Progression Items
	public static final Item FEEDBACKER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("feedbacker"), new ProgressionUnlockItem(new FabricItemSettings().maxCount(1), Ultracraft.identifier("feedbacker")));
	public static final Item HIVEL_WINGS = Registry.register(Registries.ITEM,
			Ultracraft.identifier("hivel_wings"), new ProgressionUnlockItem(new FabricItemSettings().maxCount(1), Ultracraft.identifier("hivel")).markTrinket());
	public static final Item ABSORBANT_PLATING = Registry.register(Registries.ITEM,
			Ultracraft.identifier("absorbant_plating"), new ProgressionUnlockItem(new FabricItemSettings().maxCount(1), Ultracraft.identifier("bloodheal")).markTrinket());
	public static final Item VIEW_AUGMENT = Registry.register(Registries.ITEM,
			Ultracraft.identifier("view_augment"), new ProgressionUnlockItem(new FabricItemSettings().maxCount(1), Ultracraft.identifier("ultrahud")).markTrinket());
	public static final Item KNUCKLEBLASTER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("knuckleblaster"), new ProgressionUnlockItem(new FabricItemSettings().maxCount(1), Ultracraft.identifier("knuckleblaster")));
	
	//Weapons
	public static final PierceRevolverItem PIERCE_REVOLVER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("pierce_revolver"), new PierceRevolverItem(new FabricItemSettings().maxCount(1)));
	public static final MarksmanRevolverItem MARKSMAN_REVOLVER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("marksman_revolver"), new MarksmanRevolverItem(new FabricItemSettings().maxCount(1)));
	public static final SharpshooterRevolverItem SHARPSHOOTER_REVOLVER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("sharpshooter_revolver"), new SharpshooterRevolverItem(new FabricItemSettings().maxCount(1)));
	public static final CoreEjectShotgunItem CORE_SHOTGUN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("core_shotgun"), new CoreEjectShotgunItem(new FabricItemSettings().maxCount(1)));
	public static final PumpShotgunItem PUMP_SHOTGUN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("pump_shotgun"), new PumpShotgunItem(new FabricItemSettings().maxCount(1)));
	public static final SawedOnShotgunItem SAW_SHOTGUN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("saw_shotgun"), new SawedOnShotgunItem(new FabricItemSettings().maxCount(1)));
	public static final MachineSwordItem MACHINE_SWORD = Registry.register(Registries.ITEM,
			Ultracraft.identifier("machinesword"), new MachineSwordItem(ToolMaterials.IRON, 4, -2.4f,
					new FabricItemSettings().maxCount(1)));
	public static final FlamethrowerItem FLAMETHROWER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("flamethrower"), new FlamethrowerItem(new FabricItemSettings().maxCount(1), 0, 0));
	public static final Item HARPOON = Registry.register(Registries.ITEM,
			Ultracraft.identifier("harpoon"), new HarpoonItem(6f, -2.75f,
					new FabricItemSettings().maxDamage(100)));
	public static final Item HARPOON_GUN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("harpoon_gun"), new HarpoonGunItem(new FabricItemSettings(), 25, 0f));
	public static final SoapItem SOAP = Registry.register(Registries.ITEM,
			Ultracraft.identifier("soap"), new SoapItem(new FabricItemSettings().maxCount(4).rarity(Rarity.EPIC)));
	public static final AttractorNailgunItem ATTRACTOR_NAILGUN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("attractor_nailgun"), new AttractorNailgunItem(new FabricItemSettings().maxCount(1)));
	public static final OverheatNailgunItem OVERHEAT_NAILGUN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("overheat_nailgun"), new OverheatNailgunItem(new FabricItemSettings().maxCount(1)));
	public static final JumpstartNailgunItem JUMPSTART_NAILGUN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("jumpstart_nailgun"), new JumpstartNailgunItem(new FabricItemSettings().maxCount(1)));
	public static final AlternatePiercerItem ALTERNATE_PIERCER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("alternate_piercer"), new AlternatePiercerItem(new FabricItemSettings().maxCount(1)));
	public static final AlternateMarksmanItem ALTERNATE_MARKSMAN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("alternate_marksman"), new AlternateMarksmanItem(new FabricItemSettings().maxCount(1)));
	public static final AlternateSharpshooterItem ALTERNATE_SHARPSHOOTER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("alternate_sharpshooter"), new AlternateSharpshooterItem(new FabricItemSettings().maxCount(1)));
	
	//Spawn Eggs
	public static final SpawnEggItem FILTH_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("filth_spawn_egg"),
			new SpawnEggItem(EntityRegistry.FILTH, 0xffffff, 0xacaa7a, new FabricItemSettings()));
	public static final SpawnEggItem STRAY_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("stray_spawn_egg"),
			new SpawnEggItem(EntityRegistry.STRAY, 0xffffff, 0x922923, new FabricItemSettings()));
	public static final SpawnEggItem SCHISM_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("schism_spawn_egg"),
			new SpawnEggItem(EntityRegistry.SCHISM, 0xffffff, 0xa0938e, new FabricItemSettings()));
	public static final SpawnEggItem MALICIOUS_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("malicious_spawn_egg"),
			new SpawnEggItem(EntityRegistry.MALICIOUS_FACE, 0xffffff, 0x5a5353, new FabricItemSettings()));
	public static final SpawnEggItem CERBERUS_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("cerberus_spawn_egg"),
			new SpawnEggItem(EntityRegistry.CERBERUS, 0xffffff, 0x5a5353, new FabricItemSettings()));
	public static final SpecialSpawnEggItem SWORDSMACHINE_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("swordsmachine_spawn_egg"),
			new SpecialSpawnEggItem(EntityRegistry.SWORDSMACHINE, 0xffffff, 0x423d40, new FabricItemSettings()));
	public static final DestinyBondSpawnEggItem DESTINY_SWORDSMACHINE_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("destiny_swordsmachine_spawn_egg"),
			new DestinyBondSpawnEggItem(new FabricItemSettings()));
	public static final MultiColorSpawnEggItem DRONE_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("drone_spawn_egg"),
			new MultiColorSpawnEggItem(EntityRegistry.DRONE, new int[] {0x813ec6, 0x1b182d, 0xee42ff}, new FabricItemSettings()));
	public static final SpawnEggItem STREET_CLEANER_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("streetcleaner_spawn_egg"),
			new SpawnEggItem(EntityRegistry.STREET_CLEANER, 0xffffff, 0x211c1b, new FabricItemSettings()));
	public static final SpecialSpawnEggItem HIDEOUS_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("hideous_spawn_egg"),
			new MultiColorSpawnEggItem(EntityRegistry.HIDEOUS_MASS, new int[] {0xffffff}, new FabricItemSettings()));
	public static final OrbItem SOUL_ORB = Registry.register(Registries.ITEM,
			Ultracraft.identifier("soul_orb"), new OrbItem(new FabricItemSettings(), EntityRegistry.SOUL_ORB));
	public static final OrbItem BLOOD_ORB = Registry.register(Registries.ITEM,
			Ultracraft.identifier("blood_orb"), new OrbItem(new FabricItemSettings(), EntityRegistry.BLOOD_ORB));
	public static final StainedGlassWindowItem STAINED_GLASS_WINDOW = Registry.register(Registries.ITEM,
			Ultracraft.identifier("stained_glass_window"), new StainedGlassWindowItem(EntityRegistry.STAINED_GLASS_WINDOW, new FabricItemSettings()));
	public static final SpawnEggItem V2_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("v2_spawn_egg"),
			new SpawnEggItem(EntityRegistry.V2, 0xffffff, 0x261e1f, new FabricItemSettings()));
	public static final SpawnEggItem RODENT_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("rodent_spawn_egg"),
			new SpawnEggItem(EntityRegistry.RODENT, 0xb6d53c, 0x71aa34, new FabricItemSettings()));
	public static final SpawnEggItem GREATERFILTH_SPAWN_EGG = Registry.register(Registries.ITEM,
			Ultracraft.identifier("greaterfilth_spawn_egg"),
			new SpawnEggItem(EntityRegistry.GREATER_FILTH, 0x4c5820, 0x91a25e, new FabricItemSettings()));
	
	//Plushies
	public static final PlushieItem PLUSHIE = Registry.register(Registries.ITEM,
			Ultracraft.identifier("plushie"), new PlushieItem(new FabricItemSettings()));
	public static final PlushieItem PITR = Registry.register(Registries.ITEM,
			Ultracraft.identifier("pitr"), new PitrItem(new FabricItemSettings()));
	public static final PlushieItem PITR_POIN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("pitr_poin"), new PitrPoinItem(new FabricItemSettings()));
	public static final SwordsmachinePlushieItem SWORDSMACHINE = Registry.register(Registries.ITEM,
			Ultracraft.identifier("swordsmachine_plushie"), new SwordsmachinePlushieItem(new FabricItemSettings()));
	public static final TalonItem TALON = Registry.register(Registries.ITEM,
			Ultracraft.identifier("talon"), new TalonItem(new FabricItemSettings()));
	public static final V2Item V2 = Registry.register(Registries.ITEM,
			Ultracraft.identifier("v2"), new V2Item(new FabricItemSettings()));
	public static final AnthroPlushieItem ASHEN = Registry.register(Registries.ITEM,
			Ultracraft.identifier("ashenwulf"), new AnthroPlushieItem(new FabricItemSettings()));
	
	//Special
	public static final TerminalItem TERMINAL = Registry.register(Registries.ITEM,
			Ultracraft.identifier("terminal"),
			new TerminalItem(BlockRegistry.TERMINAL, new FabricItemSettings()));
	public static final HankItem HANK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("hank"),
			new HankItem(BlockRegistry.HANK, new FabricItemSettings().rarity(Rarity.EPIC)));
	public static final FlorpItem FLORP = (FlorpItem)Registry.register(Registries.ITEM,
			Ultracraft.identifier("florp"), new FlorpItem(new FabricItemSettings().rarity(Rarity.EPIC).maxCount(1))
																.putLore(true, new String[] { "item.ultracraft.florp.hiddenlore" }));
	public static final SkyBlockItem SKY = Registry.register(Registries.ITEM,
			Ultracraft.identifier("sky_block"), new SkyBlockItem(new FabricItemSettings().rarity(Rarity.EPIC)));
	public static final BlockItem PORTAL = Registry.register(Registries.ITEM,
			Ultracraft.identifier("portal"), new BlockItem(BlockRegistry.PORTAL, new FabricItemSettings().rarity(Rarity.UNCOMMON)));
	
	//music disks
	public static final MusicDiscItem CLAIR_DE_LUNE_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/clair_de_lune"),
			new MusicDiscItem(15, SoundRegistry.CLAIR_DE_LUNE.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 231));
	public static final MusicDiscItem FIRE_IS_GONE_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/fire_is_gone"),
			new MusicDiscItem(15, SoundRegistry.THE_FIRE_IS_GONE.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 109));
	public static final MusicDiscItem PRELUDE1_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/prelude1"),
			new MusicDiscItem(15, SoundRegistry.PRELUDE1.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 197));
	public static final MusicDiscItem PRELUDE1_CALM_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/prelude1_calm"),
			new MusicDiscItem(15, SoundRegistry.PRELUDE1_CALM.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 197));
	public static final MusicDiscItem PRELUDE2_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/prelude2"),
			new MusicDiscItem(15, SoundRegistry.PRELUDE2.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 181));
	public static final MusicDiscItem PRELUDE2_CALM_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/prelude2_calm"),
			new MusicDiscItem(15, SoundRegistry.PRELUDE2_CALM.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 181));
	public static final MusicDiscItem CERBERUS_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/cerberus"),
			new MusicDiscItem(15, SoundRegistry.CERBERUS.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 135));
	public static final MusicDiscItem CERBERUS_CALM_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/cerberus_calm"),
			new MusicDiscItem(15, SoundRegistry.CERBERUS_CALM.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 135));
	public static final MusicDiscItem LIMBO1_ILLUSION_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/limbo1_illusion"),
			new MusicDiscItem(15, SoundRegistry.LIMBO1_ILLUSION.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 116));
	public static final MusicDiscItem LIMBO1_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/limbo1"),
			new MusicDiscItem(15, SoundRegistry.LIMBO1.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 114));
	public static final MusicDiscItem LIMBO1_CALM_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/limbo1_calm"),
			new MusicDiscItem(15, SoundRegistry.LIMBO1_CALM.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 114));
	public static final MusicDiscItem LIMBO2_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/limbo2"),
			new MusicDiscItem(15, SoundRegistry.LIMBO2.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 176));
	public static final MusicDiscItem LIMBO2_CALM_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/limbo2_calm"),
			new MusicDiscItem(15, SoundRegistry.LIMBO2_CALM.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 176));
	public static final MusicDiscItem VERSUS_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/versus"),
			new MusicDiscItem(15, SoundRegistry.VERSUS.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 128));
	public static final MusicDiscItem LIMBO_FREEROAM_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/counterfeit"),
			new MusicDiscItem(15, SoundRegistry.COUNTERFEIT.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 196));
	public static final MusicDiscItem CYBERGRIND_DISK = Registry.register(Registries.ITEM,
			Ultracraft.identifier("disc/cybergrind"),
			new MusicDiscItem(15, SoundRegistry.CYBERGRIND_DISK.value(), new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 269));
	
	//Animated Blocks
	public static final HellSpawnerItem HELL_SPAWNER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("hell_spawner"), new HellSpawnerItem(new FabricItemSettings()));
	
	//fakes
	public static final Item FAKE_SHIELD = Registry.register(Registries.ITEM,
			Ultracraft.identifier("fake_shield"), new Item(new FabricItemSettings().maxCount(0)));
	public static final Item FAKE_BANNER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("fake_banner"), new Item(new FabricItemSettings().maxCount(0)));
	public static final Item FAKE_TERMINAL = Registry.register(Registries.ITEM,
			Ultracraft.identifier("fake_terminal"), new Item(new FabricItemSettings().maxCount(0)));
	public static final Item FAKE_CHEST = Registry.register(Registries.ITEM,
			Ultracraft.identifier("fake_chest"), new Item(new FabricItemSettings().maxCount(0)));
	public static final Item FAKE_ENDER_CHEST = Registry.register(Registries.ITEM,
			Ultracraft.identifier("fake_ender_chest"), new Item(new FabricItemSettings().maxCount(0)));
	public static final Item FAKE_HELL_SPAWNER = Registry.register(Registries.ITEM,
			Ultracraft.identifier("fake_hell_spawner"), new Item(new FabricItemSettings().maxCount(0)));
	
	public static final RegistryKey<ItemGroup> ULTRACRAFT_TAB = RegistryKey.of(RegistryKeys.ITEM_GROUP, Ultracraft.identifier("item"));
	public static final RegistryKey<ItemGroup> EDIT_MODE_TAB = RegistryKey.of(RegistryKeys.ITEM_GROUP, Ultracraft.identifier("edit"));
	public static final RegistryKey<ItemGroup> MUSIC_TAB = RegistryKey.of(RegistryKeys.ITEM_GROUP, Ultracraft.identifier("music"));
	
	public static void register()
	{
		Registry.register(Registries.ITEM_GROUP, ULTRACRAFT_TAB,
				FabricItemGroup.builder().displayName(Text.translatable("itemGroup.ultracraft.item")).icon(() -> new ItemStack(BLUE_SKULL)).build());
		ItemGroupEvents.modifyEntriesEvent(ULTRACRAFT_TAB).register(content -> {
			content.add(BLUE_SKULL);
			content.add(RED_SKULL);
			content.add(HELL_BULLET);
			content.add(CERBERUS_BALL);
			content.add(CANCER_BULLET);
			content.add(BlockRegistry.ELEVATOR.asItem());
			content.add(BlockRegistry.ELEVATOR_WALL.asItem());
			content.add(BlockRegistry.ELEVATOR_FLOOR.asItem());
			content.add(BlockRegistry.SECRET_ELEVATOR.asItem());
			content.add(BlockRegistry.SECRET_ELEVATOR_WALL.asItem());
			content.add(BlockRegistry.SECRET_ELEVATOR_FLOOR.asItem());
			content.add(BlockRegistry.PEDESTAL.asItem());
			content.add(BlockRegistry.CERBERUS.asItem());
			content.add(((CerberusBlock)BlockRegistry.CERBERUS).getProximityStack());
			content.add(BlockRegistry.FLESH.asItem());
			content.add(BlockRegistry.RUSTY_PIPE.asItem());
			content.add(BlockRegistry.RUSTY_MESH.asItem());
			content.add(BlockRegistry.MESH.asItem());
			content.add(BlockRegistry.CRACKED_STONE.asItem());
			content.add(BlockRegistry.VENT_COVER.asItem());
			content.add(BlockRegistry.VENT.asItem());
			content.add(BlockRegistry.MAUERWERK1.asItem());
			content.add(BlockRegistry.MAUERWERK2.asItem());
			content.add(BlockRegistry.ORNATE_WAINSCOT.asItem());
			content.add(BlockRegistry.ADORNED_RAILING.asItem());
			content.add(StainedGlassWindowItem.getStack(false, StainedGlassWindow.Variant.DOVE));
			content.add(StainedGlassWindowItem.getStack(false, StainedGlassWindow.Variant.GABRIEL));
			content.add(StainedGlassWindowItem.getStack(false, StainedGlassWindow.Variant.SAINT));
			content.add(StainedGlassWindowItem.getStack(false, StainedGlassWindow.Variant.TREE));
			content.add(StainedGlassWindowItem.getStack(true, StainedGlassWindow.Variant.DOVE));
			content.add(StainedGlassWindowItem.getStack(true, StainedGlassWindow.Variant.GABRIEL));
			content.add(StainedGlassWindowItem.getStack(true, StainedGlassWindow.Variant.SAINT));
			content.add(StainedGlassWindowItem.getStack(true, StainedGlassWindow.Variant.TREE));
			content.add(BlockRegistry.SLAB_BLOCK.asItem());
			content.add(BlockRegistry.RED_CARPET.asItem());
			content.add(BlockRegistry.ORANGE_CARPET.asItem());
			content.add(BlockRegistry.YELLOW_CARPET.asItem());
			content.add(BlockRegistry.LIME_CARPET.asItem());
			content.add(BlockRegistry.GREEN_CARPET.asItem());
			content.add(BlockRegistry.CYAN_CARPET.asItem());
			content.add(BlockRegistry.LIGHT_BLUE_CARPET.asItem());
			content.add(BlockRegistry.BLUE_CARPET.asItem());
			content.add(BlockRegistry.PURPLE_CARPET.asItem());
			content.add(BlockRegistry.MAGENTA_CARPET.asItem());
			content.add(BlockRegistry.PINK_CARPET.asItem());
			content.add(BlockRegistry.BROWN_CARPET.asItem());
			content.add(BlockRegistry.BLACK_CARPET.asItem());
			content.add(BlockRegistry.GRAY_CARPET.asItem());
			content.add(BlockRegistry.LIGHT_GRAY_CARPET.asItem());
			content.add(BlockRegistry.WHITE_CARPET.asItem());
			content.add(BlockRegistry.FLOWERBED.asItem());
			content.add(BlockRegistry.HYACINTH.asItem());
			content.add(BlockRegistry.SHEETMETAL.asItem());
			content.add(BlockRegistry.SHEETMETAL_SHEET.asItem());
			content.add(BlockRegistry.SHEETMETAL_SHEET_STAIRS.asItem());
			content.add(BlockRegistry.SHEETMETAL_SHEET_SLAB.asItem());
			content.add(BlockRegistry.COLUMN1.asItem());
			content.add(BlockRegistry.COLUMN1_STAIRS.asItem());
			content.add(BlockRegistry.COLUMN2.asItem());
			content.add(BlockRegistry.COLUMN2_STAIRS.asItem());
			content.add(BlockRegistry.BRIGHT_PANEL.asItem());
			content.add(BlockRegistry.BRIGHT_PANEL_STAIRS.asItem());
			content.add(BlockRegistry.FRAMED.asItem());
			content.add(BlockRegistry.CIRCUITY.asItem());
			content.add(BlockRegistry.ZOOTYCOONCHAINLINKFENCE.asItem());
			content.add(BlockRegistry.CONCRETE_SMOOTH.asItem());
			content.add(BlockRegistry.CONCRETE_SMOOTH_STAIRS.asItem());
			content.add(BlockRegistry.CONCRETE_SMOOTH_SLAB.asItem());
			content.add(BlockRegistry.CONCRETE_TILE.asItem());
			content.add(BlockRegistry.CONCRETE_TILE_STAIRS.asItem());
			content.add(BlockRegistry.CONCRETE_TILE_SLAB.asItem());
			content.add(BLOOD_BUCKET);
			content.add(PIERCE_REVOLVER);
			content.add(MARKSMAN_REVOLVER);
			content.add(MARKSMAN_REVOLVER.getStackedMarksman());
			content.add(SHARPSHOOTER_REVOLVER);
			content.add(SHARPSHOOTER_REVOLVER.getStackedSharpshooter());
			content.add(ALTERNATE_PIERCER);
			content.add(ALTERNATE_MARKSMAN);
			content.add(ALTERNATE_SHARPSHOOTER);
			content.add(CORE_SHOTGUN);
			content.add(PUMP_SHOTGUN);
			content.add(SAW_SHOTGUN);
			content.add(ATTRACTOR_NAILGUN);
			content.add(OVERHEAT_NAILGUN);
			content.add(JUMPSTART_NAILGUN);
			content.add(MACHINE_SWORD.getDefaultStack(MachineSwordItem.Type.NORMAL));
			content.add(MACHINE_SWORD.getDefaultStack(MachineSwordItem.Type.TUNDRA));
			content.add(MACHINE_SWORD.getDefaultStack(MachineSwordItem.Type.AGONY));
			content.add(FLAMETHROWER);
			content.add(HARPOON);
			content.add(HARPOON_GUN);
			content.add(FEEDBACKER);
			content.add(HIVEL_WINGS);
			content.add(ABSORBANT_PLATING);
			content.add(VIEW_AUGMENT);
			content.add(KNUCKLEBLASTER);
			content.add(SOAP);
			content.add(FILTH_SPAWN_EGG);
			content.add(STRAY_SPAWN_EGG);
			content.add(SCHISM_SPAWN_EGG);
			content.add(MALICIOUS_SPAWN_EGG);
			content.add(CERBERUS_SPAWN_EGG);
			content.add(SWORDSMACHINE_SPAWN_EGG);
			content.add(SpecialSpawnEggItem.putLore(SWORDSMACHINE_SPAWN_EGG.getDefaultNamedStack("item.ultracraft.swordsmachine_spawn_egg.dan", "Dan"),
					new String[] {"item.ultracraft.swordsmachine_spawn_egg.dan.lore"}, new String[] {"item.ultracraft.swordsmachine_spawn_egg.dan.hiddenlore"}));
			content.add(SWORDSMACHINE_SPAWN_EGG.getDefaultBossStack("item.ultracraft.swordsmachine_spawn_egg.unremarkable", false));
			content.add(DESTINY_SWORDSMACHINE_SPAWN_EGG);
			content.add(DRONE_SPAWN_EGG);
			content.add(STREET_CLEANER_SPAWN_EGG);
			content.add(HIDEOUS_SPAWN_EGG);
			content.add(HIDEOUS_SPAWN_EGG.getDefaultBossStack("item.ultracraft.hideous_spawn_egg.unremarkable", false));
			content.add(V2_SPAWN_EGG);
			content.add(RODENT_SPAWN_EGG);
			content.add(GREATERFILTH_SPAWN_EGG);
			content.add(SOUL_ORB);
			content.add(BLOOD_ORB);
			content.add(DECORATIVE_MAURICE);
			content.add(PLUSHIE.getDefaultStack("yaya"));
			content.add(TALON.getDefaultStack("talon"));
			content.add(ASHEN.getDefaultStack("ashenwulf"));
			content.add(PLUSHIE.getDefaultStack("hakita"));
			content.add(PITR.getDefaultStack("pitr"));
			content.add(PLUSHIE.getDefaultStack("v1"));
			content.add(V2.getDefaultStack("v2"));
			content.add(SWORDSMACHINE.getDefaultStack("swordsmachine"));
			content.add(SWORDSMACHINE.getDefaultStack("tundra"));
			content.add(SWORDSMACHINE.getDefaultStack("agony"));
			content.add(DRONE_MASK);
			for (TerminalBlockEntity.Base b : TerminalBlockEntity.Base.values())
				if(!b.equals(TerminalBlockEntity.Base.RGB))
					content.add(TerminalItem.getStack(b));
			content.add(BlockRegistry.HELL_OBSERVER.asItem());
			content.add(BlockRegistry.HELL_SPAWNER.asItem());
			content.add(HELL_MASS);
			content.add(SkyBlockItem.getStack(SkyBlockEntity.SkyType.DAY));
			content.add(SkyBlockItem.getStack(SkyBlockEntity.SkyType.FIRE));
			content.add(SkyBlockItem.getStack(SkyBlockEntity.SkyType.EVENING));
			content.add(SkyBlockItem.getStack(SkyBlockEntity.SkyType.NIGHT));
			content.add(SkyBlockItem.getStack(SkyBlockEntity.SkyType.LUNA));
			content.add(BlockRegistry.FAKE_LEAVES.asItem());
			content.add(HANK);
			content.add(PORTAL);
			content.add(BlockRegistry.DARKNESS);
		});
		//Dispenser Behaviors
		DispenserBlock.registerBehavior(HELL_BULLET, new ProjectileDispenserBehavior(){
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				HellBulletEntity bullet = new HellBulletEntity(EntityRegistry.HELL_BULLET, world);
				bullet.setItem(stack);
				bullet.setPos(position.getX(), position.getY(), position.getZ());
				return bullet;
			}
		});
		DispenserBlock.registerBehavior(CERBERUS_BALL, new ProjectileDispenserBehavior(){
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				CerberusBallEntity bullet = new CerberusBallEntity(EntityRegistry.CERBERUS_BALL, world);
				bullet.setItem(stack);
				bullet.setPos(position.getX(), position.getY(), position.getZ());
				return bullet;
			}
		});
		DispenserBlock.registerBehavior(CANCER_BULLET, new ProjectileDispenserBehavior(){
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				CancerBulletEntity bullet = new CancerBulletEntity(EntityRegistry.CANCER_BULLET, world);
				bullet.setItem(stack);
				bullet.setPos(position.getX(), position.getY(), position.getZ());
				return bullet;
			}
		});
		DispenserBlock.registerBehavior(SOAP, new ProjectileDispenserBehavior(){
			@Override
			protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
				ThrownSoapEntity bullet = new ThrownSoapEntity(EntityRegistry.SOAP, world);
				bullet.setPos(position.getX(), position.getY(), position.getZ());
				return bullet;
			}
		});
		DispenserBlock.registerBehavior(SOUL_ORB, new ItemDispenserBehavior(){
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack)
			{
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				BlockPos blockPos = pointer.getPos().offset(direction);
				SoulOrbEntity orb = EntityRegistry.SOUL_ORB.spawn(pointer.getWorld(), stack.getNbt(), i -> {}, blockPos,
						SpawnReason.DISPENSER, false, false);
				if (orb != null)
					stack.decrement(1);
				return stack;
			}
		});
		DispenserBlock.registerBehavior(BLOOD_ORB, new ItemDispenserBehavior(){
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack)
			{
				Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
				BlockPos blockPos = pointer.getPos().offset(direction);
				BloodOrbEntity orb = EntityRegistry.BLOOD_ORB.spawn(pointer.getWorld(), stack.getNbt(), i -> {}, blockPos,
						SpawnReason.DISPENSER, false, false);
				if (orb != null)
					stack.decrement(1);
				return stack;
			}
		});
		DispenserBlock.registerBehavior(BLOOD_BUCKET, new ItemDispenserBehavior(){
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack)
			{
				FluidModificationItem item = (FluidModificationItem)stack.getItem();
				BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
				ServerWorld world = pointer.getWorld();
				if (item.placeFluid(null, world, blockPos, null)) {
					item.onEmptied(null, world, stack, blockPos);
					return new ItemStack(Items.BUCKET);
				}
				return stack;
			}
		});
		Registry.register(Registries.ITEM_GROUP, EDIT_MODE_TAB,
				FabricItemGroup.builder().displayName(Text.translatable("itemGroup.ultracraft.edit")).icon(() -> new ItemStack(BlockRegistry.MAP_TRIGGER)).build());
		ItemGroupEvents.modifyEntriesEvent(EDIT_MODE_TAB).register(content -> {
			content.add(BlockRegistry.MAP_ROOM);
			content.add(BlockRegistry.MAP_TRIGGER);
			content.add(BlockRegistry.MAP_ENEMY_TRIGGER);
			content.add(BlockRegistry.MAP_CHECKPOINT);
			content.add(BlockRegistry.MAP_PROGRESSION);
			content.add(BlockRegistry.MAP_TIMER);
			content.add(BlockRegistry.MAP_TRAVEL);
			content.add(BlockRegistry.MAP_ABYSS);
			content.add(BlockRegistry.MAP_TITLE);
			content.add(BlockRegistry.MAP_TITLE_LISTENER);
			content.add(BlockRegistry.MAP_MUSIC);
			content.add(BlockRegistry.MAP_MUSIC_LISTENER);
			content.add(BlockRegistry.MAP_DAMAGE);
			content.add(BlockRegistry.MAP_LEVEL);
			content.add(BlockRegistry.MAP_REDSTONE);
			content.add(BlockRegistry.MAP_RECEIVER);
			content.add(BlockRegistry.MAP_DOOR);
			content.add(BlockRegistry.MAP_SPAWNER);
			content.add(BlockRegistry.MAP_SOUND);
			content.add(BlockRegistry.MAP_EXPLOSION);
			content.add(BlockRegistry.MAP_CYBERGRIND);
			content.add(BlockRegistry.MAP_LIGHT);
			content.add(BlockRegistry.MAP_GLOBAL_REDSTONE);
			content.add(BlockRegistry.MAP_GLOBAL_RECEIVER);
			content.add(BlockRegistry.MAP_GLOBAL_TITLE);
		});
		Registry.register(Registries.ITEM_GROUP, MUSIC_TAB,
				FabricItemGroup.builder().displayName(Text.translatable("itemGroup.ultracraft.music")).icon(() -> new ItemStack(ItemRegistry.CLAIR_DE_LUNE_DISK)).build());
		ItemGroupEvents.modifyEntriesEvent(MUSIC_TAB).register(content -> {
			content.add(ItemRegistry.FIRE_IS_GONE_DISK);
			content.add(ItemRegistry.PRELUDE1_CALM_DISK);
			content.add(ItemRegistry.PRELUDE1_DISK);
			content.add(ItemRegistry.PRELUDE2_CALM_DISK);
			content.add(ItemRegistry.PRELUDE2_DISK);
			content.add(ItemRegistry.CERBERUS_CALM_DISK);
			content.add(ItemRegistry.CERBERUS_DISK);
			content.add(ItemRegistry.LIMBO1_ILLUSION_DISK);
			content.add(ItemRegistry.LIMBO1_CALM_DISK);
			content.add(ItemRegistry.LIMBO1_DISK);
			content.add(ItemRegistry.LIMBO2_CALM_DISK);
			content.add(ItemRegistry.LIMBO2_DISK);
			content.add(ItemRegistry.CLAIR_DE_LUNE_DISK);
			content.add(ItemRegistry.VERSUS_DISK);
			content.add(ItemRegistry.LIMBO_FREEROAM_DISK);
			content.add(ItemRegistry.CYBERGRIND_DISK);
		});
	}
}
