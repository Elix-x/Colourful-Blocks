package code.elix_x.coremods.colourfulblocks.gui;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.net.ColorChangeMessage;
import code.elix_x.excore.utils.color.RGBA;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiSelectColor extends GuiScreen {

	private ResourceLocation texture = new ResourceLocation(ColourfulBlocksBase.MODID + ":textures/gui/brush.png");

	protected int xSize;
	protected int ySize;
	protected int guiLeft;
	protected int guiTop;

	private GuiButton randomB;
	private GuiButton done;

	private GuiTextField rT;
	private GuiTextField gT;
	private GuiTextField bT;

	private Random random;

	private Block block;

	public float r;
	public float g;
	public float b;

	public GuiSelectColor(RGBA rgba){
		xSize = 248;
		ySize = 166;

		r = rgba.getRF();
		g = rgba.getGF();
		b = rgba.getBF();

		random = new Random();

		fixBlock();
	}

	@Override
	public void initGui(){
		super.initGui();

		Keyboard.enableRepeatEvents(true);

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		buttonList.clear();

		buttonList.add(randomB = new GuiButton(0, guiLeft + 7, guiTop + ySize - 3 - 4 - 16, 64, 16, StatCollector.translateToLocal("guiselectcolor.button.random")));
		buttonList.add(done = new GuiButton(1, guiLeft + xSize - 7 - 64, guiTop + ySize - 3 - 4 - 16, 64, 16, StatCollector.translateToLocal("guiselectcolor.button.done")));

		GuiTextField oldRt = rT;
		rT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 64 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4, 64, 16);
		if(oldRt != null){
			rT.setFocused(oldRt.isFocused());
			rT.setText(oldRt.getText());
		} else {
			rT.setText("" + r);
		}

		GuiTextField oldGt = gT;
		gT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 64 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4 + 30, 64, 16);
		if(oldGt != null){
			gT.setFocused(oldGt.isFocused());
			gT.setText(oldGt.getText());
		} else {
			gT.setText("" + g);
		}

		GuiTextField oldBt = bT;
		bT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 64 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4 + 30 + 30, 64, 16);
		if(oldBt != null){
			bT.setFocused(oldBt.isFocused());
			bT.setText(oldBt.getText());
		} else {
			bT.setText("" + b);
		}
	}

	public void reInitGui(){
		done.enabled = true;

		try {
			r = Float.parseFloat(rT.getText());
		} catch(NumberFormatException e){
			r = 0;
			done.enabled = false;
		}
		if(r < 0 || r > 1) done.enabled = false;

		try {
			g = Float.parseFloat(gT.getText());
		} catch(NumberFormatException e){
			g = 0;
			done.enabled = false;
		}
		if(g < 0 || g > 1) done.enabled = false;

		try {
			b = Float.parseFloat(bT.getText());
		} catch(NumberFormatException e){
			b = 0;
			done.enabled = false;
		}
		if(b < 0 || b > 1) done.enabled = false;

		initGui();
	}

	@Override
	public void drawScreen(int i, int j, float f){
		super.drawScreen(i, j, f);

		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		this.drawTexturedModalRect(guiLeft + xSize / 2 - 218 / 2, guiTop + ySize - 3 - 4 - 16 - 90, 0, 170, 218, 66);

		this.drawTexturedModalRect(guiLeft + xSize / 2 - 218 / 2 + (int) (r * 218), guiTop + ySize - 3 - 4 - 16 - 90 - 1, 221, 199, 6, 8);
		this.drawTexturedModalRect(guiLeft + xSize / 2 - 218 / 2 + (int) (g * 218), guiTop + ySize - 3 - 4 - 16 - 60 - 1, 221, 199, 6, 8);
		this.drawTexturedModalRect(guiLeft + xSize / 2 - 218 / 2 + (int) (b * 218), guiTop + ySize - 3 - 4 - 16 - 30 - 1, 221, 199, 6, 8);

		rT.drawTextBox();
		gT.drawTextBox();
		bT.drawTextBox();

		randomB.drawButton(mc, i, j);
		done.drawButton(mc, i, j);

		try {
			fixBlock();
			GL11.glPushMatrix();
			GL11.glScaled(3, 3, 3);
			CustomItemRenderer.renderItemIntoGUI(fontRendererObj, Minecraft.getMinecraft().getTextureManager(), new ItemStack(block), (guiLeft + xSize / 2) / 3 - 8, (guiTop + (ySize - 3 - 4 - 16 - 90) / 2) / 3 - 8, itemRender, new RGBA(r, g, b));
			GL11.glPopMatrix();
		} catch(NullPointerException e){
			GL11.glPopMatrix();
		}
	}

	private void fixBlock(){
		while(block == null || block == Blocks.air){
			block = block.getBlockById(random.nextInt(20000));
		}
		return;
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button == randomB){
			r = random.nextFloat();
			g = random.nextFloat();
			b = random.nextFloat();
		}
		if(button == done){
			ColourfulBlocksBase.net.sendToServer(new ColorChangeMessage(new RGBA(r, g, b)));
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button){
		super.mouseClicked(x, y, button);
		rT.mouseClicked(x, y, button);
		gT.mouseClicked(x, y, button);
		bT.mouseClicked(x, y, button);

		{
			int i = guiLeft + xSize / 2 - 218 / 2;
			int j = guiTop + ySize - 3 - 4 - 16 - 90;
			if(isCoordBetweenInclusive(x, y, i,  i + 218, j, j + 4)){
				r = (x - i) / 218f;
				rT.setText("" + r);
				initGui();
			}
		}
		{
			int i = guiLeft + xSize / 2 - 218 / 2;
			int j = guiTop + ySize - 3 - 4 - 16 - 60;
			if(isCoordBetweenInclusive(x, y, i, i + 218, j, j + 4)){
				g = (x - i) / 218f;
				gT.setText("" + g);
				initGui();
			}
		}
		{
			int i = guiLeft + xSize / 2 - 218 / 2;
			int j = guiTop + ySize - 3 - 4 - 16 - 30;
			if(isCoordBetweenInclusive(x, y, i, i + 218, j, j + 4)){
				b = (x - i) / 218f;
				bT.setText("" + b);
				initGui();
			}
		}
	}

	/**
	 * 
	 * @param a = value to check
	 * @param i = frontier 1
	 * @param j = frontier 2
	 * @return if a is between a and b exclusively (may not be on i or j)
	 */
	public static boolean isPointBetween(int a, int i, int j){
		return (i < a && a < j) ||(j < a && a < i);
	}

	/**
	 * 
	 * @param a = value to check
	 * @param i = frontier 1
	 * @param j = frontier 2
	 * @return if a is between a and b inclusively (may be on i or j)
	 */
	public static boolean isPointBetweenInclusive(int a, int i, int j){
		return (i <= a && a <= j) || (j <= a && a <= i);
	}

	/**
	 * 
	 * @param a = first value to check
	 * @param b = second value to check
	 * @param i = frontier 1 for first value
	 * @param j = frontier 2 for first value
	 * @param x = frontier 1 for second value
	 * @param y = frontier 2 for second value
	 * @return if a is between i and j and b is between x and y
	 */
	public static boolean isCoordBetween(int a, int b, int i, int j, int x, int y){
		return isPointBetween(a, i, j) && isPointBetween(b, x, y);
	}

	/**
	 * 
	 * @param a = first value to check
	 * @param b = second value to check
	 * @param i = frontier 1 for first value
	 * @param j = frontier 2 for first value
	 * @param x = frontier 1 for second value
	 * @param y = frontier 2 for second value
	 * @return if a is between i and j inclusively and b is between x and y inclusively
	 */
	public static boolean isCoordBetweenInclusive(int a, int b, int i, int j, int x, int y){
		return isPointBetweenInclusive(a, i, j) && isPointBetweenInclusive(b, x, y);
	}

	@Override
	protected void keyTyped(char c, int keyID){
		super.keyTyped(c, keyID);
		if(rT.textboxKeyTyped(c, keyID) || gT.textboxKeyTyped(c, keyID) || bT.textboxKeyTyped(c, keyID)){
			reInitGui();
		}
	}

	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int which){
		super.mouseMovedOrUp(x, y, which);
	}

}
