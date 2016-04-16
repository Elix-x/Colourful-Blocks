package code.elix_x.coremods.colourfulblocks;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;

import code.elix_x.coremods.colourfulblocks.color.ColoredBlocksManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolProvider;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.coremods.colourfulblocks.events.MainipulatePaintEvent;
import code.elix_x.coremods.colourfulblocks.events.SyncColoredBlocksEvent;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import code.elix_x.coremods.colourfulblocks.net.ColorChangeMessage;
import code.elix_x.coremods.colourfulblocks.net.ColorfulBlocksSyncMessage;
import code.elix_x.coremods.colourfulblocks.proxy.IColorfulBlocksProxy;
import code.elix_x.excore.EXCore;
import code.elix_x.excore.utils.packets.SmartNetworkWrapper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = ColourfulBlocksBase.MODID, name = ColourfulBlocksBase.NAME, version = ColourfulBlocksBase.VERSION, dependencies = "required-after:" + EXCore.DEPENDENCY, acceptedMinecraftVersions = EXCore.MCVERSION)
public class ColourfulBlocksBase {

	public static final String MODID = "colourfullblocks";
	public static final String NAME = "Colourful Blocks";
	public static final String VERSION = "1.2.2";

	@Mod.Instance(MODID)
	public static ColourfulBlocksBase instance;

	@SidedProxy(clientSide = "code.elix_x.coremods.colourfulblocks.proxy.ClientProxy", serverSide = "code.elix_x.coremods.colourfulblocks.proxy.ServerProxy")
	public static IColorfulBlocksProxy proxy;

	public static final Logger logger = LogManager.getLogger(NAME);

	public static SmartNetworkWrapper net;

	public static File configFolder;
	public static File mainConfigFile;
	public static Configuration mainConfig;

	public static boolean multipyOriginalColor;

	public static boolean consumeWaterOnErase;
	public static boolean consumeWaterOnPaint;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){ 
		net = new SmartNetworkWrapper(NAME);
		net.registerMessage3(new Function<ColorfulBlocksSyncMessage, Runnable>(){

			@Override
			public Runnable apply(final ColorfulBlocksSyncMessage message){
				return new Runnable(){

					@Override
					public void run(){
						if(Minecraft.getMinecraft().theWorld.provider.dimensionId == message.dimId){
							ColoredBlocksManager.get(Minecraft.getMinecraft().theWorld).readFromNBT(message.nbt);
						}
					}

				};
			}

		}, ColorfulBlocksSyncMessage.class, Side.CLIENT);
		net.registerMessage1(new Function<Pair<ColorChangeMessage, MessageContext>, Runnable>(){

			@Override
			public Runnable apply(final Pair<ColorChangeMessage, MessageContext> pair){
				return new Runnable(){

					@Override
					public void run(){
						ColoringToolsManager.updateColor(pair.getRight().getServerHandler().playerEntity, pair.getLeft().rgba);
					}

				};
			}

		}, ColorChangeMessage.class, Side.SERVER);

		configFolder = new File(event.getModConfigurationDirectory(), NAME);
		if(!configFolder.exists()){
			File oldConfigFolder = new File(event.getModConfigurationDirectory(), MODID);
			if(oldConfigFolder.exists()){
				oldConfigFolder.renameTo(configFolder);
			}
		}
		configFolder.mkdirs();

		mainConfigFile = new File(configFolder, "main.cfg");
		try {
			mainConfigFile.createNewFile();
		} catch (IOException e) {
			logger.error("Caught exception while creating main config file: ", e);
		}
		mainConfig = new Configuration(mainConfigFile);
		mainConfig.load();

		multipyOriginalColor = mainConfig.getBoolean("multiplyOriginalColor", "world", true, "If block has custom color and is colored, result color is multiplication of it's color by paint color.\nIf false, result color is paint color.");

		consumeWaterOnErase = mainConfig.getBoolean("consumeWaterOnErase", "consomation", true, "Consume water from bottle when erasing paint");
		consumeWaterOnPaint = mainConfig.getBoolean("consumeWaterOnPaint", "consomation", false, "Consume water from bucket when mixing new paint");

		mainConfig.save();

		ColoringToolsManager.registerProvider(new ColoringToolProvider<ItemBrush>(){

			@Override
			public String getConfigOptionName(){
				return "brushes";
			}

			@Override
			public String getRecipeType(){
				return ColoringMaterialsManager.RECIPETYPEBRUSH;
			}

			@Override
			public ItemBrush provide(ColoringToolMaterial material){
				return new ItemBrush(material);
			}

		});

		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event){
		mainConfig.load();
		ColoringMaterialsManager.init();
		ColoringToolsManager.init();
		mainConfig.save();

		MinecraftForge.EVENT_BUS.register(new SyncColoredBlocksEvent());
		MinecraftForge.EVENT_BUS.register(new MainipulatePaintEvent());

		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){ 
		proxy.postInit(event);
	}

}
