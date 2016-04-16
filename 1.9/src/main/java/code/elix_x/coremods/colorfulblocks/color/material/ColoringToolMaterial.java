package code.elix_x.coremods.colorfulblocks.color.material;

import code.elix_x.excore.utils.color.RGBA;

public class ColoringToolMaterial {

	public String name;
	public int durability;
	public RGBA rgba;
	public double bufferMultiplier;

	public ColoringToolMaterial(String n, int d, RGBA r, double b){
		name = n;
		durability = d;
		rgba = r;
		bufferMultiplier = b;
	}

	public ColoringToolMaterial(String n, int d, int h, double b){
		this(n, d, new RGBA(h), b);
	}

	public int getColor(){
		return rgba.argb();
	}

}
