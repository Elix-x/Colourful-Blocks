package code.elix_x.coremods.colourfulblocks.proxy;

import code.elix_x.coremods.colourfulblocks.client.events.LastRenderWorldEvent;
import code.elix_x.coremods.colourfulblocks.client.gui.GuiSelectColor;
import code.elix_x.coremods.colourfulblocks.color.tool.IColoringTool;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IColorfulBlocksProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event){

	}

	@Override
	public void init(FMLInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(new LastRenderWorldEvent());
	}

	@Override
	public void postInit(FMLPostInitializationEvent event){

	}

	@Override
	public void displayGuiSelectColor(ItemStack coloringTool){
		Minecraft.getMinecraft().displayGuiScreen(new GuiSelectColor(((IColoringTool) coloringTool.getItem()).getCurrentColor(coloringTool)));
	}

}
