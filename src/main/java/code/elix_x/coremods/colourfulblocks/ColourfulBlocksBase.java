package code.elix_x.coremods.colourfulblocks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.coremods.colourfulblocks.brush.BrushMaterial;
import code.elix_x.coremods.colourfulblocks.color.ColourfulBlocksManager;
import code.elix_x.coremods.colourfulblocks.exceptions.MalformedColorIdentifierException;
import code.elix_x.coremods.colourfulblocks.exceptions.MalformedItemStackIdentifierException;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import code.elix_x.coremods.colourfulblocks.net.ColourChangeMessage;
import code.elix_x.coremods.colourfulblocks.net.ColourfulBlocksGuiHandler;
import code.elix_x.coremods.colourfulblocks.net.ColourfulBlocksSyncMessage;
import code.elix_x.coremods.colourfulblocks.proxy.CommonProxy;
import code.elix_x.excore.utils.color.RGBA;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ColourfulBlocksBase.MODID, name = ColourfulBlocksBase.NAME, version = ColourfulBlocksBase.VERSION, dependencies = "required-after:excore")
public class ColourfulBlocksBase {

	public static final String MODID = "colourfullblocks";
	public static final String NAME = "Colourful Blocks";
	public static final String VERSION = "1.0.1";

	@Mod.Instance(MODID)
	public static ColourfulBlocksBase instance;

