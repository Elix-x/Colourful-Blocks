package code.elix_x.coremods.colorfulblocks.core;

import java.util.Map;

import code.elix_x.coremods.colorfulblocks.ColourfulBlocksBase;
import code.elix_x.excore.EXCore;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name(ColourfulBlocksBase.MODID)
@TransformerExclusions("code.elix_x.coremods.colorfulblocks.core")
@MCVersion(EXCore.MCVERSION)
@SortingIndex(1001)
public final class ColorfulBlocksCore implements IFMLLoadingPlugin {

	//-Dfml.coreMods.load=code.elix_x.coremods.colorfulblocks.core.ColorfulBlocksCore

	public static final String Transformer = "code.elix_x.coremods.colorfulblocks.core.ColorfulBlocksTransformer";

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
