package code.elix_x.coremods.colourfulblocks.gui;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.net.ColorChangeMessage;
import code.elix_x.excore.utils.color.RGBA;

public class GuiSelectColor extends GuiScreen{

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


	public int r;
	public int g;
	public int b;

	public GuiSelectColor(RGBA rgba) {
		xSize = 248;
		ySize = 166;

		r = rgba.r;
		g = rgba.g;
		b = rgba.b;

		random = new Random();

		fixBlock();
	}

	@Override
	public void initGui() {
		super.initGui();

		Keyboard.enableRepeatEvents(true);

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		randomB = new GuiButton(0, guiLeft + 7, guiTop + ySize - 3 - 4 - 16, 64, 16, StatCollector.translateToLocal("guiselectcolor.button.random"));
		done = new GuiButton(1, guiLeft + xSize - 7 - 64, guiTop + ySize - 3 - 4 - 16, 64, 16, StatCollector.translateToLocal("guiselectcolor.button.done"));

		buttonList.clear();
		buttonList.add(randomB);
		buttonList.add(done);

		rT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 32 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4, 32, 16);
		gT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 32 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4 + 30, 32, 16);
		bT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 32 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4 + 30 + 30, 32, 16);

		rT.setText("" + r);
		gT.setText("" + g);
		bT.setText("" + b);
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		super.drawScreen(i, j, f);

		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		this.drawTexturedModalRect(guiLeft + xSize / 2 - 218 / 2, guiTop + ySize - 3 - 4 - 16 - 90, 0, 170, 218, 66);

		this.drawTexturedModalRect(guiLeft + xSize / 2 - 218 / 2 + (r * 218 / 255), guiTop + ySize - 3 - 4 - 16 - 90 - 1, 221, 199, 6, 8);
		this.drawTexturedModalRect(guiLeft + xSize / 2 - 218 / 2 + (g * 218 / 255), guiTop + ySize - 3 - 4 - 16 - 60 - 1, 221, 199, 6, 8);
		this.drawTexturedModalRect(guiLeft + xSize / 2 - 218 / 2 + (b * 218 / 255), guiTop + ySize - 3 - 4 - 16 - 30 - 1, 221, 199, 6, 8);

		rT.drawTextBox();
		gT.drawTextBox();
		bT.drawTextBox();

		randomB.drawButton(mc, i, j);
		done.drawButton(mc, i, j);


		try{
			fixBlock();
			GL11.glPushMatrix();
			GL11.glScaled(3, 3, 3);
			CustomItemRenderer.renderItemIntoGUI(fontRendererObj, Minecraft.getMinecraft().getTextureManager(), new ItemStack(block), (guiLeft + xSize / 2) / 3 - 8, (guiTop + (ySize - 3 - 4 - 16 - 90) / 2) / 3 - 8, itemRender, new RGBA(r, g, b, 255));
			GL11.glPopMatrix();
		} catch(NullPointerException e){
			GL11.glPopMatrix();
		}
	}

	private void fixBlock(){
		if(block == null){
			while(true){
				block = block.getBlockById(random.nextInt(20000));
				if(block != null && block != Blocks.air){
					return;
				}
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == randomB){
			r = random.nextInt(255);
			g = random.nextInt(255);
			b = random.nextInt(255);
		}
		if(button == done){
			ColourfulBlocksBase.net.sendToServer(new ColorChangeMessage(new RGBA(r, g, b)));
			Minecraft.getMinecraft().displayGuiScreen(null);
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		rT.mouseClicked(x, y, button);
		gT.mouseClicked(x, y, button);
		bT.mouseClicked(x, y, button);

		{
			int i = guiLeft + xSize / 2 - 218 / 2;
			int j = guiTop + ySize - 3 - 4 - 16 - 90;

			if(isCoordBetweenInclusive(x, y, i,  i + 218, j, j + 4)){
				int d = x - i;
				int sd = d * 255 / 218;
				r = sd;
				rT.setText("" + r);
			}
		}
		{
			int i = guiLeft + xSize / 2 - 218 / 2;
			int j = guiTop + ySize - 3 - 4 - 16 - 60;
			if(isCoordBetweenInclusive(x, y, i, i + 218, j, j + 4)){
				int d = x - i;
				int sd = d * 255 / 218;
				g = sd;
				gT.setText("" + g);
			}
		}
		{
			int i = guiLeft + xSize / 2 - 218 / 2;
			int j = guiTop + ySize - 3 - 4 - 16 - 30;
			if(isCoordBetweenInclusive(x, y, i, i + 218, j, j + 4)){
				int d = x - i;
				int sd = d * 255 / 218;
				b = sd;
				bT.setText("" + b);
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
		/*	System.out.println("a: " + a);
		System.out.println("i: " + i);
		System.out.println("j: " + j);*/
		/*System.out.println("b: " + b);
		System.out.println("x: " + x);
		System.out.println("y: " + y);*/
		return isPointBetweenInclusive(a, i, j) && isPointBetweenInclusive(b, x, y);
	}

	@Override
	protected void keyTyped(char c, int keyID) {
		super.keyTyped(c, keyID);
		if(Character.isDigit(c) || keyID == Keyboard.KEY_BACK){
			if(rT.textboxKeyTyped(c, keyID)){
				if(!rT.getText().equals("")){
					r = Integer.parseInt(rT.getText());
				} else {
					r = 0;
				}
				if(r < 0){
					r = 0;
				}
				if (r > 255){
					r = 255;
				}
				rT.setText("" + r);
			}
			if(gT.textboxKeyTyped(c, keyID)){
				if(!gT.getText().equals("")){
					g = Integer.parseInt(gT.getText());
				} else {
					g = 0;
				}
				if(g < 0){
					g = 0;
				}
				if (g > 255){
					g = 255;
				}
				gT.setText("" + g);
			}
			if(bT.textboxKeyTyped(c, keyID)){
				if(!bT.getText().equals("")){
					b = Integer.parseInt(bT.getText());
				} else {
					b = 0;
				}
				if(b < 0){
					b = 0;
				}
				if (b > 255){
					b = 255;
				}
				bT.setText("" + b);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public void onGuiClosed()
	{
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int which) {
		super.mouseMovedOrUp(x, y, which);
	}

}
