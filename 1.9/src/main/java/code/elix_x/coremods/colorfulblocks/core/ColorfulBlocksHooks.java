package code.elix_x.coremods.colorfulblocks.core;

import code.elix_x.coremods.colorfulblocks.color.ColoredBlocksManager;
import net.minecraft.tileentity.TileEntity;

public class ColorfulBlocksHooks {

	public static int getTintIndex(int original){
		return original == -1 ? Integer.MAX_VALUE : original;
	}

	public static void recolorTileEntity(TileEntity tileentity){
		ColoredBlocksManager.recolorTileEntity(tileentity);
	}

}
