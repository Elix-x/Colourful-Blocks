package code.elix_x.coremods.colourfulblocks.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import code.elix_x.coremods.colourfulblocks.gui.GuiBrush;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import cpw.mods.fml.common.network.IGuiHandler;

public class ColourfulBlocksGuiHandler implements IGuiHandler{

	public static final int guiIdBrush = 0;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == guiIdBrush){
			if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBrush){
				
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == guiIdBrush){
			if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBrush){
				return new GuiBrush(ItemBrush.getCurrentRGBA(player.getCurrentEquippedItem()));
			}
		}
		return null;
	}

}
