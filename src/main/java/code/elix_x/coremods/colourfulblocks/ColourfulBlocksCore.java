package code.elix_x.coremods.colourfulblocks;

import java.io.File;
import java.util.Map;

import code.elix_x.coremods.colourfulblocks.core.ColourfulBlocksTransformer;
import code.elix_x.coremods.colourfulblocks.core.ColourfulBlocksTranslator;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name(value = ColourfulBlocksBase.MODID)
@TransformerExclusions(value = "code.elix_x.coremods")
@MCVersion(value = "1.7.10")
public final class ColourfulBlocksCore implements IFMLLoadingPlugin{

	//-Dfml.coreMods.load=code.elix_x.coremods.colourfullblocks.ColourfullBlocksCore
	
	public static final String Transformer = ColourfulBlocksTransformer.class.getName();
	
	public static final String[] transformers = new String[]{Transformer};
	
	public static File mcDir;
	
	@Override
	public String[] getASMTransformerClass() {
		return transformers;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return ColourfulBlocksTranslator.class.getName();
	}

	@Override
	public void injectData(Map<String, Object> data) {
		mcDir = (File) data.get("mcLocation");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
