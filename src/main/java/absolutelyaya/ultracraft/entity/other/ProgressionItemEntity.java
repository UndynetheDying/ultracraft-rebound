package absolutelyaya.ultracraft.entity.other;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILoadoutComponent;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProgressionItemEntity extends ItemEntity
{
	protected static final TrackedData<String> PROGRESSION_ENTRY = DataTracker.registerData(ProgressionItemEntity.class, TrackedDataHandlerRegistry.STRING);
	protected static final TrackedData<Boolean> PICKUP = DataTracker.registerData(ProgressionItemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	
	protected List<UUID> playerList = new ArrayList<>();
	
	public ProgressionItemEntity(EntityType<? extends ItemEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	public static ProgressionItemEntity spawn(World world, Vec3d pos, String entry, ItemStack stack, Random random)
	{
		ProgressionItemEntity item = new ProgressionItemEntity(EntityRegistry.PROGRESSION_ITEM, world);
		item.setStack(stack);
		item.setPosition(pos);
		item.dataTracker.set(PROGRESSION_ENTRY, entry);
		item.setVelocity(Vec3d.ZERO.addRandom(random, 0.5f));
		world.spawnEntity(item);
		return item;
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		dataTracker.startTracking(PROGRESSION_ENTRY, "");
		dataTracker.startTracking(PICKUP, true);
	}
	
	@Override
	public void onPlayerCollision(PlayerEntity player)
	{
		if(getWorld().isClient)
			return;
		if(player instanceof ServerPlayerEntity serverPlayer)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeInt(getId());
			ServerPlayNetworking.send(serverPlayer, PacketRegistry.PICKUP_PROGRESSION_ITEM_ID, buf);
		}
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(player);
		ILoadoutComponent loadout = UltraComponents.LOADOUT.get(player);
		if(playerList.contains(player.getUuid()) || isAlreadyHeld(loadout) || (dataTracker.get(PICKUP) && !player.getInventory().insertStack(getStack().copy())))
			return;
		int count = getStack().getCount();
		progression.obtain(Identifier.tryParse(getProgressionEntry()));
		progression.sync();
		player.increaseStat(Stats.PICKED_UP.getOrCreateStat(getStack().getItem()), count);
		player.triggerItemPickedUpByEntityCriteria(this);
		playerList.add(player.getUuid());
	}
	
	boolean isAlreadyHeld(ILoadoutComponent loadout)
	{
		return getStack().getItem() instanceof AbstractWeaponItem weapon && loadout.isWeaponTypeHeld(weapon.getWeaponType());
	}
	
	public String getProgressionEntry()
	{
		return dataTracker.get(PROGRESSION_ENTRY);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("progressionEntry", NbtElement.STRING_TYPE))
			dataTracker.set(PROGRESSION_ENTRY, nbt.getString("progressionEntry"));
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt)
	{
		nbt.putString("progressionEntry", getProgressionEntry());
		return super.writeNbt(nbt);
	}
	
	public void setNoPickup()
	{
		dataTracker.set(PICKUP, false);
	}
}
