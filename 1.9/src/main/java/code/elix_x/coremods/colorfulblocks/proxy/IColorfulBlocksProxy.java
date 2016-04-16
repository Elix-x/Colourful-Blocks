package code.elix_x.coremods.colorfulblocks.proxy;

import code.elix_x.excore.utils.proxy.IProxy;
import net.minecraft.item.ItemStack;

public interface IColorfulBlocksProxy extends IProxy {

	public void displayGuiSelectColor(ItemStack coloringTool);

}
