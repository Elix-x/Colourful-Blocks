package code.elix_x.coremods.colorfulblocks.api.world;

import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.BlockPos;

public interface IColoredBlocksManager {

	public boolean hasRGBA(BlockPos pos);

	public RGBA getRGBA(BlockPos pos);

	public void addRGBA(BlockPos pos, RGBA rgba);

	public void removeRGBA(BlockPos pos);

}
