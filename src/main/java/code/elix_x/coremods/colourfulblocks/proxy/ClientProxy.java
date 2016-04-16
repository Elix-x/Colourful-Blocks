package code.elix_x.coremods.colourfulblocks.proxy;

import code.elix_x.coremods.colourfulblocks.color.tool.IColoringTool;
import code.elix_x.coremods.colourfulblocks.gui.GuiSelectColor;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class ClientProxy implements IColorfulBlocksProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event){ 

	}

	@Override
	public void init(FMLInitializationEvent event){ 

	}

	@Override
	public void postInit(FMLPostInitializationEvent event){ 

	}

	@Override
	public void displayGuiSelectColor(ItemStack coloringTool){
		Minecraft.getMinecraft().displayGuiScreen(new GuiSelectColor(((IColoringTool) coloringTool.getItem()).getCurrentColor(coloringTool)));
	}

}
