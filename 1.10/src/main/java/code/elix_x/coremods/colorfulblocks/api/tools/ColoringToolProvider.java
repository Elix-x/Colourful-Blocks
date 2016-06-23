package code.elix_x.coremods.colorfulblocks.api.tools;

import code.elix_x.coremods.colorfulblocks.api.materials.ColoringToolMaterial;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public interface ColoringToolProvider <T extends Item & IColoringTool> {

	public String getConfigOptionName();

	public String getRecipeType();

	public ModelResourceLocation getDefaultModel();

	public T provide(ColoringToolMaterial material);

}
