package absolutelyaya.ultracraft.entity.other;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.projectile.IIgnoreSharpshooter;
import absolutelyaya.ultracraft.item.StainedGlassWindowItem;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class StainedGlassWindow extends AbstractDecorationEntity implements IIgnoreSharpshooter
{
	protected static final TrackedData<Boolean> REINFORCED = DataTracker.registerData(StainedGlassWindow.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<Boolean> NO_DROP = DataTracker.registerData(StainedGlassWindow.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<Byte> VARIANT = DataTracker.registerData(StainedGlassWindow.class, TrackedDataHandlerRegistry.BYTE);
	
	public StainedGlassWindow(EntityType<? extends AbstractDecorationEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	private StainedGlassWindow(World world, BlockPos pos, Variant variant)
	{
		super(EntityRegistry.STAINED_GLASS_WINDOW, world, pos);
		setVariant(variant);
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		dataTracker.startTracking(REINFORCED, false);
		dataTracker.startTracking(NO_DROP, false);
		dataTracker.startTracking(VARIANT, (byte)Variant.DOVE.ordinal());
	}
	
	@Override
	public void onTrackedDataSet(TrackedData<?> data)
	{
		super.onTrackedDataSet(data);
		if (VARIANT.equals(data))
			this.updateAttachmentPosition();
	}
	
	public static StainedGlassWindow place(World world, BlockPos pos, Direction facing, Variant variant)
	{
		StainedGlassWindow window = new StainedGlassWindow(world, pos, variant);
		window.setFacing(facing);
		window.setVariant(variant);
		if(!window.canStayAttached())
			return null;
		return window;
	}
	
	@Override
	public boolean canStayAttached()
	{
		if(age > 5 && isReinforced())
			return true;
		return super.canStayAttached();
	}
	
	Variant getVariant()
	{
		return Variant.values()[dataTracker.get(VARIANT) % Variant.values().length];
	}
	
	public void setVariant(Variant variant)
	{
		dataTracker.set(VARIANT, (byte)variant.ordinal());
	}
	
	public Identifier getTexture()
	{
		return getVariant().texture;
	}
	
	@Override
	public int getWidthPixels()
	{
		return getVariant().width;
	}
	
	@Override
	public int getHeightPixels()
	{
		return getVariant().height;
	}
	
	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if(dataTracker.get(REINFORCED) && !source.isOf(DamageTypes.PLAYER_ATTACK) || (source.getAttacker() instanceof PlayerEntity player && !player.canModifyBlocks()))
			return false;
		return super.damage(source, amount);
	}
	
	@Override
	public void onBreak(@Nullable Entity entity)
	{
		playSound(SoundRegistry.STAINED_GLASS_WINDOW_BREAK, 1.0f, 1.0f);
		if((entity instanceof PlayerEntity player && player.isCreative()) || !getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS) ||
				   dataTracker.get(NO_DROP))
			return;
		dropStack(StainedGlassWindowItem.getStack(dataTracker.get(REINFORCED), getVariant()));
	}
	
	@Override
	public void onPlace()
	{
		playSound(SoundRegistry.STAINED_GLASS_WINDOW_PLACE, 1.0f, 1.0f);
	}
	
	@Nullable
	@Override
	public ItemStack getPickBlockStack()
	{
		return StainedGlassWindowItem.getStack(dataTracker.get(REINFORCED), getVariant());
	}
	
	@Override
	public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch)
	{
		setPosition(x, y, z);
	}
	
	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate)
	{
		setPosition(x, y, z);
	}
	
	@Override
	public Vec3d getSyncedPos()
	{
		return Vec3d.of(attachmentPos);
	}
	
	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket()
	{
		return new EntitySpawnS2CPacket(this, facing.getId(), getDecorationBlockPos());
	}
	
	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);
		setFacing(Direction.byId(packet.getEntityData()));
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("reinforced", NbtElement.BYTE_TYPE))
			dataTracker.set(REINFORCED, nbt.getBoolean("reinforced"));
		if(nbt.contains("noDrop", NbtElement.BYTE_TYPE))
			dataTracker.set(NO_DROP, nbt.getBoolean("noDrop"));
		if(nbt.contains("facing", NbtElement.BYTE_TYPE))
			setFacing(Direction.fromHorizontal(nbt.getByte("facing")));
		if(nbt.contains("variant", NbtElement.BYTE_TYPE))
			setVariant(Variant.values()[nbt.getByte("variant") % Variant.values().length]);
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound nbt)
	{
		nbt.putBoolean("reinforced", dataTracker.get(REINFORCED));
		nbt.putBoolean("noDrop", dataTracker.get(NO_DROP));
		nbt.putByte("facing", (byte)facing.getHorizontal());
		nbt.putByte("variant", (byte)getVariant().ordinal());
		return super.writeNbt(nbt);
	}
	
	public void setReinforced(boolean b)
	{
		dataTracker.set(REINFORCED, b);
	}
	
	public boolean isReinforced()
	{
		return dataTracker.get(REINFORCED);
	}
	
	public enum Variant
	{
		DOVE(32, 48, Ultracraft.texIdentifier("textures/entity/stained_glass/bird")),
		GABRIEL(48, 96,Ultracraft.texIdentifier("textures/entity/stained_glass/gabriel")),
		SAINT(16, 32,Ultracraft.texIdentifier("textures/entity/stained_glass/saint"));
		public final int width, height;
		public final Identifier texture;
		
		Variant(int width, int height, Identifier texture)
		{
			this.width = width;
			this.height = height;
			this.texture = texture;
		}
	}
}