	@SidedProxy(clientSide = "code.elix_x.coremods.colourfulblocks.proxy.ClientProxy", serverSide = "code.elix_x.coremods.colourfulblocks.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static final Logger logger = LogManager.getLogger(NAME + " Base");

	public static SimpleNetworkWrapper net;

	public static File configFolder;
	public static File mainConfigFile;
	public static Configuration mainConfig;
	/*public static File recipeConfigFile;
	public static Configuration recipeConfig;
	public static File colorConfigFile;
	public static Configuration colorConfig;*/

	public static File brushesFile;

	public static boolean lenient;
	public static boolean crashIfMalformed;

	public static boolean consumeWaterOnErase;
	public static boolean consumeWaterOnPaint;

	/*public static boolean malformedRecipeCrash;
	public static boolean malformedColorCrash;*/

	/*public static Map<ToolMaterial, ItemStack> recipes = new HashMap<ToolMaterial, ItemStack>();
	public static Map<ToolMaterial, RGBA> colors = new HashMap<Item.ToolMaterial, RGBA>();*/

	//	public Item woodenBrush;

	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{ 
		net = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		net.registerMessage(ColourfulBlocksSyncMessage.ColourfulBlocksSyncMessageHandler.class, ColourfulBlocksSyncMessage.class, 0, Side.CLIENT);
		net.registerMessage(ColourChangeMessage.ColourChangeMessageHandler.class, ColourChangeMessage.class, 1, Side.SERVER);

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

		lenient = mainConfig.getBoolean("lenient", "Json", false, "Switch parser to lenient mode, for users who don't know json syntax");
		crashIfMalformed = mainConfig.getBoolean("crashIfMalformed", "Json", true, "Crash if caught exception while parsing json.\nIf false, game will not crash AND brushes will NOT be added!\nSet to false if you know what you are doing.");

		consumeWaterOnErase = mainConfig.getBoolean("consumeWaterOnErase", "consomation", true, "Consume water from bottle when erasing paint");
		consumeWaterOnPaint = mainConfig.getBoolean("consumeWaterOnPaint", "consomation", false, "Consume water from bottle when mixing new paint");

		mainConfig.save();

		/*recipeConfigFile = new File(configFolder, "recipes.cfg");
		try {
			recipeConfigFile.createNewFile();
		} catch (IOException e) {
			logger.error("Caught exception while creating recipe config file: ", e);
		}
		recipeConfig = new Configuration(recipeConfigFile);
		recipeConfig.load();
		recipeConfig.save();

		colorConfigFile = new File(configFolder, "colors.cfg");
		try {
			colorConfigFile.createNewFile();
		} catch (IOException e) {
			logger.error("Caught exception while creating color config file: ", e);
		}
		colorConfig = new Configuration(colorConfigFile);
		colorConfig.load();
		colorConfig.save();*/

		brushesFile = new File(configFolder, "brushes.json");

		proxy.preInit(event);
	}


	@EventHandler
	public void init(FMLInitializationEvent event) throws MalformedJsonException
	{
		File lang = new File(configFolder, "lang");
		lang.mkdirs();
		File en_US = new File(lang, "en_US.lang");
		if(!en_US.exists()){
			try {
				en_US.createNewFile();
			} catch (IOException e) {
				logger.info("Caught exception while creating main lang file: ", e);
			}
			try{
				String s = "";
				/*s += "guibrush.button.reset=Reset";
				s += "guibrush.button.done=Done";
				s += "\n";
				s += "\n";
				s += "\n";
				s += "\n";
				s += "\n";
				s += "\n";
				s += "\n";
				s += "\n";
				s += "\n";*/
				s += "item.blockbrush.name=Brush";
				s += "\n";
				s += "\n";
				s += "\n";
				s += "brushmaterial.WOOD=Wooden";
				s += "\n";
				s += "brushmaterial.STONE=Stone";
				s += "\n";
				s += "brushmaterial.IRON=Iron";
				s += "\n";
				s += "brushmaterial.GOLD=Golden";
				s += "\n";
				s += "brushmaterial.DIAMOND=Diamond";
				s += "\n";
				FileWriter writer = new FileWriter(en_US);
				writer.write(s);
				writer.close();
			} catch(Exception e){
				logger.error("Caught exception while writing default en_US file: ", e);
			}
		}

		for(File file : lang.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return !file.isDirectory() && file.getName().endsWith(".lang");
			}
		})){
			try{
				String language = file.getName().substring(0, 5);
				HashMap<String, String> map = new HashMap<String, String>();

				BufferedReader br = new BufferedReader(new FileReader(file)) ;

				for(String line; (line = br.readLine()) != null; ) {
					try{
						map.put(line.split("=")[0], line.split("=")[1]);
					} catch(IndexOutOfBoundsException e){

					}
				}

				br.close();

				LanguageRegistry.instance().injectLanguage(language, map);
			} catch(Exception e){
				logger.error("Caught exception while parsing lang file: ", e);
			}
		}


		if(!brushesFile.exists()){
			try {
				brushesFile.createNewFile();
			} catch (IOException e) {
				logger.error("Caught exception while creating brushes file: ", e);
			}

			try{

				JsonWriter writer = new JsonWriter(new FileWriter(brushesFile));

				writer.setIndent("	");

				writer.beginObject();

				writer.name("brushes");
				writer.beginArray();

				for(ToolMaterial material : ToolMaterial.values()){
					writer.beginObject();

					writer.name("name").value(material == ToolMaterial.EMERALD ? "DIAMOND" : material.name());
					writer.name("durability").value(material.getMaxUses() * 10);
					writer.name("buffer").value((material.getMaxUses() / 50) < 1 ? 1 : material.getMaxUses() / 50);
					writer.name("color").value((material == ToolMaterial.WOOD ? (110 + ":" + 65 + ":" + 43) : material == ToolMaterial.STONE ? (77 + ":" + 77 + ":" + 77) : material == ToolMaterial.IRON ? (153 + ":" + 153 + ":" + 153) : material == ToolMaterial.GOLD ? (186 + ":" + 154 + ":" + 9) : material == ToolMaterial.EMERALD ? (39 + ":" + 207 + ":" + 230) : "16777215"));
					if(material.getRepairItemStack() != null){
						writer.name("ingredient").value((material.getRepairItemStack().getItem() == Item.getItemFromBlock(Blocks.planks) ? "oredictionary:plankWood" : Item.itemRegistry.getNameForObject(material.getRepairItemStack().getItem())) + (material.getRepairItemStack().getItemDamage() == OreDictionary.WILDCARD_VALUE ? "" : "/" + material.getRepairItemStack().getItemDamage()));
					}
					writer.endObject();
				}

				writer.endArray();

				writer.endObject();


				writer.close();
			} catch(Exception e){
				logger.error("Caught exception while creating default file: ", e);
			}
		}

		try {
			JsonParser parser = new JsonParser();
			JsonReader reader = new JsonReader(new FileReader(brushesFile));
			reader.setLenient(lenient);
			JsonElement element = parser.parse(reader);
			JsonObject object = element.getAsJsonObject();
			JsonArray array = object.getAsJsonArray("brushes");
			Iterator<JsonElement> iterator = array.iterator();
			while(iterator.hasNext()){
				try{
					JsonObject o = iterator.next().getAsJsonObject();

					String name = o.get("name").getAsString();
					int durability = o.get("durability").getAsInt();
					int buffer = o.get("buffer").getAsInt();
					buffer = buffer < 0 ? 1 : buffer;

					int hex = 0;
					RGBA rgba = null;
					String color = o.get("color").getAsString();
					{
						String[] ss = color.split(":");
						if(ss.length !=1 && ss.length != 3 && ss.length != 4){
							throw new MalformedColorIdentifierException("Malformed color identifier: " + color + " must have 1 or 3 or 4 parts (hex or r:g:b or r:g:b:a)");
						}

						if(ss.length == 1){
							hex = Integer.parseInt(ss[0], 16);
						} else{
							rgba = new RGBA(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), ss.length == 4 ? Integer.parseInt(ss[3]) : 100);
						}
					}

					ItemBrush brush = null;
					if(rgba != null){
						brush = new ItemBrush(new BrushMaterial(name, durability, rgba, buffer));
					} else {
						brush = new ItemBrush(new BrushMaterial(name, durability, hex, buffer));
					}
					GameRegistry.registerItem(brush, "brush_" + name);

					if(o.has("ingredient")){
						Object itemstack = null;
						String recipe = o.get("ingredient").getAsString();
						if(!recipe.equals("")){
							String[] idMeta = recipe.split("//");
							if(idMeta.length > 2){
								throw new MalformedItemStackIdentifierException("Malformed item identifier: " + recipe + " must have 1 or 2 parts (oredict or id or id/meta)");
							}
							String[] modidID = idMeta[0].split(":");
							if(modidID.length < 2){
								throw new MalformedItemStackIdentifierException("Malformed item identifier: " + recipe + " must have modid part (modid:id or oredictionary:id)");
							}
							if(modidID.length > 2){
								throw new MalformedItemStackIdentifierException("Malformed item identifier: " + recipe + " must have only 2 parts (modid:id or oredictionary:id)");
							}

							if(modidID[0].equals("oredictionary")){
								itemstack = modidID[1];
							} else {
								if(idMeta.length == 2){
									itemstack = new ItemStack(GameRegistry.findItem(modidID[0], modidID[1]), 1, Integer.parseInt(idMeta[1]));
								} else {
									itemstack = GameRegistry.findItem(modidID[0], modidID[1]);
								}
							}

							if(itemstack != null){
								GameRegistry.addRecipe(new ShapedOreRecipe(brush, "  W", " M ", "S  ", 'W', Blocks.wool, 'M', itemstack, 'S', "stickWood"));
							}
						}
					}

				} catch(Exception e){
					logger.error("Caught exception while parsing brushes: ", e);
					if(crashIfMalformed){
						throw new MalformedJsonException("Brushes.json is malformed: ", e);
					}
				}
			}

		} catch (FileNotFoundException e) {
			logger.error("Caught exception while searching brushes: ", e);
		} catch (MalformedJsonException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Caught exception while parsing brushes: ", e);
			if(crashIfMalformed){
				throw new MalformedJsonException("Brushes.json is malformed: ", e);
			}
		}

		MinecraftForge.EVENT_BUS.register(new ColourfulBlocksManager.Events());
		MinecraftForge.EVENT_BUS.register(new ItemBrush.MainipulatePaintEvent());

		proxy.init(event);
	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{ 
		proxy.postInit(event);
	}

	@EventHandler
	public void onStarting(FMLServerStartingEvent event){
		ColourfulBlocksManager.onStarting(event);
	}

	@EventHandler
	public void onStopping(FMLServerStoppingEvent event){
		ColourfulBlocksManager.onStopping(event);
	}
	
	@EventHandler
	public void stopped(FMLServerStoppedEvent event){
		ColourfulBlocksManager.stopped(event);
	}

}
