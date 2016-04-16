package code.elix_x.coremods.colorfulblocks.core;

import code.elix_x.coremods.colorfulblocks.color.ColoredBlocksManager;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class ColourfulBlocksHooks {

	public static int colorMultiplier(int original, IBlockAccess world, Block block, int x, int y, int z){
		return ColoredBlocksManager.getBlockColor(world, block, x, y, z, original);
	}

	public static void recolorTileEntity(TileEntity tileentity){
		ColoredBlocksManager.recolorTileEntity(tileentity);
	}

}
