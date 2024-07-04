package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.*;
import absolutelyaya.ultracraft.accessor.*;
import absolutelyaya.ultracraft.api.HeavyEntities;
import absolutelyaya.ultracraft.block.HellObserverBlockEntity;
import absolutelyaya.ultracraft.block.IPunchableBlock;
import absolutelyaya.ultracraft.block.AbstractPedestalBlock;
import absolutelyaya.ultracraft.block.TerminalBlockEntity;
import absolutelyaya.ultracraft.compat.TrinketUtil;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.level.IUltraLevelComponent;
import absolutelyaya.ultracraft.components.player.*;
import absolutelyaya.ultracraft.config.Config;
import absolutelyaya.ultracraft.config.EnumEntry;
import absolutelyaya.ultracraft.config.HivelConfig;
import absolutelyaya.ultracraft.config.ServerConfig;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.data.StyleBonusManager;
import absolutelyaya.ultracraft.data.UltraRecipeManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.cybergrind.CybergrindManager;
import absolutelyaya.ultracraft.entity.machine.DroneEntity;
import absolutelyaya.ultracraft.entity.projectile.AbstractSkewerEntity;
import absolutelyaya.ultracraft.entity.projectile.ChainsawEntity;
import absolutelyaya.ultracraft.entity.projectile.ThrownCoinEntity;
import absolutelyaya.ultracraft.item.weapons.AbstractWeaponItem;
import absolutelyaya.ultracraft.item.weapons.SoapItem;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Predicate;

public class PacketRegistry
{
	public static final Identifier PUNCH_PACKET_ID = Ultracraft.identifier("parry");
	public static final Identifier PUNCH_BLOCK_PACKET_ID = Ultracraft.identifier("punch_block");
	public static final Identifier PRIMARY_SHOT_C2S_PACKET_ID = Ultracraft.identifier("primary_shot_c2s");
	public static final Identifier SEND_WING_STATE_C2S_PACKET_ID = Ultracraft.identifier("set_winged_state_c2s");
	public static final Identifier SEND_WING_DATA_C2S_PACKET_ID = Ultracraft.identifier("set_winged_data_c2s");
	public static final Identifier DASH_C2S_PACKET_ID = Ultracraft.identifier("dash_c2s");
	public static final Identifier SLAM_C2S_PACKET_ID = Ultracraft.identifier("slam_c2s");
	public static final Identifier REQUEST_WINGED_DATA_PACKET_ID = Ultracraft.identifier("request_wing_data");
	public static final Identifier SKIM_C2S_PACKET_ID = Ultracraft.identifier("skim_c2s");
	public static final Identifier THROW_COIN_PACKET_ID = Ultracraft.identifier("throw_coin");
	public static final Identifier LOCK_PEDESTAL_ID = Ultracraft.identifier("lock_pedestal");
	public static final Identifier ANIMATION_C2S_PACKET_ID = Ultracraft.identifier("animation_c2s");
	public static final Identifier FISH_PACKET_ID = Ultracraft.identifier("fish");
	public static final Identifier TERMINAL_SYNC_C2S_PACKET_ID = Ultracraft.identifier("terminal_c2s");
	public static final Identifier GRAFFITI_C2S_PACKET_ID = Ultracraft.identifier("graffiti_c2s");
	public static final Identifier TERMINAL_REDSTONE_PACKET_ID = Ultracraft.identifier("terminal_redstone");
	public static final Identifier TERMINAL_WEAPON_CRAFT_PACKET_ID = Ultracraft.identifier("terminal_weapon_craft");
	public static final Identifier TERMINAL_WEAPON_DISPENSE_PACKET_ID = Ultracraft.identifier("terminal_weapon_dispense");
	public static final Identifier CYCLE_WEAPON_VARIANT_PACKET_ID = Ultracraft.identifier("cycle_weapon_variant");
	public static final Identifier HELL_OBSERVER_C2S_PACKET_ID = Ultracraft.identifier("hell_observer_c2s");
	public static final Identifier REQUEST_GRAFFITI_WHITELIST_PACKET_ID = Ultracraft.identifier("request_graffiti_whitelist");
	public static final Identifier ARM_CYCLE_PACKET_ID = Ultracraft.identifier("arm_cycle");
	public static final Identifier ARM_VISIBLE_PACKET_ID = Ultracraft.identifier("arm_visible");
	public static final Identifier PUNCH_PRESSED_PACKET_ID = Ultracraft.identifier("punch_pressed");
	public static final Identifier SYNC_CONFIG_C2S_PACKET_ID = Ultracraft.identifier("sync_config_c2s");
	public static final Identifier HIVEL_DATA_PACKET_ID = Ultracraft.identifier("hiveldata");
	public static final Identifier SLIDE_STATE_PACKET_ID = Ultracraft.identifier("slide_state");
	public static final Identifier SLAM_STATE_PACKET_ID = Ultracraft.identifier("slam_state");
	public static final Identifier SYNC_LOADOUT_PACKET_ID = Ultracraft.identifier("loadout");
	public static final Identifier TRAVEL_PACKET_ID = Ultracraft.identifier("travel");
	public static final Identifier ENTER_LEVEL_PACKET_ID = Ultracraft.identifier("enter_level");
	public static final Identifier REQUEST_DESTINATIONS_PACKET_ID = Ultracraft.identifier("destinations_c2s");
	public static final Identifier REQUEST_INSTANCES_PACKET_ID = Ultracraft.identifier("instances_c2s");
	public static final Identifier REQUEST_FULL_CYBERGRIND_PACKET_ID = Ultracraft.identifier("request_cybergrind");
	public static final Identifier SWITCH_SLOT_PACKET_ID = Ultracraft.identifier("slot_c2s");
	public static final Identifier SUBMIT_BEST_RANK_PACKET_ID = Ultracraft.identifier("rank_c2s");
	public static final Identifier SUBMIT_BEST_TIME_PACKET_ID = Ultracraft.identifier("time_c2s");
	
