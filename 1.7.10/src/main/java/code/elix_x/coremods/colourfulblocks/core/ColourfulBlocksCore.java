package code.elix_x.coremods.colourfulblocks.core;

import java.util.Map;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.excore.EXCore;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name(ColourfulBlocksBase.MODID)
@TransformerExclusions("code.elix_x.coremods.colourfulblocks.core")
@MCVersion(EXCore.MCVERSION)
@SortingIndex(1001)
public final class ColourfulBlocksCore implements IFMLLoadingPlugin {

	//-Dfml.coreMods.load=code.elix_x.coremods.colourfulblocks.core.ColourfulBlocksCore

	public static final String Transformer = "code.elix_x.coremods.colourfulblocks.core.ColourfulBlocksTransformer";

	public static final String[] transformers = new String[]{Transformer};

	@Override
	public String[] getASMTransformerClass(){
		return transformers;
	}

	@Override
	public String getModContainerClass(){
		return null;
	}

	@Override
	public String getSetupClass(){
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data){

	}

	@Override
	public String getAccessTransformerClass(){
		return null;
	}

}
