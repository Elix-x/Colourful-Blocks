package code.elix_x.coremods.colorfulblocks.proxy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

import code.elix_x.coremods.colorfulblocks.ColorfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.api.tools.ColoringToolProvider;
import code.elix_x.coremods.colorfulblocks.api.tools.IColoringTool;
import code.elix_x.coremods.colorfulblocks.client.color.ColorfulBlocksBlockColor;
import code.elix_x.coremods.colorfulblocks.client.color.StandartColoringToolItemColor;
import code.elix_x.coremods.colorfulblocks.client.events.LastRenderWorldEvent;
import code.elix_x.coremods.colorfulblocks.client.gui.GuiSelectColor;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.coremods.colorfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.excore.utils.reflection.AdvancedReflectionHelper.AField;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IColorfulBlocksProxy {

	public static final Pattern langFilePattern = Pattern.compile("lang/[a-zA-Z][a-zA-Z]_[a-zA-Z][a-zA-Z].lang");

	@Override
	public void preInit(FMLPreInitializationEvent event){

	}

	@Override
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new LastRenderWorldEvent());

		for(ColoringToolProvider provider : ColoringToolsManager.getProviders()){
			ModelResourceLocation def = provider.getDefaultModel();
			if(def != null){
				for(Item item : (Collection<Item>) ColoringToolsManager.getAllItems(provider)){
					ModelLoader.setCustomModelResourceLocation(item, 0, def);
					Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, def);
					Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new StandartColoringToolItemColor(0, 1), item);
				}
			}
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event){
		Map<net.minecraftforge.fml.common.registry.RegistryDelegate<Block>, IBlockColor> blockColorMap = new AField<BlockColors, Map<net.minecraftforge.fml.common.registry.RegistryDelegate<Block>, IBlockColor>>(BlockColors.class, "blockColorMap").setAccessible(true).get(Minecraft.getMinecraft().getBlockColors());
		for(Block block : Block.REGISTRY){
			blockColorMap.put(block.delegate, new ColorfulBlocksBlockColor(blockColorMap.get(block.delegate)));
		}
	}

	@Override
	public void loadLocalisations(){
		ColoringMaterialsManager.logger.info("Loading localisations");
		List<IResourcePack> resourcePacks = new AField<Minecraft, List<IResourcePack>>(Minecraft.class, "defaultResourcePacks", "field_110449_ao").setAccessible(true).get(Minecraft.getMinecraft());
		for(final File extensionDir : ColoringMaterialsManager.extensionsDir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File file){
				return file.isDirectory();
			}

		})){
			ColoringMaterialsManager.logger.info("Loading localisations from extension " + extensionDir.getName());
			resourcePacks.add(new IResourcePack(){

				@Override
				public boolean resourceExists(ResourceLocation location){
					return langFilePattern.matcher(location.getResourcePath()).matches() && new File(extensionDir, location.getResourcePath()).exists();
				}

				@Override
				public InputStream getInputStream(ResourceLocation location) throws IOException {
					return new FileInputStream(new File(extensionDir, location.getResourcePath()));
				}

				@Override
				public Set<String> getResourceDomains(){
					return Sets.newHashSet(ColorfulBlocksBase.MODID);
				}

				@Override
				public String getPackName(){
					return "CoBl";
				}

				@Override
				public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
					return null;
				}

				@Override
				public BufferedImage getPackImage() throws IOException {
					return null;
				}

			});
		}
	}

	@Override
	public void displayGuiSelectColor(ItemStack coloringTool){
		Minecraft.getMinecraft().displayGuiScreen(new GuiSelectColor(((IColoringTool) coloringTool.getItem()).getCurrentColor(coloringTool)));
	}

}
