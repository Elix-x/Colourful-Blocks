package code.elix_x.coremods.colourfulblocks.core;

import code.elix_x.coremods.colourfulblocks.color.ColoredBlocksManager;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class ColourfulBlocksHooks {

	public static int colorMultiplier(int original, IBlockAccess world, Block block, int x, int y, int z){
		return ColoredBlocksManager.getBlockColor(world, block, x, y, z, original);
	}

	public static void recolorTileEntity(TileEntity tileentity){
		ColoredBlocksManager.recolorTileEntity(tileentity);
	}

	public static void recolorBlock(RenderBlocks renderblocks, int x, int y, int z){
		ColoredBlocksManager.recolorBlock(renderblocks, x, y, z);
	}

}
