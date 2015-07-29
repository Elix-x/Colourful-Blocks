package code.elix_x.coremods.colourfulblocks.brush;

import code.elix_x.excore.utils.color.RGBA;

public class BrushMaterial {

	public String name;
	public int durability;
	public RGBA rgba;
	public int hex;
	public int buffer;
	//public ToolMaterial material;

	public BrushMaterial(String n, int d, RGBA r, int b) {
		name = n;
		durability = d;
		rgba = r;
		buffer = b;
	}

	public BrushMaterial(String n, int d, int h, int b) {
		name = n;
		durability = d;
		hex = h;
		buffer = b;
	}

	/*public BrushMaterial(ToolMaterial mat) {
		this(mat.name(), mat.getMaxUses() * 10, null);
		material = mat;
	}*/

	public int getColor() {
		return rgba != null ? rgba.getHex() : /*ColourfullBlocksBase.getColorFromMaterial(material)*/ hex;
	}
}
