package code.elix_x.coremods.colorfulblocks.client.color;

import code.elix_x.coremods.colorfulblocks.color.ColoredBlocksManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ColorfulBlocksBlockColor implements IBlockColor {

	private IBlockColor parent;

	public ColorfulBlocksBlockColor(IBlockColor parent){
		this.parent = parent;
	}

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tintIndex){
		return world != null && pos != null ? ColoredBlocksManager.getBlockColor(world, state, new code.elix_x.excore.utils.pos.BlockPos(pos), parent != null && tintIndex != Integer.MAX_VALUE ? parent.colorMultiplier(state, world, pos, tintIndex) : -1) : parent != null ? parent.colorMultiplier(state, world, pos, tintIndex) : -1;
	}

}
