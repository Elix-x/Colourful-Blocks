package code.elix_x.coremods.colorfulblocks.api.tools;

import java.util.Collection;

import net.minecraft.item.Item;

public interface IColoringToolsManager {

	public void registerProvider(ColoringToolProvider provider);

	public <I extends Item & IColoringTool> Collection<I> getAllItems(ColoringToolProvider<I> provider);

}