	public static final Identifier FREEZE_PACKET_ID = Ultracraft.identifier("freeze");
	public static final Identifier HITSCAN_PACKET_ID = Ultracraft.identifier("scan");
	public static final Identifier DASH_S2C_PACKET_ID = Ultracraft.identifier("dash_s2c");
	public static final Identifier BLEED_PACKET_ID = Ultracraft.identifier("bleed");
	public static final Identifier SET_GUNCD_PACKET_ID = Ultracraft.identifier("set_gcd");
	public static final Identifier CATCH_FISH_PACKET_ID = Ultracraft.identifier("fish");
	public static final Identifier SYNC_CONFIG_S2C_PACKET_ID = Ultracraft.identifier("sync_config_s2c");
	public static final Identifier FINISH_SYNC_CONFIG_S2C_PACKET_ID = Ultracraft.identifier("finish_sync_config_s2c");
	public static final Identifier ENTITY_TRAIL_PACKET_ID = Ultracraft.identifier("entity_trail");
	public static final Identifier SLAM_S2C_PACKET_ID = Ultracraft.identifier("slam_s2c");
	public static final Identifier EXPLOSION_PACKET_ID = Ultracraft.identifier("explosion");
	public static final Identifier PRIMARY_SHOT_S2C_PACKET_ID = Ultracraft.identifier("primary_shot_s2c");
	public static final Identifier DEBUG_PACKET_ID = Ultracraft.identifier("debug");
	public static final Identifier SKIM_S2C_PACKET_ID = Ultracraft.identifier("skim_s2c");
	public static final Identifier COIN_PUNCH_PACKET_ID = Ultracraft.identifier("coinpunch");
	public static final Identifier WORLD_INFO_PACKET_ID = Ultracraft.identifier("world_info");
	public static final Identifier BLOCK_PLAYER_PACKET_ID = Ultracraft.identifier("block");
	public static final Identifier UNBLOCK_PLAYER_PACKET_ID = Ultracraft.identifier("unblock");
	public static final Identifier OPEN_SERVER_CONFIG_MENU_PACKET_ID = Ultracraft.identifier("server_config");
	public static final Identifier RICOCHET_WARNING_PACKET_ID = Ultracraft.identifier("warn_ricochet");
	public static final Identifier REPLENISH_STAMINA_PACKET_ID = Ultracraft.identifier("replenish_stamina");
	public static final Identifier ANIMATION_S2C_PACKET_ID = Ultracraft.identifier("animation_s2c");
	public static final Identifier SOAP_KILL_PACKET_ID = Ultracraft.identifier("soapkill");
	public static final Identifier ULTRA_RECIPE_PACKET_ID = Ultracraft.identifier("ultra_recipe");
	public static final Identifier HIVEL_WHITELIST_PACKET_ID = Ultracraft.identifier("hivel_whitelist");
	public static final Identifier GRAFFITI_WHITELIST_PACKET_ID = Ultracraft.identifier("graffiti_whitelist");
	public static final Identifier HELL_OBSERVER_PACKET_ID = Ultracraft.identifier("hell_observer");
	public static final Identifier SCREENSHAKE_PACKET_ID = Ultracraft.identifier("screenshake");
	public static final Identifier STYLE_BONUS_PACKET_ID = Ultracraft.identifier("style");
	public static final Identifier TRAVEL_SCREEN_PACKET_ID = Ultracraft.identifier("open_travel_screen");
	public static final Identifier EDIT_PING_PACKET_ID = Ultracraft.identifier("edit_ping");
	public static final Identifier TITLE_PACKET_ID = Ultracraft.identifier("title");
	public static final Identifier SEND_DESTINATIONS_PACKET_ID = Ultracraft.identifier("destinations_s2c");
	public static final Identifier SEND_LEVELS_PACKET_ID = Ultracraft.identifier("levels_s2c");
	public static final Identifier SEND_LEVEL_INSTANCES_PACKET_ID = Ultracraft.identifier("instances_s2c");
	public static final Identifier FINISH_TRAVELLING_PACKET_ID = Ultracraft.identifier("travel_end");
	public static final Identifier ANNOUNCE_CYBERGRIND_PACKET_ID = Ultracraft.identifier("announce_cybergrind");
	public static final Identifier SYNC_CYBERGRIND_PACKET_ID = Ultracraft.identifier("sync_cybergrind");
	public static final Identifier PICKUP_PROGRESSION_ITEM_PACKET_ID = Ultracraft.identifier("pickup_progression");
	
