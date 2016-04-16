package code.elix_x.coremods.colorfulblocks.proxy;

import code.elix_x.coremods.colorfulblocks.client.events.LastRenderWorldEvent;
import code.elix_x.coremods.colorfulblocks.color.tool.IColoringTool;
import code.elix_x.coremods.colorfulblocks.gui.GuiSelectColor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
