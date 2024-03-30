package absolutelyaya.ultracraft.registry;

import absolutelyaya.goop.api.WaterHandling;
import absolutelyaya.goop.client.GoopClient;
import absolutelyaya.goop.particles.GoopDropParticleEffect;
import absolutelyaya.ultracraft.ExplosionHandler;
import absolutelyaya.ultracraft.client.gui.CybergrindHUD;
import absolutelyaya.ultracraft.client.gui.screen.*;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.ITrailEnjoyer;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.gui.TitleHUD;
import absolutelyaya.ultracraft.client.rendering.EditModeRenderer;
import absolutelyaya.ultracraft.client.rendering.UltraHudRenderer;
import absolutelyaya.ultracraft.compat.PlayerAnimator;
import absolutelyaya.ultracraft.components.level.IUltraLevelComponent;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.cybergrind.CybergrindData;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.data.UltraRecipeManager;
import absolutelyaya.ultracraft.data.LevelData;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import absolutelyaya.ultracraft.particle.ParryIndicatorParticleEffect;
import absolutelyaya.ultracraft.recipe.UltraRecipe;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

import java.util.*;

import static absolutelyaya.ultracraft.registry.PacketRegistry.*;

@SuppressWarnings("CodeBlock2Expr")
@Environment(EnvType.CLIENT)
public class ClientPacketRegistry
{
	public static void registerS2C()
	{
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.FREEZE_PACKET_ID, ((client, handler, buf, sender) -> {
			int ticks = buf.readInt();
			boolean freezePhysicsDisabled = buf.readBoolean();
			if(!UltracraftClient.getConfig().freezeVFX)
				return;
			if(!freezePhysicsDisabled)
				Ultracraft.freeze((ServerWorld)null, ticks);
			UltracraftClient.freezeVFX(ticks);
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.HITSCAN_PACKET_ID, ((client, handler, buf, sender) -> {
			UltracraftClient.HITSCAN_HANDLER.addEntry(
					new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
					new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
					buf.readByte());
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.DASH_S2C_PACKET_ID, (client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			PlayerEntity player = client.player.getWorld().getPlayerByUuid(buf.readUuid());
			if(player == null || player.equals(client.player))
				return;
			Vec3d dir = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			client.execute(() -> {
				Random rand = client.player.getRandom();
				UltraComponents.HIVEL.get(player).onDash();
				Vec3d pos;
				for (int i = 0; i < 5; i++)
				{
					Vec3d particleVel = new Vec3d(-dir.x, -dir.y, -dir.z).multiply(rand.nextDouble() * 0.33 + 0.1);
					pos = player.getPos().add((rand.nextDouble() - 0.5) * player.getWidth() * 2,
							rand.nextDouble() * player.getHeight() + 0.5, (rand.nextDouble() - 0.5) * player.getWidth() * 2).add(dir.multiply(0.25));
					client.player.getWorld().addParticle(ParticleRegistry.DASH, pos.x, pos.y, pos.z, particleVel.x, particleVel.y, particleVel.z);
				}
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.BLEED_PACKET_ID, (((client, handler, buf, responseSender) -> {
			if(client.player == null)
				return;
			float amount = buf.readFloat();
			Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			double halfheight = buf.readDouble();
			boolean shotgun = buf.readBoolean();
			boolean water = client.player.getWorld().getFluidState(new BlockPos((int)pos.x, (int)pos.y, (int)pos.z)).isIn(FluidTags.WATER);
			client.execute(() -> {
				Random rand = client.player.getRandom();
				for (int i = 0; i < Math.min(3 * amount, 32); i++)
				{
					if(!water)
						client.player.getWorld().addParticle(new GoopDropParticleEffect(
										new Vec3d(0.56, 0.09, 0.01), 0.6f + rand.nextFloat() * 0.4f * (amount / 10f), true,
										WaterHandling.REPLACE_WITH_CLOUD_PARTICLE), pos.x, pos.y + halfheight, pos.z,
								rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5);
					else
						client.player.getWorld().addParticle(new DustParticleEffect(
										GoopClient.getConfig().censorMature ? Vec3d.unpackRgb(GoopClient.getConfig().censorColor).toVector3f() : new Vector3f(0.56f, 0.09f, 0.01f),
										3.6f + rand.nextFloat() * 0.4f * (amount / 10f)),
								pos.x + (rand.nextDouble() - 0.5) * 0.5, pos.y + halfheight + (rand.nextDouble() - 0.5) * halfheight * 1.5, pos.z + (rand.nextDouble() - 0.5) * 0.5,
								(rand.nextDouble() - 0.5) * 0.05, (rand.nextDouble() - 0.5) * 0.05, (rand.nextDouble() - 0.5) * 0.05);
				}
				if(client.player.squaredDistanceTo(pos) < 10 && !water)
				{
					UltracraftClient.addBlood(amount / (shotgun ? 10f : 30f));
					IWingedPlayerComponent winged = UltraComponents.WINGED.get(client.player);
					if(!winged.isJustPlayedBloodhealNoise())
					{
						client.player.playSound(SoundRegistry.BLOOD_HEAL, SoundCategory.PLAYERS, 0.6f * Math.min(1f, amount * 2), 1.7f);
						winged.setJustPlayedBloodhealNoise();
					}
				}
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.SET_GUNCD_PACKET_ID, ((client, handler, buf, sender) -> {
			Item item = buf.readItemStack().getItem();
			int ticks = buf.readInt();
			int idx = buf.readInt();
			client.execute(() -> {
				if(client.player != null && item instanceof AbstractWeaponItem weapon)
					UltraComponents.WINGED.get(client.player).getGunCooldownManager().setCooldown(weapon, ticks, idx);
			});
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.CATCH_FISH_PACKET_ID, ((client, handler, buf, sender) -> {
			UltraHudRenderer.onCatchFish(buf.readItemStack());
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.SYNC_CONFIG_S2C_PACKET_ID, ((client, handler, buf, sender) -> {
			String configId = buf.readString();
			String ruleId = buf.readString();
			byte type = buf.readByte();
			switch(type)
			{
				default -> UltracraftClient.syncConfigEntry(configId, ruleId, buf.readInt());
				case 69 -> UltracraftClient.syncConfigEntry(configId, ruleId, buf.readInt());
				case NbtElement.FLOAT_TYPE -> UltracraftClient.syncConfigEntry(configId, ruleId, buf.readFloat());
				case NbtElement.BYTE_TYPE -> UltracraftClient.syncConfigEntry(configId, ruleId, buf.readBoolean());
			}
		}));
		ClientPlayNetworking.registerGlobalReceiver(FINISH_SYNC_CONFIG_S2C_PACKET_ID, ((client, handler, buf, sender) -> {
			String configId = buf.readString();
			UltracraftClient.finishSyncingConfig(configId);
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.ENTITY_TRAIL_PACKET_ID, ((client, handler, buf, sender) -> {
			Entity e = client.world.getEntityById(buf.readInt());
			boolean b = buf.readBoolean();
			int data = buf.readInt();
			if(e instanceof ITrailEnjoyer trailer)
			{
				client.execute(() -> {
					if(b)
						trailer.addEntityTrail(data);
					else
						UltracraftClient.TRAIL_RENDERER.removeTrail(trailer.getLastTrailID());
				});
			}
			else
				Ultracraft.LOGGER.warn("Received invalid Packet data: [entity_trail] -> Target entity isn't a TrailEnjoyer!" );
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.SLAM_S2C_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			PlayerEntity player = client.world.getPlayerByUuid(buf.readUuid());
			if(player == null)
				return;
			BlockPos pos = buf.readBlockPos();
			BlockState state = client.player.getWorld().getBlockState(pos);
			boolean strong = buf.readBoolean();
			client.execute(() -> {
				Random random = client.player.getRandom();
				for (int i = 0; i < 32; i++)
				{
					float x = (float)((random.nextDouble() * 6) - 3 + player.getX());
					float z = (float)((random.nextDouble() * 6) - 3 + player.getZ());
					client.player.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), true,
							x, pos.up().getY() + 0.1, z, 0f, 1f, 0f);
				}
				if(strong)
					client.player.getWorld().addParticle(ParticleTypes.EXPLOSION, true,
							player.getX(), pos.up().getY() - 0.2, player.getZ(), 0f, 0f, 0f);
			});
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.EXPLOSION_PACKET_ID, (((client, handler, buf, responseSender) -> {
			if(client.player == null)
				return;
			Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
			float radius = (float)buf.readDouble();
			client.execute(() -> {
				ExplosionHandler.explosionClient((ClientWorld)client.player.getWorld(),
						pos, radius);
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.PRIMARY_SHOT_S2C_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			PlayerEntity player = client.player.getWorld().getPlayerByUuid(buf.readUuid());
			if(player == null)
				return;
			Vec3d velocity = player.getVelocity();
			client.execute(() -> {
				if(player.getMainHandStack().getItem() instanceof AbstractWeaponItem gun)
					gun.onPrimaryFire(player.getWorld(), player, velocity);
			});
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.DEBUG_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			Vector3f pos = buf.readVector3f();
			boolean alt = buf.readBoolean();
			client.execute(() -> {
				client.player.getWorld().addParticle(alt ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, pos.x, pos.y, pos.z,
						0f, 0f, 0f);
			});
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.SKIM_S2C_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			Vec3d pos = new Vec3d(buf.readVector3f());
			client.execute(() -> client.player.getWorld().addParticle(ParticleRegistry.RIPPLE, pos.x, pos.y, pos.z, 0, 0, 0));
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.COIN_PUNCH_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			int score = buf.readInt();
			client.execute(() -> UltraHudRenderer.onPunchCoin(score));
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.WORLD_INFO_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			client.execute(() -> UltracraftClient.sendJoinInfo(client, true));
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.BLOCK_PLAYER_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			UUID target = buf.readUuid();
			client.execute(() -> {
				boolean b = UltracraftClient.getConfig().blockedPlayers.contains(target);
				if(!b)
				{
					UltracraftClient.getConfig().blockedPlayers.add(target);
					UltracraftClient.saveConfig();
				}
				client.player.sendMessage(Text.translatable("command.ultracraft.block.client-" + (b ? "fail" : "success")));
			});
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.UNBLOCK_PLAYER_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			UUID target = buf.readUuid();
			client.execute(() -> {
				boolean b = UltracraftClient.getConfig().blockedPlayers.remove(target);
				UltracraftClient.saveConfig();
				client.player.sendMessage(Text.translatable("command.ultracraft.unblock.client-" + (b ? "success" : "fail")));
			});
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.OPEN_SERVER_CONFIG_MENU_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			NbtCompound rules = buf.readNbt();
			client.execute(() -> {
				client.setScreen(new ServerConfigScreen(rules));
			});
		}));
		ClientPlayNetworking.registerGlobalReceiver(PacketRegistry.RICOCHET_WARNING_PACKET_ID, ((client, handler, buf, sender) -> {
			if(client.player == null)
				return;
			Vector3f source = buf.readVector3f();
			UUID target = buf.readUuid();
			client.execute(() -> {
				if(client.player.getUuid().equals(target))
					client.player.getWorld().addParticle(ParticleRegistry.RICOCHET_WARNING, source.x, source.y, source.z, 0, 0, 0);
				else
					client.player.getWorld().addParticle(new ParryIndicatorParticleEffect(false), source.x, source.y, source.z, 0, 0, 0);
				client.player.getWorld().playSound(source.x, source.y, source.z, SoundRegistry.COIN_WARNING, SoundCategory.PLAYERS, 0.75f, 1.65f, false);
			});
		}));
		ClientPlayNetworking.registerGlobalReceiver(REPLENISH_STAMINA_PACKET_ID, (client, handler, buf, sender) -> {
			int i = buf.readInt();
			client.execute(() -> {
				UltraComponents.HIVEL.get(client.player).replenishStamina(i);
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(ANIMATION_S2C_PACKET_ID, (client, handler, buf, sender) -> {
			UUID targetID = buf.readUuid();
			AbstractClientPlayerEntity target = (AbstractClientPlayerEntity)client.player.getWorld().getPlayerByUuid(targetID);
			if(target == null || target.equals(client.player))
				return;
			int animID = buf.readInt();
			int fade = buf.readInt();
			boolean firstperson = buf.readBoolean();
			client.execute(() -> {
				PlayerAnimator.playAnimation(target, animID, fade, firstperson, true);
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(SOAP_KILL_PACKET_ID, (((client, handler, buf, responseSender) -> {
			if(client.player == null)
				return;
			Vec3d pos = new Vec3d(buf.readVector3f());
			double width = buf.readDouble();
			double height = buf.readDouble();
			client.execute(() -> {
				Random rand = client.player.getRandom();
				for (int i = 0; i < 24; i++)
				{
					Vec3d pos1 = pos.add((rand.nextFloat() - 0.5f) * width, (rand.nextFloat() - 0.5f) * height, (rand.nextFloat() - 0.5f) * width);
					Vec3d vel = Vec3d.ZERO.addRandom(rand, 0.2f);
					client.player.getWorld().addParticle(ParticleRegistry.SOAP_BUBBLE, pos1.x, pos1.y, pos1.z, vel.x, vel.y, vel.z);
				}
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(ULTRA_RECIPE_PACKET_ID, (((client, handler, buf, responseSender) -> {
			List<Pair<Identifier, UltraRecipe>> list = buf.readList(UltraRecipe::deserialize);
			ImmutableMap.Builder<Identifier, UltraRecipe> builder = ImmutableMap.builder();
			for(Pair<Identifier, UltraRecipe> pair : list)
				builder.put(pair.getLeft(), pair.getRight());
			UltraRecipeManager.setRecipes(builder.build());
		})));
		ClientPlayNetworking.registerGlobalReceiver(HIVEL_WHITELIST_PACKET_ID, (((client, handler, buf, responseSender) -> {
			client.execute(UltraHudRenderer::onWhitelistHint);
		})));
		ClientPlayNetworking.registerGlobalReceiver(GRAFFITI_WHITELIST_PACKET_ID, (((client, handler, buf, responseSender) -> {
			boolean b = buf.readBoolean();
			client.execute(() -> {
				UltracraftClient.GRAFFITI_WHITELISTED = b;
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(HELL_OBSERVER_PACKET_ID, (((client, handler, buf, responseSender) -> {
			BlockPos pos = buf.readBlockPos();
			client.execute(() -> {
				client.setScreen(new HellObserverScreen(pos));
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(SCREENSHAKE_PACKET_ID, (((client, handler, buf, responseSender) -> {
			float strength = buf.readFloat();
			client.execute(() -> {
				if(client.player instanceof WingedPlayerEntity winged)
					winged.addScreenshake(strength);
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(STYLE_BONUS_PACKET_ID, (((client, handler, buf, responseSender) -> {
			String key = buf.readString();
			client.execute(() -> {
				UltraComponents.STYLE.get(client.player).clientStyleBonusGet(key);
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(TRAVEL_SCREEN_PACKET_ID, (((client, handler, buf, responseSender) -> {
			boolean forced = buf.readBoolean();
			boolean ranking = buf.readBoolean() && UltraComponents.LEVEL_STATS.get(client.player).getCurrentLevelInstance() != null;
			client.execute(() -> {
				client.setScreen(ranking ? new LevelRankingScreen() : new TravelScreen(false, forced));
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(EDIT_PING_PACKET_ID, (((client, handler, buf, responseSender) -> {
			List<BlockPos> rooms = new ArrayList<>();
			List<BlockPos> orphans = new ArrayList<>();
			int size = buf.readInt();
			for (int i = 0; i < size; i++)
				rooms.add(buf.readBlockPos());
			size = buf.readInt();
			for (int i = 0; i < size; i++)
				orphans.add(buf.readBlockPos());
			
			
			BlockPos focus = UltraComponents.EDITOR.get(client.player).getEditFocus("room");
			if(focus != null && !rooms.contains(focus))
				rooms.add(focus);
			client.execute(() -> {
				EditModeRenderer.Instance.newRoomBlocks = rooms;
				EditModeRenderer.Instance.newOrphans = orphans;
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(TITLE_PACKET_ID, (((client, handler, buf, responseSender) -> {
			boolean large = buf.readBoolean();
			Text text = buf.readText();
			float delay = buf.readFloat();
			client.execute(() -> {
				if(large)
					TitleHUD.Instance.setBigTitle(text, delay);
				else
				{
					TitleHUD.Instance.setBoxTitle(text, delay);
					client.player.playSound(SoundRegistry.RECEIVE_BOX_TITLE, 1f, 1f);
				}
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(SEND_DESTINATIONS_PACKET_ID, (((client, handler, buf, responseSender) -> {
			int length = buf.readInt();
			List<Identifier> ids = new ArrayList<>();
			for (int i = 0; i < length; i++)
				ids.add(buf.readIdentifier());
			client.execute(() -> {
				IUltraLevelComponent global = UltraComponents.GLOBAL.get(client.player.getWorld().getLevelProperties());
				global.setDestinations(ids);
				if(client.currentScreen instanceof TravelScreen travel)
					travel.initButtons();
				if(client.currentScreen instanceof LevelRankingScreen ranking)
					ranking.refreshNextLevelButton();
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(SEND_LEVELS_PACKET_ID, (((client, handler, buf, responseSender) -> {
			for (int i = 0; i <= 1; i++) //execute twice; once to get levels and again to get custom levels
			{
				List<LevelData> list = buf.readList(LevelData::deserialize);
				ImmutableMap.Builder<Identifier, LevelData> builder = ImmutableMap.builder();
				for(LevelData level : list)
					builder.put(level.getID(), level);
				LevelDataManager.setLevels(builder.build(), i == 0);
			}
		})));
		ClientPlayNetworking.registerGlobalReceiver(SEND_LEVEL_INSTANCES_PACKET_ID, (((client, handler, buf, responseSender) -> {
			NbtCompound nbt = buf.readNbt();
			Map<String, UUID> map = new HashMap<>();
			for (String id : nbt.getKeys())
			{
				NbtCompound instance = nbt.getCompound(id);
				UUID owner = instance.getUuid("owner");
				map.put(id, owner);
			}
			client.execute(() -> {
				if(client.currentScreen instanceof AbstractTravelScreen travel)
					travel.setInstanceMap(map);
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(FINISH_TRAVELLING_PACKET_ID, (((client, handler, buf, responseSender) -> {
			if(client.currentScreen instanceof AbstractTravelScreen travel)
				travel.setShouldClose();
		})));
		ClientPlayNetworking.registerGlobalReceiver(ANNOUNCE_CYBERGRIND_ID, (((client, handler, buf, responseSender) -> {
			Text result = buf.readText();
			CybergrindHUD hud = CybergrindHUD.Instance;
			if(hud != null)
				hud.startAnnouncementSequence(result);
		})));
		ClientPlayNetworking.registerGlobalReceiver(SYNC_CYBERGRIND_ID, (((client, handler, buf, responseSender) -> {
			byte mode = buf.readByte();
			NbtCompound data = mode != CybergrindData.DESTROY_SYNC ? buf.readNbt() : new NbtCompound();
			client.execute(() -> {
				if(client.player == null)
					return;
				IWingedPlayerComponent winged = UltraComponents.WINGED.get(client.player);
				if(mode == CybergrindData.DESTROY_SYNC)
				{
					winged.setCybergrindData(null);
					return;
				}
				CybergrindData last = winged.getCybergrindData();
				if(last == null)
				{
					if(mode != CybergrindData.FULL_SYNC)
						ClientPlayNetworking.send(REQUEST_FULL_CYBERGRIND_PACKET_ID, new PacketByteBuf(Unpooled.buffer()));
					else
						winged.setCybergrindData(CybergrindData.fromNbt(data));
					return;
				}
				if(data.contains("currentWave", NbtElement.INT_TYPE))
					last.setCurrentWave(data.getInt("currentWave"));
				if(data.contains("enemies", NbtElement.INT_TYPE))
					last.setEnemies(data.getInt("enemies"));
			});
		})));
		ClientPlayNetworking.registerGlobalReceiver(PICKUP_PROGRESSION_ITEM_ID, (((client, handler, buf, responseSender) -> {
			int entityID = buf.readInt();
			client.execute(() -> {
				client.particleManager.addParticle(new ItemPickupParticle(client.getEntityRenderDispatcher(), client.getBufferBuilders(),
						client.world, client.world.getEntityById(entityID), client.player));
				client.world.removeEntity(entityID, Entity.RemovalReason.DISCARDED);
			});
		})));
	}
}