	public static void registerC2S()
	{
		ServerPlayNetworking.registerGlobalReceiver(PUNCH_PACKET_ID, (server, player, handler, buf, sender) -> {
			World world = player.getWorld();
			Entity target;
			if(buf.readBoolean() && world instanceof ServerWorld serverWorld)
				target = serverWorld.getDragonPart(buf.readVarInt());
			else
				target = null;
			byte punchArm = buf.readByte();
			Vector3f clientVel = buf.readVector3f(); //velocity the player has on the client
			boolean debug = buf.readBoolean();
			server.execute(() -> {
				IArmComponent arm = UltraComponents.ARMS.get(player);
				if(arm.getUnlockedArmCount() == 0)
					return;
				if(punchArm != -1)
					arm.setActiveArm(punchArm);
				if(player instanceof LivingEntityAccessor accessor)
					accessor.punch();
				Vec3d forward = player.getRotationVector().normalize();
				player.swingHand(Hand.OFF_HAND, true);
				
				if(player.getOffHandStack().getItem() instanceof SoapItem soap)
				{
					soap.onOffhandThrow(world, player);
					return;
				}
				ServerConfig config = ServerConfig.INSTANCE;
				
				//Punch Entity; Takes Priority over Projectile Parries
				if(target != null && !(target instanceof DroneEntity drone && drone.isFalling() && arm.isFeedbacker()))
				{
					if(player.getOffHandStack().isIn(TagRegistry.PUNCH_FLAMES))
						target.setFireTicks(100);
					if (arm.isFeedbacker())
					{
						if(target instanceof MeleeInterruptable mp && (!(mp instanceof MobEntity) || ((MobEntity)mp).isAttacking()))
						{
							Ultracraft.freeze(player, 10);
							target.damage(DamageSources.get(world, DamageSources.INTERRUPT, player), 6);
							mp.onInterrupt(player);
							world.playSound(null, player.getBlockPos(), SoundRegistry.GENERIC_INTERRUPT, SoundCategory.PLAYERS, 0.75f, 2f);
							player.heal(4);
						}
						else if(target instanceof MinecartAccessor minecart)
							minecart.parry(player);
					}
					boolean knuckle = arm.isKnuckleblaster();
					world.playSound(null, player.getBlockPos(), knuckle ? SoundRegistry.KNUCKLEBLASTER_PUNCH : SoundRegistry.FEEDBACKER_PUNCH ,
							SoundCategory.PLAYERS, 0.75f, 0.5f);
					boolean targetDamaged = target.damage(DamageSources.get(world, knuckle ? DamageSources.KNUCKLE_PUNCH : DamageSources.PUNCH, player),
							knuckle ? config.knuckleblasterDamage.getValue() : config.feedbackerDamage.getValue());
					
					if(knuckle && target instanceof PlayerEntity hitPlayer && hitPlayer.getActiveItem().getItem() instanceof ShieldItem)
					{
						hitPlayer.disableShield(true);
						UltraComponents.STYLE.get(player).styleBonusGet(StyleBonusManager.getBonuses().get(Ultracraft.identifier("shieldbreak")));
					}
					
					boolean fatal = !target.isAlive();
					Vec3d vel = forward.multiply(fatal ? 1.5f : 0.75f);
					if(arm.isKnuckleblaster())
						vel = vel.multiply(1.5f);
					if(target instanceof ProjectileEntity || (target instanceof LivingEntityAccessor && ((LivingEntityAccessor)target).takePunchKnockback()))
						target.setVelocity(vel);
					if(HeavyEntities.isHeavy(target.getType()) && targetDamaged && target.isAlive())
						UltraComponents.STYLE.get(player).styleBonusGet(StyleBonusManager.getBonuses().get(Ultracraft.identifier("disrespect")));
					return;
				}
				
				//Projectile Parry
				//Fetch all Parry Candidate Projectiles
				boolean chainingAllowed = ServerConfig.INSTANCE.parryChaining.getValue();
				Vec3d pos = player.getEyePos();
				HashSet<Entity> parriables = new HashSet<>();
				parriables.addAll(fetchParryCandidates(player, pos, forward, config.parryRange.getValue(), clientVel, chainingAllowed,
						debug ? 1 : 0, null, null));
				parriables.addAll(fetchParryCandidates(player, pos, forward, config.parryRange.getValue(), clientVel, chainingAllowed,
						0, EntityRegistry.DRONE, e -> e instanceof IParriable parriable && parriable.isParriable()));
				parriables.addAll(fetchParryCandidates(player, pos, forward, config.coinPunchRange.getValue(), clientVel, chainingAllowed,
						debug ? 2 : 0, EntityRegistry.THROWN_COIN, null));
				
				//The actual Parry Logic
				IParriable parried;
				if(!parriables.isEmpty())
					parried = getNearestParriable(parriables, pos);
				else
					return;
				boolean feedbacker = arm.isFeedbacker();
				if(parried instanceof AbstractSkewerEntity skewer)
				{
					skewer.damage(DamageSources.get(world, feedbacker ? DamageSources.PUNCH : DamageSources.KNUCKLE_PUNCH), 1f);
					return;
				}
				if(!arm.isFeedbacker())
				{
					if(parried instanceof ChainsawEntity chainsaw)
					{
						Ultracraft.freeze(player, 10);
						chainsaw.onKnucklePunch(player);
					}
					return;
				}
				if(parried == null || !parried.isParriable())
					return;
				boolean heal = !player.equals(parried.getParriableOwner());
				if(heal && ((Entity)parried).age < 4)
				{
					if(((ProjectileEntityAccessor)parried).isBoostable())
					{
						heal = false;
						Ultracraft.freeze(player, 5); //ProjBoost freezes are shorter
					}
					else
						return;
				}
				else if(!(parried instanceof ThrownCoinEntity))
				{
					Ultracraft.freeze(player, 10);
					UltraComponents.STYLE.get(player).styleBonusGet(StyleBonusManager.getBonuses().get(Ultracraft.identifier("parry")));
					player.incrementStat(StatisticRegistry.PARRY);
				}
				world.playSound(null, player.getBlockPos(), SoundRegistry.PARRY, SoundCategory.PLAYERS, 0.75f, 2f);
				parried.setParried(true, player);
				((Entity)parried).setVelocity(forward.multiply(chainingAllowed ? 2f + 0.2f * ((ChainParryAccessor)parried).getParryCount() : 2.5f));
				if(heal && !(parried instanceof ThrownCoinEntity))
				{
					player.heal(player.getMaxHealth() - player.getHealth()); //full heal
					if(player instanceof WingedPlayerEntity winged)
						winged.onParry();
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PUNCH_BLOCK_PACKET_ID, (server, player, handler, buf, sender) -> {
			World world = player.getWorld();
			BlockPos target = buf.readBlockPos();
			boolean mainHand = buf.readBoolean();
			server.execute(() -> {
				if(target != null)
				{
					BlockState state = world.getBlockState(target);
					if(state.getBlock() instanceof IPunchableBlock punchable && !(mainHand && player.getMainHandStack().isOf(Items.DEBUG_STICK)))
						punchable.onPunch(player, target, mainHand);
					if(state.getBlock() instanceof BellBlock bell)
						bell.ring(player, player.getWorld(), target, player.getHorizontalFacing().getOpposite());
					IArmComponent arm = UltraComponents.ARMS.get(player);
					if(state.isIn(TagRegistry.PUNCH_BREAKABLE) || (arm.isKnuckleblaster() && state.isIn(TagRegistry.KNUCKLE_BREAKABLE)) &&
																		  player.canModifyAt(world, target))
						player.getWorld().breakBlock(target, true, player);
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PRIMARY_SHOT_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			byte action = buf.readByte();
			Vec3d velocity = action > 0 ? new Vec3d(buf.readVector3f()) : Vec3d.ZERO;
			server.execute(() -> {
				IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
				if (player.getMainHandStack().getItem() instanceof AbstractWeaponItem gun)
				{
					winged.setPrimaryFiring(action > 0);
					if (action == 0 || !gun.onPrimaryFire(player.getWorld(), player, velocity))
						return;
					for (ServerPlayerEntity p : ((ServerWorld)player.getWorld()).getPlayers(p -> player.distanceTo(p) < 128f))
					{
						PacketByteBuf cbuf = new PacketByteBuf(Unpooled.buffer());
						cbuf.writeUuid(player.getUuid());
						ServerPlayNetworking.send(p, PRIMARY_SHOT_S2C_PACKET_ID, cbuf);
					}
				}
				else if(action == 0)
					winged.setPrimaryFiring(false);
				else
					Ultracraft.LOGGER.warn("{} tried to use primary fire action but is holding a non-weapon Item!", player);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(SEND_WING_STATE_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			IWingDataComponent wings = UltraComponents.WING_DATA.get(player);
			IUltraLevelComponent level = UltraComponents.GLOBAL.get(player.getWorld().getLevelProperties());
			boolean whitelisted = level.isPlayerAllowedToHivel(player);
			boolean wingsActive = buf.readBoolean() && whitelisted &&
										  (UltraComponents.PROGRESSION.get(player).isUnlocked(ProgressionComponent.HIVEL) ||
												   (Ultracraft.TRINKETS && TrinketUtil.isHasTrinketEquipped(player, ItemRegistry.HIVEL_WINGS)));
			server.execute(() ->
			{
				wings.setActive(wingsActive);
				wings.sync();
				((WingedPlayerEntity)player).updateSpeedConfig();
				if(wingsActive)
				{
					player.setSneaking(false);
					player.setSprinting(false);
				}
				if(whitelisted)
					return;
				PacketByteBuf cbuf = new PacketByteBuf(Unpooled.buffer());
				ServerPlayNetworking.send(player, PacketRegistry.HIVEL_WHITELIST_PACKET_ID, cbuf);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(SEND_WING_DATA_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			IWingDataComponent wings = UltraComponents.WING_DATA.get(player);
			Vector3f wingColor = buf.readVector3f(), metalColor = buf.readVector3f();
			String pattern = buf.readString();
			String overlay = buf.readString();
			server.execute(() -> {
				wings.setColor(wingColor, 0);
				wings.setColor(metalColor, 1);
				wings.setPattern(Ultracraft.checkSupporter(player.getUuid(), false) ? pattern : "");
				wings.setOverlay(overlay);
				wings.sync();
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(DASH_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			Vec3d dir = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeUuid(player.getUuid());
			buf.writeDouble(dir.x);
			buf.writeDouble(dir.y);
			buf.writeDouble(dir.z);
			for (ServerPlayerEntity p : ((ServerWorld)player.getWorld()).getPlayers())
				ServerPlayNetworking.send(p, DASH_S2C_PACKET_ID, buf);
			server.execute(() -> {
				UltraComponents.HIVEL.get(player).onDash();
				player.incrementStat(StatisticRegistry.DASH);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(SLAM_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			boolean start = buf.readBoolean();
			boolean strong = buf.readBoolean();
			server.execute(() -> {
				WingedPlayerEntity winged = (WingedPlayerEntity)player;
				if(start)
					winged.startSlam();
				else
				{
					winged.endSlam(strong);
					player.incrementStat(StatisticRegistry.SLAM);
				}
				if(start)
					return;
				PacketByteBuf cbuf = new PacketByteBuf(Unpooled.buffer());
				cbuf.writeUuid(player.getUuid());
				cbuf.writeBlockPos(player.getSteppingPos());
				cbuf.writeBoolean(strong);
				for (ServerPlayerEntity p : ((ServerWorld)player.getWorld()).getPlayers())
					ServerPlayNetworking.send(p, SLAM_S2C_PACKET_ID, cbuf);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(REQUEST_WINGED_DATA_PACKET_ID, (server, player, handler, buf, sender) -> {
			UUID targetID = buf.readUuid();
			PlayerEntity target = server.getPlayerManager().getPlayer(targetID);
			if(target == null)
				return;
			UltraComponents.WING_DATA.get(player).sync();
			//buf = new PacketByteBuf(Unpooled.buffer());
			//buf.writeUuid(targetID);
			//buf.writeBoolean(wings.isVisible());
			//buf.writeVector3f(wings.getColors()[0]);
			//buf.writeVector3f(wings.getColors()[1]);
			//buf.writeString(wings.getPattern());
			//ServerPlayNetworking.send(player, WING_DATA_S2C_PACKET_ID, buf);
		});
		ServerPlayNetworking.registerGlobalReceiver(SKIM_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			if(player == null)
				return;
			Vec3d pos = new Vec3d(buf.readVector3f());
			PacketByteBuf cbuf = new PacketByteBuf(Unpooled.buffer());
			cbuf.writeVector3f(pos.toVector3f());
			player.getWorld().getPlayers().forEach(p -> {
				if(player.squaredDistanceTo(p) < 32f * 32f)
					ServerPlayNetworking.send((ServerPlayerEntity)p, SKIM_S2C_PACKET_ID, cbuf);
			});
			server.execute(() -> {
				player.playSound(SoundRegistry.WATER_SKIM, SoundCategory.PLAYERS, 1f, 0.8f + player.getRandom().nextFloat() * 0.4f);
				player.getWorld().addParticle(ParticleRegistry.RIPPLE, pos.x, pos.y, pos.z, 0, 0, 0);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(THROW_COIN_PACKET_ID, (server, player, handler, buf, sender) -> {
			if(player == null)
				return;
			Vec3d pos = new Vec3d(buf.readVector3f());
			Vec3d vel = new Vec3d(buf.readVector3f());
			server.execute(() -> {
				ThrownCoinEntity coin = ThrownCoinEntity.spawn(player, player.getWorld());
				float f = player.isOnGround() ? 1.75f : 1.275f;
				Vec3d p = pos.add(vel.multiply(f, 0.25f, f));
				coin.setPosition(p);
				Vec3d rot = player.getRotationVector();
				coin.setVelocity(rot.x, rot.y, rot.z, 0.6f, 0f);
				coin.addVelocity(vel.multiply(f, 1f, f));
				coin.addVelocity(0f, 0.55f, 0f);
				player.getWorld().spawnEntity(coin);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(LOCK_PEDESTAL_ID, (server, player, handler, buf, sender) -> {
			BlockPos pos = buf.readBlockPos();
			Boolean b = buf.readBoolean();
			server.execute(() -> {
				if(player.getWorld().getBlockState(pos).getBlock() instanceof AbstractPedestalBlock)
					player.getWorld().setBlockState(pos, player.getWorld().getBlockState(pos).with(AbstractPedestalBlock.LOCKED, b));
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(ANIMATION_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			PacketByteBuf cbuf = new PacketByteBuf(Unpooled.buffer());
			cbuf.writeUuid(player.getUuid()); //target
			cbuf.writeInt(buf.readInt()); //animID
			cbuf.writeInt(buf.readInt()); //fade
			cbuf.writeBoolean(buf.readBoolean()); //firstPerson
			player.getWorld().getPlayers().forEach(p -> {
				if(p != player)
					ServerPlayNetworking.send((ServerPlayerEntity)p, ANIMATION_S2C_PACKET_ID, cbuf);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(FISH_PACKET_ID, (server, player, handler, buf, sender) -> {
			int data = buf.readInt();
			server.execute(() -> player.getWorld().playSound(null, player.getBlockPos(),
					FishPacket.values()[data].sound, SoundCategory.PLAYERS, 1f, 1f));
		});
		ServerPlayNetworking.registerGlobalReceiver(TERMINAL_SYNC_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			BlockPos pos = buf.readBlockPos();
			int textColor = buf.readInt();
			int baseColor = buf.readInt();
			int base = buf.readInt();
			NbtCompound screenSaver = buf.readNbt();
			NbtCompound mainMenu = buf.readNbt();
			server.execute(() ->  {
				BlockEntity be = player.getWorld().getBlockEntity(pos);
				if(be instanceof TerminalBlockEntity terminal)
					terminal.applyCustomization(textColor, baseColor, base, screenSaver, mainMenu);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(GRAFFITI_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			BlockPos pos = buf.readBlockPos();
			int[] palette = buf.readIntArray(15);
			byte[] pixels = buf.readByteArray();
			int revision = buf.readInt();
			byte version = buf.readByte();
			server.execute(() -> {
				BlockEntity be = player.getWorld().getBlockEntity(pos);
				if(be instanceof TerminalBlockEntity terminal)
				{
					terminal.setPalette(Arrays.asList(ArrayUtils.toObject(palette)));
					terminal.setGraffiti(ByteArrayList.of(pixels));
					terminal.setGraffitiRevision(revision);
					terminal.setGraffitiVersion(version);
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(TERMINAL_REDSTONE_PACKET_ID, (server, player, handler, buf, sender) -> {
			BlockPos pos = buf.readBlockPos();
			int strength = buf.readInt();
			server.execute(() -> {
				BlockEntity be = player.getWorld().getBlockEntity(pos);
				if(be instanceof TerminalBlockEntity terminal)
					terminal.redstoneImpulse(MathHelper.clamp(strength, 0, 15));
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(TERMINAL_WEAPON_CRAFT_PACKET_ID, (server, player, handler, buf, sender) -> {
			Identifier recipe = buf.readIdentifier();
			server.execute(() -> UltraRecipeManager.getRecipe(recipe).craft(player));
		});
		ServerPlayNetworking.registerGlobalReceiver(TERMINAL_WEAPON_DISPENSE_PACKET_ID, (server, player, handler, buf, sender) -> {
			Identifier weapon = buf.readIdentifier();
			int weaponType = buf.readInt();
			boolean alt = buf.readBoolean();
			server.execute(() ->
			{
				if(UltraComponents.PROGRESSION.get(player).isOwned(weapon))
				{
					ItemStack stack;
					if(alt)
						stack = Registries.ITEM.get(Weapon.values()[weaponType].getAlt(weapon)).getDefaultStack();
					else
						stack = Registries.ITEM.get(weapon).getDefaultStack();
					player.giveItemStack(stack);
				}
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(CYCLE_WEAPON_VARIANT_PACKET_ID, (server, player, handler, buf, sender) -> {
			server.execute(() -> AbstractWeaponItem.cycleVariant(player));
		});
		ServerPlayNetworking.registerGlobalReceiver(HELL_OBSERVER_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			BlockPos pos = buf.readBlockPos();
			int playerCount = buf.readInt();
			int playerOperator = buf.readInt();
			int enemyCount = buf.readInt();
			int enemyOperator = buf.readInt();
			boolean requireBoth = buf.readBoolean();
			Vector3f offset = buf.readVector3f();
			Vector3f size = buf.readVector3f();
			boolean previewArea = buf.readBoolean();
			server.execute(() -> {
				if(!(player.getWorld().getBlockEntity(pos) instanceof HellObserverBlockEntity observer))
					return;
				observer.sync(playerCount, playerOperator, enemyCount, enemyOperator, requireBoth,
						new Vec3i((int)offset.x, (int)offset.y, (int)offset.z), new Vec3i((int)size.x, (int)size.y, (int)size.z), previewArea);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(REQUEST_GRAFFITI_WHITELIST_PACKET_ID, (server, player, handler, buf, sender) -> {
			server.execute(() ->
			{
				PacketByteBuf cbuf = new PacketByteBuf(Unpooled.buffer());
				cbuf.writeBoolean(UltraComponents.GLOBAL.get(player.getWorld().getLevelProperties()).isPlayerAllowedToGraffiti(player));
				ServerPlayNetworking.send(player, GRAFFITI_WHITELIST_PACKET_ID, cbuf);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(ARM_CYCLE_PACKET_ID, (server, player, handler, buf, sender) -> {
			server.execute(() -> UltraComponents.ARMS.get(player).cycleArms());
		});
		ServerPlayNetworking.registerGlobalReceiver(ARM_VISIBLE_PACKET_ID, (server, player, handler, buf, sender) -> {
			boolean v = buf.readBoolean();
			server.execute(() -> UltraComponents.ARMS.get(player).setArmVisible(v));
		});
		ServerPlayNetworking.registerGlobalReceiver(PUNCH_PRESSED_PACKET_ID, (server, player, handler, buf, sender) -> {
			boolean v = buf.readBoolean();
			server.execute(() -> UltraComponents.ARMS.get(player).setPunchPressed(v));
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.SYNC_CONFIG_C2S_PACKET_ID, (server, player, handler, buf, sender) -> {
			String configId = buf.readString();
			String rule = buf.readString();
			byte type = buf.readByte();
			Config config = Config.getFromID(configId);
			switch(type)
			{
				default -> ServerConfig.onChanged(server, configId, config.set(rule, buf.readInt()));
				case 69 -> ServerConfig.onChanged(server, configId, config.set((EnumEntry<?>)config.getEntry(rule), buf.readInt()));
				case NbtElement.FLOAT_TYPE -> {
					float v = buf.readFloat();
					ServerConfig.onChanged(server, configId, config.set(rule, v));
					if(rule.equals(HivelConfig.INSTANCE.speed.getId()))
						server.getPlayerManager().getPlayerList().forEach(p -> ((WingedPlayerEntity)p).updateSpeedConfig());
				}
				case NbtElement.BYTE_TYPE -> ServerConfig.onChanged(server, configId, config.set(rule, buf.readBoolean()));
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.HIVEL_DATA_PACKET_ID, (server, player, handler, buf, sender) -> {
			boolean ignoreSlowdown = buf.readBoolean();
			server.execute(() -> {
				IHivelComponent hivel = UltraComponents.HIVEL.get(player);
				hivel.setIgnoreSlowdown(ignoreSlowdown);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.SLIDE_STATE_PACKET_ID, (server, player, handler, buf, sender) -> {
			boolean slide = buf.readBoolean();
			server.execute(() -> {
				if(player instanceof WingedPlayerEntity winged)
					winged.setSliding(slide);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.SLAM_STATE_PACKET_ID, (server, player, handler, buf, sender) -> {
			boolean slam = buf.readBoolean();
			server.execute(() -> {
				if(player instanceof WingedPlayerEntity winged)
					winged.setSlamming(slam);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.SYNC_LOADOUT_PACKET_ID, (server, player, handler, buf, sender) -> {
			Weapon weapon = Weapon.values()[buf.readInt()];
			int count = buf.readInt();
			Identifier[] ids = new Identifier[count];
			for (int i = 0; i < count; i++)
				ids[i] = buf.readIdentifier();
			server.execute(() -> {
				//check all items
				if(ids.length > 0)
				{
					List<ItemStack> inventory = player.getInventory().main;
					for (int slot = 0; slot < inventory.size(); slot++)
					{
						//if the item is a weapon of this loadouts type
						if(!(inventory.get(slot).getItem() instanceof AbstractWeaponItem w && w.getWeaponType().equals(weapon)))
							continue;
						boolean found = false;
						//check if its variant is in the loadout
						for (Identifier id : ids)
						{
							if (id.equals(Registries.ITEM.getId(w)))
							{
								found = true;
								break;
							}
						}
						//if its not found in the loadout, set its variant to the first available one.
						if(!found)
							AbstractWeaponItem.replaceVariant(inventory.get(slot), player, slot, Registries.ITEM.get(ids[0]));
					}
				}
				//set loadout
				UltraComponents.LOADOUT.get(player).setLoadoutForWeapon(weapon, ids);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.TRAVEL_PACKET_ID, (server, player, handler, buf, sender) -> {
			int id = buf.readInt();
			Layer layer = Layer.values()[id];
			server.execute(() -> {
				Identifier progression = layer.progression;
				if(!UltraComponents.GLOBAL.get(player.getWorld().getLevelProperties()).isDestinationUnlocked(progression))
				{
					player.sendMessage(Text.translatable("message.ultracraft.travel.error-notunlocked"));
					Ultracraft.LOGGER.warn(player + " tried to travel to locked destination: '" + progression + "'");
					onTravelFinished(player);
					return;
				}
				UltraComponents.LEVEL_STATS.get(player).enterLevel(null, null);
				ServerWorld world = server.getWorld(layer.getWorldKey());
				BlockPos pos = layer.arrivalPos == null ? world.getSpawnPos() : layer.arrivalPos;
				FabricDimensions.teleport(player, world, new TeleportTarget(pos.toCenterPos(), Vec3d.ZERO, world.getSpawnAngle(), 0f));
				onTravelFinished(player);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.ENTER_LEVEL_PACKET_ID, (server, player, handler, buf, sender) -> {
			Identifier level = buf.readIdentifier();
			String instanceId = buf.readString();
			boolean privat;
			if(instanceId.equals("private"))
			{
				instanceId = "";
				privat = true;
			}
			else
				privat = false;
			String finalInstanceId = instanceId;
			server.execute(() -> {
				
				if(!UltraComponents.GLOBAL.get(player.getWorld().getLevelProperties()).isDestinationUnlocked(level))
				{
					player.sendMessage(Text.translatable("message.ultracraft.travel.error-notunlocked"));
					Ultracraft.LOGGER.warn(player + " tried to travel to locked destination: '" + level + "'");
					onTravelFinished(player);
					return;
				}
				Pair<String, LevelManager.LevelInstance> instance;
				ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(player);
				if(!finalInstanceId.isEmpty() && LevelManager.Instance.isInstanceExistant(finalInstanceId))
				{
					instance = new Pair<>(finalInstanceId, LevelManager.Instance.getInstance(finalInstanceId));
					if(levelStats.getCurrentLevelInstance() != null && levelStats.getCurrentLevelInstance().equals(finalInstanceId) &&
							   instance.getRight().getOwner() != null && instance.getRight().getOwner().equals(player))
						LevelManager.Instance.reloadInstance(finalInstanceId, privat);
					else
						LevelManager.Instance.joinInstance(player, finalInstanceId);
				}
				else if((instance = LevelManager.Instance.instantiateLevel(level, privat)) != null)
					LevelManager.Instance.joinInstance(player, instance.getLeft());
				else
					player.sendMessage(Text.translatable("message.ultracraft.travel.error-instance"));
				onTravelFinished(player);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.REQUEST_DESTINATIONS_PACKET_ID, (server, player, handler, buf, sender) -> {
			server.execute(() -> {
				IUltraLevelComponent global = UltraComponents.GLOBAL.get(player.getWorld().getLevelProperties());
				List<Identifier> ids = global.getUnlockedDestinationList();
				PacketByteBuf cbuf = new PacketByteBuf(Unpooled.buffer());
				cbuf.writeInt(ids.size());
				ids.forEach(cbuf::writeIdentifier);
				ServerPlayNetworking.send(player, SEND_DESTINATIONS_PACKET_ID, cbuf);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.REQUEST_INSTANCES_PACKET_ID, (server, player, handler, buf, sender) -> {
			Identifier levelId = buf.readIdentifier();
			server.execute(() -> {
				NbtCompound nbt = LevelManager.Instance.serializePool(levelId);
				if(nbt.getKeys().isEmpty())
					return;
				PacketByteBuf cbuf = new PacketByteBuf(Unpooled.buffer());
				cbuf.writeNbt(nbt);
				ServerPlayNetworking.send(player, SEND_LEVEL_INSTANCES_PACKET_ID, cbuf);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.REQUEST_FULL_CYBERGRIND_PACKET_ID, (server, player, handler, buf, sender) -> {
			server.execute(() -> CybergrindManager.Instance.syncFullActiveGame(player));
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.SWITCH_SLOT_PACKET_ID, (server, player, handler, buf, sender) -> {
			int lastSlot = buf.readByte();
			int newSlot = buf.readByte();
			server.execute(() -> UltraComponents.WINGED.get(player).onUpdateActiveSlot(lastSlot, newSlot));
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.SUBMIT_BEST_RANK_PACKET_ID, (server, player, handler, buf, sender) -> {
			Identifier levelId = buf.readIdentifier();
			byte rank = buf.readByte();
			server.execute(() -> UltraComponents.LEVEL_STATS.get(player).setBestRank(levelId, rank));
		});
		ServerPlayNetworking.registerGlobalReceiver(PacketRegistry.SUBMIT_BEST_TIME_PACKET_ID, (server, player, handler, buf, sender) -> {
			Identifier levelId = buf.readIdentifier();
			long time = buf.readLong();
			boolean perfect = buf.readBoolean();
			server.execute(() -> UltraComponents.LEVEL_STATS.get(player).setBestTime(levelId, perfect, time));
		});
	}
	
	static IParriable getNearestParriable(Set<Entity> parriables, Vec3d to)
	{
		double nearestDistance = 100.0;
		IParriable nearest = null;
		
		for (Entity e : parriables)
		{
			if(!(e instanceof IParriable parriable))
				continue;
			double distance = e.squaredDistanceTo(to);
			if(distance < nearestDistance)
			{
				nearest = parriable;
				nearestDistance = distance;
			}
		}
		return nearest;
	}
	
	static void addDebugParticle(ServerPlayerEntity p, Vec3d pos, boolean alt)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVector3f(pos.toVector3f());
		buf.writeBoolean(alt);
		ServerPlayNetworking.send(p, DEBUG_PACKET_ID, buf);
	}
	
	static void onTravelFinished(ServerPlayerEntity player)
	{
		ServerPlayNetworking.send(player, FINISH_TRAVELLING_PACKET_ID, new PacketByteBuf(Unpooled.buffer()));
		if(player instanceof WingedPlayerEntity winged)
			winged.setSlamming(false);
	}
	
	static HashSet<Entity> fetchParryCandidates(ServerPlayerEntity player, Vec3d pos, Vec3d forward, float dist, Vector3f clientVel,
													   boolean chainingAllowed, int debug, EntityType<? extends Entity> entityType, Predicate<Entity> predicate)
	{
		if(predicate == null)
			predicate = e -> {
				if(!(entityType == null || e.getType().equals(entityType)))
					return false;
				return (!(e instanceof IParriable) || !(((IParriable)e).isParried()) || chainingAllowed);
			}; //standard predicate
		HashSet<Entity> output = new HashSet<>();
		Box check = new Box(pos.x - 0.3f, pos.y - 0.3f, pos.z - 0.3f,
				pos.x + 0.3f, pos.y + 0.3f, pos.z + 0.3f)
							.stretch(forward.multiply(dist)).offset(new Vec3d(clientVel.mul(0.25f)))
							.stretch(clientVel.x * 16, clientVel.y * 16, clientVel.z * 16).stretch(0, -1, 0);
		//Get Pariables that absolutely are in the Parry Check
		List<Entity> candidates = player.getWorld().getEntitiesByClass(Entity.class, check, predicate);
		//Get Pariables that could move into the Parry Check
		List<Entity> potentialCandidates = player.getWorld().getEntitiesByClass(Entity.class, player.getBoundingBox().expand(4),
				e -> {
					if(!(entityType == null || e.getType().equals(entityType)) || candidates.contains(e))
						return false;
					return (!(e instanceof IParriable) || !(((IParriable)e).isParried()) || chainingAllowed);
				});
		//Get Drones
		if(entityType == null || !entityType.equals(EntityRegistry.THROWN_COIN))
		{
			List<DroneEntity> drones = player.getWorld().getEntitiesByClass(DroneEntity.class, check, DroneEntity::isParriable);
			candidates.addAll(drones);
		}
		
		for (Entity proj : potentialCandidates)
		{
			Vec3d vel = proj.getVelocity();
			if (check.intersects(proj.getBoundingBox().expand(vel.x, vel.y, vel.z)))
			{
				candidates.add(proj);
				break;
			}
		}
		Vec3d eyePos = player.getEyePos();
		for (Entity candidate : candidates)
		{
			if(candidate.getPos().subtract(eyePos).length() < dist && !(candidate instanceof PersistentProjectileEntity persistent && persistent.inGround))
				output.add(candidate);
		}
		if(debug > 0)
		{
			addDebugParticle(player, new Vec3d(check.minX, check.minY, check.minZ), debug == 2);
			addDebugParticle(player, new Vec3d(check.maxX, check.minY, check.minZ), debug == 2);
			addDebugParticle(player, new Vec3d(check.minX, check.minY, check.maxZ), debug == 2);
			addDebugParticle(player, new Vec3d(check.maxX, check.minY, check.maxZ), debug == 2);
			addDebugParticle(player, new Vec3d(check.minX, check.maxY, check.minZ), debug == 2);
			addDebugParticle(player, new Vec3d(check.maxX, check.maxY, check.minZ), debug == 2);
			addDebugParticle(player, new Vec3d(check.minX, check.maxY, check.maxZ), debug == 2);
			addDebugParticle(player, new Vec3d(check.maxX, check.maxY, check.maxZ), debug == 2);
		}
		return output;
	}
}
