package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.gui.screen.PedestalScreen;
import absolutelyaya.ultracraft.client.gui.screen.PedestalScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlerRegistry
{
	public static final ScreenHandlerType<PedestalScreenHandler> PEDESTAL = Registry.register(Registries.SCREEN_HANDLER,
			Ultracraft.identifier( "pedestal"), new ScreenHandlerType<>(PedestalScreenHandler::createPedestalHandler, FeatureSet.empty()));
	
	public static void registerClient()
	{
		HandledScreens.register(PEDESTAL, PedestalScreen::new);
	}
	
	public static void registerServer()
	{
	
	}
}
