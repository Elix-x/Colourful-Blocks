package code.elix_x.coremods.colorfulblocks.client.color;

import code.elix_x.coremods.colorfulblocks.color.tool.IColoringTool;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class StandartColoringToolItemColor implements IItemColor {

	public int materialTintIndex;
	public int currentColorTintIndex;

	public StandartColoringToolItemColor(int materialTintIndex, int currentColorTintIndex){
		this.materialTintIndex = materialTintIndex;
		this.currentColorTintIndex = currentColorTintIndex;
	}

	@Override
	public int getColorFromItemstack(ItemStack itemstack, int tintIndex){
		return tintIndex == materialTintIndex ? ((IColoringTool) itemstack.getItem()).getMaterial().rgba.argb() : tintIndex == currentColorTintIndex ? ((IColoringTool) itemstack.getItem()).getCurrentColor(itemstack).argb() : -1;
	}

}
