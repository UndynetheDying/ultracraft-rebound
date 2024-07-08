package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundRegistry
{
	//ENEMIES
	public static final SoundEvent GENERIC_SPAWN = register("entity.generic.spawn");
	public static final SoundEvent GENERIC_FIRE= register("entity.generic.fire");
	public static final SoundEvent MACHINE_DAMAGE = register("entity.generic.machine.damage");
	public static final SoundEvent MACHINE_DEATH = register("entity.generic.machine.death");
	public static final SoundEvent HUSK_DAMAGE = register("entity.generic.husk.damage");
	public static final SoundEvent HUSK_DEATH = register("entity.generic.husk.death");
	public static final SoundEvent GENERIC_ENRAGE = register("entity.generic.enrage");
	public static final SoundEvent ENRAGED_LOOP = register("entity.generic.enraged_loop");
	public static final SoundEvent GENERIC_INTERRUPT = register("entity.generic.interrupt");
	
	public static final SoundEvent FILTH_ATTACK = register("entity.filth.attack");
	
	public static final SoundEvent MAURICE_BEAM_TELL = register("entity.malicious_face.tell.beam");
	
	public static final SoundEvent CERB_RISE = register("entity.cerberus.rise");
	public static final SoundEvent CERB_CRACK = register("entity.cerberus.crack");
	public static final SoundEvent CERB_RAM_TELL = register("entity.cerberus.tell.ram");
	public static final SoundEvent CERB_THROW_TELL = register("entity.cerberus.tell.throw");
	public static final SoundEvent CERB_STOMP_TELL = register("entity.cerberus.tell.stomp");
	public static final SoundEvent CERB_DEATH = register("entity.cerberus.death");
	
	public static final SoundEvent SWORDSMACHINE_ENRAGE = register("entity.swordsmachine.enrage");
	public static final SoundEvent SWORDSMACHINE_DEATH = register("entity.swordsmachine.death");
	
	public static final SoundEvent DRONE_CHARGE = register("entity.drone.charge");
	public static final SoundEvent DRONE_DEATH = register("entity.drone.death");
	
	public static final SoundEvent STREET_CLEANER_BREATHE = register("entity.street_cleaner.breathe");
	
	public static final SoundEvent HIDEOUS_MASS_EMERGE = register("entity.hideous_mass.emerge_step");
	public static final SoundEvent HIDEOUS_MASS_TURN = register("entity.hideous_mass.turn");
	public static final SoundEvent HIDEOUS_MASS_MORTAR = register("entity.hideous_mass.mortar");
	public static final SoundEvent HIDEOUS_MASS_SLAM_TELL = register("entity.hideous_mass.tell.stomp");
	public static final SoundEvent HIDEOUS_MASS_CLAP_TELL = register("entity.hideous_mass.tell.clap");
	public static final SoundEvent HIDEOUS_MASS_HARPOON_TELL = register("entity.hideous_mass.tell.harpoon");
	public static final SoundEvent HIDEOUS_MASS_IMPACT = register("entity.hideous_mass.impact");
	public static final SoundEvent HIDEOUS_MASS_CLAP_IMPACT = register("entity.hideous_mass.clap_impact");
	public static final SoundEvent HIDEOUS_MASS_ENRAGED_LOOP = register("entity.hideous_mass.enraged_loop");
	public static final SoundEvent HIDEOUS_MASS_DEATH = register("entity.hideous_mass.death");
	
	public static final SoundEvent V2_CORE_EJECT_TELL = register("entity.v2.tell.core_eject");
	public static final SoundEvent V2_PIERCER_TELL = register("entity.v2.tell.piercer");
	public static final SoundEvent V2_DEATH = register("entity.v2.death");
	
	//WEAPONS
	public static final SoundEvent REVOLVER_FIRE = register("item.revolver.fire");
	public static final SoundEvent SLAB_REVOLVER_FIRE = register("item.alt_revolver.fire");
	public static final SoundEvent SLAB_REVOLVER_FIRE_DING = register("item.alt_revolver.fire_ding");
	public static final SoundEvent SLAB_REVOLVER_CLICK = register("item.alt_revolver.click");
	public static final SoundEvent REVOLVER_ALT_CHARGE = register("item.revolver.alt_charge");
	public static final SoundEvent PIERCER_CHARGE = register("item.piercer.charge");
	public static final SoundEvent PIERCER_FIRE = register("item.piercer.fire");
	public static final SoundEvent COIN_TOSS = register("item.marksman.coin_toss");
	public static final SoundEvent COIN_HIT_NEXT = register("entity.coin.hit_next");
	public static final SoundEvent COIN_WARNING = register("entity.coin.warning");
	public static final SoundEvent SHARPSHOOTER_SPIN = register("item.sharpshooter.spin");
	public static final SoundEvent SHARPSHOOTER_FIRE = register("item.sharpshooter.fire");
	
	public static final SoundEvent SHOTGUN_FIRE = register("item.shotgun.fire");
	public static final SoundEvent SHOTGUN_PARRY = register("item.shotgun.parry");
	public static final SoundEvent SHOTGUN_PUMP = register("item.shotgun.pump");
	public static final SoundEvent SHOTGUN_OVERPUMP_BEEP = register("item.shotgun.overpump_beep");
	public static final SoundEvent SHOTGUN_CORE_CHARGE = register("item.shotgun.core_charge");
	public static final SoundEvent SHOTGUN_OPEN = register("item.shotgun.open");
	public static final SoundEvent SHOTGUN_CLOSE = register("item.shotgun.close");
	public static final SoundEvent SHOTGUN_HISS = register("item.shotgun.hiss");
	
	public static final SoundEvent MACHINESWORD_ATTACK = register("item.machinesword.attack");
	
	public static final SoundEvent FLAMETHROWER_START = register("item.flamethrower.start");
	public static final SoundEvent FLAMETHROWER_LOOP = register("item.flamethrower.loop");
	public static final SoundEvent FLAMETHROWER_STOP = register("item.flamethrower.end");
	public static final SoundEvent FLAMETHROWER_OVERHEAT = register("item.flamethrower.overheat");
	
	public static final SoundEvent REPULSIVE_SKEWER_SHOOT = register("item.repulsive_skewer.shoot");
	public static final SoundEvent REPULSIVE_SKEWER_REEL = register("item.repulsive_skewer.reel");
	
	public static final SoundEvent NAILGUN_FIRE = register("item.nailgun.fire");
	public static final SoundEvent NAILGUN_MAGNET_FIRE = register("item.nailgun.magnet.fire");
	public static final SoundEvent NAILGUN_MAGNET_BEEP = register("item.nailgun.magnet.beep");
	public static final SoundEvent NAILGUN_JUMPSTART_HOOK_BREAK = register("item.nailgun.jumpstart_hook.break");
	
	public static final SoundEvent SKEWER_HIT_GROUND = register("entity.skewer.hit_ground");
	public static final SoundEvent SKEWER_BREAK = register("entity.skewer.break");
	public static final SoundEvent SKEWER_DISOWN = register("entity.skewer.disown");
	
	public static final SoundEvent ARM_SWITCH = register("arm.switch");
	public static final SoundEvent FEEDBACKER_PUNCH = register("arm.feedbacker.punch");
	public static final SoundEvent KNUCKLEBLASTER_PUNCH = register("arm.knuckleblaster.punch");
	public static final SoundEvent KNUCKLEBLASTER_RELOAD = register("arm.knuckleblaster.reload");
	
	//BLOCKS
	public static final SoundEvent STAINED_GLASS_WINDOW_PLACE = register("blocks.stained_glass_window.place");
	public static final SoundEvent STAINED_GLASS_WINDOW_BREAK = register("blocks.stained_glass_window.break");
	public static final SoundEvent CHECKPOINT_GET = register("blocks.checkpoint.get");
	public static final SoundEvent SLAB_ACTIVATE = register("blocks.slab_block.activate");
	public static final SoundEvent SLAB_DEACTIVATE = register("blocks.slab_block.deactivate");
	
	//MISC
	public static final SoundEvent ELEVATOR_FALL = register("misc.elevator_fall");
	public static final SoundEvent WIND_LOOP = register("misc.wind_loop");
	public static final SoundEvent BAD_EXPLOSION = register("misc.bad_explosion");
	public static final SoundEvent MACHINESWORD_LOOP = register("entity.machinesword_loop");
	public static final SoundEvent KILLERFISH_SELECT = register("item.killerfish.select");
	public static final SoundEvent KILLERFISH_USE = register("item.killerfish.use");
	public static final SoundEvent LUMPFISH_SELECT = register("item.lumpfish.select");
	public static final SoundEvent LUMPFISH_UNSELECT = register("item.lumpfish.unselect");
	public static final SoundEvent LUMPFISH_USE = register("item.lumpfish.use");
	public static final SoundEvent BLAHAJ_USE = register("item.blahaj.use");
	public static final SoundEvent ORB_AMBIENT = register("entity.orb.ambient");
	public static final SoundEvent BARRIER_BREAK = register("entity.barrier_break");
	public static final SoundEvent BLOOD_HEAL = register("entity.blood_heal");
	public static final SoundEvent DASH = register("entity.dash");
	public static final SoundEvent DASH_JUMP = register("entity.dash-jump");
	public static final SoundEvent NO_STAMINA = register("misc.insufficient_stamina");
	public static final SoundEvent STAMINA_REGEN = register("misc.stamina_regen");
	public static final SoundEvent SLAM = register("misc.slam");
	public static final SoundEvent PARRY = register("misc.parry");
	public static final SoundEvent WATER_SKIM = register("misc.water_skim");
	public static final SoundEvent LAUGH = register("misc.laugh");
	public static final SoundEvent RECEIVE_BOX_TITLE = register("misc.box-title");
	public static final SoundEvent HUSK_SCREAM_LOOP = register("misc.husk_scream");
	public static final SoundEvent SPIN = register("misc.spin");
	public static final SoundEvent SACRIFICE = register("misc.sacrifice");
	public static final SoundEvent I_SAWED = register("misc.i_sawed");
	public static final SoundEvent PLACEHOLDER = register("placeholder");
	
	//MUSIC
	public static final RegistryEntry.Reference<SoundEvent> THE_FIRE_IS_GONE = registerReference("music.the_fire_is_gone");
	public static final RegistryEntry.Reference<SoundEvent> CLAIR_DE_LUNE = registerReference("music.clair_de_lune");
	public static final RegistryEntry.Reference<SoundEvent> PRELUDE1 = registerReference("music.prelude1");
	public static final RegistryEntry.Reference<SoundEvent> PRELUDE1_CALM = registerReference("music.prelude1_calm");
	public static final RegistryEntry.Reference<SoundEvent> PRELUDE2 = registerReference("music.prelude2");
	public static final RegistryEntry.Reference<SoundEvent> PRELUDE2_CALM = registerReference("music.prelude2_calm");
	public static final RegistryEntry.Reference<SoundEvent> CERBERUS = registerReference("music.cerberus");
	public static final RegistryEntry.Reference<SoundEvent> CERBERUS_CALM = registerReference("music.cerberus_calm");
	public static final RegistryEntry.Reference<SoundEvent> LIMBO1_ILLUSION = registerReference("music.limbo1_illusion");
	public static final RegistryEntry.Reference<SoundEvent> LIMBO1 = registerReference("music.limbo1");
	public static final RegistryEntry.Reference<SoundEvent> LIMBO1_CALM = registerReference("music.limbo1_calm");
	public static final RegistryEntry.Reference<SoundEvent> LIMBO2 = registerReference("music.limbo2");
	public static final RegistryEntry.Reference<SoundEvent> LIMBO2_CALM = registerReference("music.limbo2_calm");
	public static final RegistryEntry.Reference<SoundEvent> VERSUS = registerReference("music.versus");
	public static final RegistryEntry.Reference<SoundEvent> CYBERGRIND = registerReference("music.cybergrind");
	public static final RegistryEntry.Reference<SoundEvent> CYBERGRIND_DISK = registerReference("music.efefski_cybergrind");
	
	public static final MusicSound CYBERGRIND_MUSIC = new MusicSound(CYBERGRIND, 0, 0, true);
	
	public static void register()
	{
	
	}
	
	private static RegistryEntry.Reference<SoundEvent> registerReference(String id)
	{
		Identifier identifier = Ultracraft.identifier(id);
		return Registry.registerReference(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}
	
	private static SoundEvent register(String id)
	{
		Identifier identifier = Ultracraft.identifier(id);
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}
}
