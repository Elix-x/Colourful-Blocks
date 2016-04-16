package code.elix_x.coremods.colorfulblocks.color.tool;

import code.elix_x.coremods.colorfulblocks.color.material.ColoringToolMaterial;
import net.minecraft.item.Item;

public interface ColoringToolProvider <T extends Item & IColoringTool> {

	public String getConfigOptionName();

	public String getRecipeType();

	public T provide(ColoringToolMaterial material);

}
