package absolutelyaya.ultracraft.components;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.entity.ILivingComponent;
import absolutelyaya.ultracraft.components.entity.LivingComponent;
import absolutelyaya.ultracraft.components.level.IUltraLevelComponent;
import absolutelyaya.ultracraft.components.level.UltraLevelComponent;
import absolutelyaya.ultracraft.components.player.*;
import absolutelyaya.ultracraft.components.world.DimensionDataComponent;
import absolutelyaya.ultracraft.components.world.IDimensionDataComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public final class UltraComponents implements EntityComponentInitializer, LevelComponentInitializer, WorldComponentInitializer
{
	public static final ComponentKey<IWingDataComponent> WING_DATA =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "wing_data"), IWingDataComponent.class);
	public static final ComponentKey<IWingedPlayerComponent> WINGED =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "winged"), IWingedPlayerComponent.class);
	public static final ComponentKey<IProgressionComponent> PROGRESSION =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "progression"), IProgressionComponent.class);
	public static final ComponentKey<IEasterComponent> EASTER =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "easter"), IEasterComponent.class);
	public static final ComponentKey<IArmComponent> ARMS =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "arms"), IArmComponent.class);
	public static final ComponentKey<IStyleComponent> STYLE =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "style"), IStyleComponent.class);
	public static final ComponentKey<IHivelComponent> HIVEL =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "hivel"), IHivelComponent.class);
	public static final ComponentKey<ILoadoutComponent> LOADOUT =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "loadout"), ILoadoutComponent.class);
	public static final ComponentKey<IEditorComponent> EDITOR =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "editor"), IEditorComponent.class);
	
	public static final ComponentKey<ILivingComponent> LIVING =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "living"), ILivingComponent.class);
	
	public static final ComponentKey<IUltraLevelComponent> GLOBAL =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "global_data"), IUltraLevelComponent.class);
	
	public static final ComponentKey<IDimensionDataComponent> DIMENSION_DATA =
			ComponentRegistry.getOrCreate(new Identifier(Ultracraft.MOD_ID, "dimension_data"), IDimensionDataComponent.class);
	
	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry)
	{
		registry.registerForPlayers(WING_DATA, WingDataComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(WINGED, WingedPlayerComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(PROGRESSION, ProgressionComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(EASTER, EasterComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(ARMS, ArmComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(STYLE, StyleComponent::new, RespawnCopyStrategy.NEVER_COPY);
		registry.registerForPlayers(HIVEL, HivelComponent::new, RespawnCopyStrategy.NEVER_COPY);
		registry.registerForPlayers(LOADOUT, LoadoutComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		registry.registerForPlayers(EDITOR, EditorComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
		
		registry.registerFor(LivingEntity.class, LIVING, LivingComponent::new);
	}
	
	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry)
	{
		registry.register(GLOBAL, UltraLevelComponent::new);
	}
	
	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry)
	{
		registry.register(DIMENSION_DATA, DimensionDataComponent::new);
	}
}
