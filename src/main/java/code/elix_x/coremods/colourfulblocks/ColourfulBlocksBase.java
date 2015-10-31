package code.elix_x.coremods.colourfulblocks;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.stream.MalformedJsonException;

import code.elix_x.coremods.colourfulblocks.color.ColourfulBlocksManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolProvider;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.coremods.colourfulblocks.events.MainipulatePaintEvent;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import code.elix_x.coremods.colourfulblocks.net.ColorChangeMessage;
import code.elix_x.coremods.colourfulblocks.net.ColorfulBlocksSyncMessage;
import code.elix_x.coremods.colourfulblocks.net.ColorfulBlocksSyncMessage;
import code.elix_x.coremods.colourfulblocks.net.ColourfulBlocksGuiHandler;
import code.elix_x.coremods.colourfulblocks.proxy.CommonProxy;
import code.elix_x.excore.EXCore;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;

@Mod(modid = ColourfulBlocksBase.MODID, name = ColourfulBlocksBase.NAME, version = ColourfulBlocksBase.VERSION, dependencies = "required-after:" + EXCore.DEPENDENCY)
public class ColourfulBlocksBase {

	public static final String MODID = "colourfullblocks";
	public static final String NAME = "Colourful Blocks";
	public static final String VERSION = "1.1.3";

	@Mod.Instance(MODID)
	public static ColourfulBlocksBase instance;

	@SidedProxy(clientSide = "code.elix_x.coremods.colourfulblocks.proxy.ClientProxy", serverSide = "code.elix_x.coremods.colourfulblocks.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static final Logger logger = LogManager.getLogger(NAME + " Base");

	public static SimpleNetworkWrapper net;

	public static File configFolder;
	public static File mainConfigFile;
	public static Configuration mainConfig;

	public static boolean consumeWaterOnErase;
	public static boolean consumeWaterOnPaint;

	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{ 
		net = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		net.registerMessage(ColorfulBlocksSyncMessage.ColorfulBlocksSyncMessageHandler.class, ColorfulBlocksSyncMessage.class, 0, Side.CLIENT);
		net.registerMessage(ColorChangeMessage.ColorChangeMessageHandler.class, ColorChangeMessage.class, 1, Side.SERVER);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ColourfulBlocksGuiHandler());
		configFolder = new File(event.getModConfigurationDirectory(), MODID);
		configFolder.mkdirs();

		mainConfigFile = new File(configFolder, "main.cfg");
		try {
			mainConfigFile.createNewFile();
		} catch (IOException e) {
			logger.error("Caught exception while creating main config file: ", e);
		}
		mainConfig = new Configuration(mainConfigFile);
		mainConfig.load();
		
		consumeWaterOnErase = mainConfig.getBoolean("consumeWaterOnErase", "consomation", true, "Consume water from bottle when erasing paint");
		consumeWaterOnPaint = mainConfig.getBoolean("consumeWaterOnPaint", "consomation", false, "Consume water from bucket when mixing new paint");

		mainConfig.save();

		ColoringToolsManager.registerProvider(new ColoringToolProvider<ItemBrush>() {

			@Override
			public String getConfigOptionName() {
				return "brushes";
			}

			@Override
			public String getRecipeType() {
				return ColoringMaterialsManager.RECIPETYPEBRUSH;
			}

			@Override
			public ItemBrush provide(ColoringToolMaterial material) {
				return new ItemBrush(material);
			}
			
		});
		
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) throws MalformedJsonException
	{
		mainConfig.load();
		ColoringMaterialsManager.init();
		ColoringToolsManager.init();
		mainConfig.save();

		MinecraftForge.EVENT_BUS.register(new ColourfulBlocksManager.Events());
		MinecraftForge.EVENT_BUS.register(new MainipulatePaintEvent());

		proxy.init(event);
	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{ 
		proxy.postInit(event);
	}

}
