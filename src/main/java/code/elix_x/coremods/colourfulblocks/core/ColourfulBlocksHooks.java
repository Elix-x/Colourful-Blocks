package code.elix_x.coremods.colourfulblocks.core;

import code.elix_x.coremods.colourfulblocks.color.ColoredBlocksManager;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class ColourfulBlocksHooks {
	
	public static int getBlockColor(IBlockAccess world, int x, int y, int z){
		return ColoredBlocksManager.getBlockColor(world, x, y, z);
	}
	
	public static void recolorTileEntity(TileEntity tileentity){
		ColoredBlocksManager.recolorTileEntity(tileentity);
	}

	public static void recolorBlock(RenderBlocks renderblocks, int x, int y, int z){
		ColoredBlocksManager.recolorBlock(renderblocks, x, y, z);
	}
}
