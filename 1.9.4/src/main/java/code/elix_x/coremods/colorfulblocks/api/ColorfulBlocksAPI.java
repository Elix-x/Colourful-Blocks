package code.elix_x.coremods.colorfulblocks.api;

import code.elix_x.coremods.colorfulblocks.api.tools.IColoringToolsManager;
import code.elix_x.coremods.colorfulblocks.api.world.IColoredBlocksManager;
import net.minecraft.world.World;

public interface ColorfulBlocksAPI {

	public static final String RECIPENAMENULL = "NULL";
	public static final String RECIPENAMEVANILLA = "VANILLA";
	public static final String RECIPETYPEBRUSH = "BRUSH";
	public static final String RECIPEENTRYMATERIAL = "<MATERIAL>";
	public static final String COLORINGTOOLLANG = "item.coloringtool";
	public static final String COLORINGTOOLNAMEORDERLANG = COLORINGTOOLLANG + ".nameorder";
	public static final String COLORINGTOOLMATERIALLANG = "coloringtoolmaterial";

	public static final ColorfulBlocksAPI INSTANCE = null;

	public boolean consumeWaterOnPaint();

	public IColoringToolsManager getColoringToolsManager();

	public IColoredBlocksManager getColoredBlocksManager(World world);

}
