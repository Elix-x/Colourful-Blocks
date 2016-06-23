package code.elix_x.coremods.colorfulblocks.api.materials;

import code.elix_x.excore.utils.color.RGBA;

public class ColoringToolMaterial {

	public final String name;
	public final int durability;
	public final RGBA rgba;
	public final double bufferMultiplier;

	public ColoringToolMaterial(String n, int d, RGBA r, double b){
		name = n;
		durability = d;
		rgba = r;
		bufferMultiplier = b;
	}

}
