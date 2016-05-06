package code.elix_x.coremods.colorfulblocks;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;

import code.elix_x.coremods.colorfulblocks.api.ColorfulBlocksAPI;
import code.elix_x.coremods.colorfulblocks.api.materials.ColoringToolMaterial;
import code.elix_x.coremods.colorfulblocks.api.tools.ColoringToolProvider;
import code.elix_x.coremods.colorfulblocks.api.tools.IColoringTool;
import code.elix_x.coremods.colorfulblocks.api.tools.IColoringToolsManager;
import code.elix_x.coremods.colorfulblocks.api.world.IColoredBlocksManager;
import code.elix_x.coremods.colorfulblocks.color.ColoredBlocksManager;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.coremods.colorfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.coremods.colorfulblocks.events.MainipulatePaintEvent;
import code.elix_x.coremods.colorfulblocks.events.SyncColoredBlocksEvent;
import code.elix_x.coremods.colorfulblocks.items.ItemBrush;
import code.elix_x.coremods.colorfulblocks.net.ColorChangeMessage;
import code.elix_x.coremods.colorfulblocks.net.ColorfulBlocksSyncMessage;
import code.elix_x.coremods.colorfulblocks.proxy.IColorfulBlocksProxy;
import code.elix_x.excore.EXCore;
import code.elix_x.excore.utils.packets.SmartNetworkWrapper;
import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ColorfulBlocksBase.MODID, name = ColorfulBlocksBase.NAME, version = ColorfulBlocksBase.VERSION, dependencies = "required-after:" + EXCore.DEPENDENCY, acceptedMinecraftVersions = EXCore.MCVERSION)
public class ColorfulBlocksBase {

	public static final String MODID = "colorfulblocks";
	public static final String NAME = "Colorful Blocks";
	public static final String VERSION = "1.3.1";

	@Mod.Instance(MODID)
	public static ColorfulBlocksBase instance;

	@SidedProxy(clientSide = "code.elix_x.coremods.colorfulblocks.proxy.ClientProxy", serverSide = "code.elix_x.coremods.colorfulblocks.proxy.ServerProxy")
	public static IColorfulBlocksProxy proxy;

	public static final Logger logger = LogManager.getLogger(NAME);

	public static SmartNetworkWrapper net;

	public static File configFolder;
	public static File mainConfigFile;
	public static Configuration mainConfig;

	public static boolean multipyOriginalColor;

	public static boolean consumeWaterOnErase;
	public static boolean consumeWaterOnPaint;

	public static ColoringToolProvider<ItemBrush> brushesProvider;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		new AField<ColorfulBlocksAPI, ColorfulBlocksAPI>(ColorfulBlocksAPI.class, "INSTANCE").setFinal(false).set(null, new ColorfulBlocksAPI(){

			@Override
			public boolean consumeWaterOnPaint(){
				return ColorfulBlocksBase.consumeWaterOnPaint;
			}

			@Override
			public IColoringToolsManager getColoringToolsManager(){
				return new IColoringToolsManager(){

					@Override
					public void registerProvider(ColoringToolProvider provider){
						ColoringToolsManager.registerProvider(provider);
					}

					@Override
					public <I extends Item & IColoringTool> Collection<I> getAllItems(ColoringToolProvider<I> provider){
						return ColoringToolsManager.getAllItems(provider);
					}

				};
			}

			@Override
			public IColoredBlocksManager getColoredBlocksManager(World world){
				return ColoredBlocksManager.get(world);
			}

		});

		net = new SmartNetworkWrapper(NAME);
		net.registerMessage3(new Function<ColorfulBlocksSyncMessage, Runnable>(){

			@Override
			public Runnable apply(final ColorfulBlocksSyncMessage message){
				return new Runnable(){

					@Override
					public void run(){
						if(Minecraft.getMinecraft().theWorld.provider.getDimension() == message.dimId){
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

		ColoringToolsManager.registerProvider(brushesProvider = new ColoringToolProvider<ItemBrush>(){

			@Override
			public String getConfigOptionName(){
				return "brushes";
			}

			@Override
			public String getRecipeType(){
				return ColorfulBlocksAPI.RECIPETYPEBRUSH;
			}

			@Override
			public ItemBrush provide(ColoringToolMaterial material){
				return (ItemBrush) new ItemBrush(material).setRegistryName(MODID, "brush_" + material.name);
			}

			@Override
			public ModelResourceLocation getDefaultModel(){
				return new ModelResourceLocation(ColorfulBlocksBase.MODID + ":brush", "inventory");
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
