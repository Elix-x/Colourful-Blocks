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
import code.elix_x.coremods.colourfulblocks.net.ColourChangeMessage;
import code.elix_x.excore.utils.color.RGBA;

public class GuiBrush extends GuiScreen{

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

	/*private GuiOptionSliderCustom rS;
	private GuiOptionSliderCustom gS;
	private GuiOptionSliderCustom bS;*/

	private Random random;

	private Block block;


	public int r;
	public int g;
	public int b;

	public GuiBrush(RGBA rgba) {
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

		randomB = new GuiButton(0, guiLeft + 7, guiTop + ySize - 3 - 4 - 16, 64, 16, StatCollector.translateToLocal("guibrush.button.random"));
		done = new GuiButton(1, guiLeft + xSize - 7 - 64, guiTop + ySize - 3 - 4 - 16, 64, 16, StatCollector.translateToLocal("guibrush.button.done"));

		buttonList.clear();
		buttonList.add(randomB);
		buttonList.add(done);

		rT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 32 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4, 32, 16);
		gT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 32 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4 + 30, 32, 16);
		bT = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 32 / 2, guiTop + ySize - 3 - 4 - 16 - 90 + 6 + 4 + 30 + 30, 32, 16);

		rT.setText("" + r);
		gT.setText("" + g);
		bT.setText("" + b);

		/*rS = new GuiOptionSliderCustom(10, guiLeft + xSize / 2 - 218 / 2, guiTop + ySize - 3 - 4 - 16 - 90, EnumColorListener.R);
		gS = new GuiOptionSliderCustom(10, guiLeft + xSize / 2 - 218 / 2, guiTop + ySize - 3 - 4 - 16 - 90, EnumColorListener.G);
		bS = new GuiOptionSliderCustom(10, guiLeft + xSize / 2 - 218 / 2, guiTop + ySize - 3 - 4 - 16 - 90, EnumColorListener.B);*/
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		super.drawScreen(i, j, f);
		//		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

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
			//			GL11.glColor3d(r, g, b);
			//			GL11.glColor3d(150, 0, 150);
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
			ColourfulBlocksBase.net.sendToServer(new ColourChangeMessage(new RGBA(r, g, b, 100), Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld.provider.dimensionId));
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
		/*	System.out.println("a: " + a);
		System.out.println("i: " + i);
		System.out.println("j: " + j);
		System.out.println("result: " + ((i <= a && a <= j) || (j <= a && a <= i)));*/
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
		/*String s = String.valueOf(c);
		try{
			int i = Integer.parseInt(s);
		} catch(NumberFormatException e){
			return;
		}*/
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
		/*if(which == 0){

		}*/
	}

	public class GuiOptionSliderCustom extends GuiButton
	{
		private float currentPercent;
		public boolean held;
		/*private final float field_146132_r;
		private final float field_146131_s;*/
		private EnumColorListener color;

		public GuiOptionSliderCustom(int id, int x, int y, EnumColorListener e)
		{
			super(id, x, y, 255, 4, "");

		}

		/*public GuiOptionSliderCustom(int id, int x, int y/*, float p_i45017_5_, float p_i45017_6_)
		{
//			super(id, x, y, 150, 20, "");
			super(id, x, y, 255, 4, "");
			this.field_146134_p = 1.0F;

		}*/

		/**
		 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
		 * this button.
		 */
		public int getHoverState(boolean p_146114_1_)
		{
			return 0;
		}

		/**
		 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
		 */
		protected void mouseDragged(Minecraft p_146119_1_, int p_146119_2_, int p_146119_3_)
		{
			if (this.visible)
			{
				if (this.held)
				{
					this.currentPercent = (float)(p_146119_2_ - (this.xPosition + 4)) / (float)(this.width - 8);

					if (this.currentPercent < 0.0F)
					{
						this.currentPercent = 0.0F;
					}

					if (this.currentPercent > 1.0F)
					{
						this.currentPercent = 1.0F;
					}

					/*float f = this.field_146133_q.denormalizeValue(this.field_146134_p);
					p_146119_1_.gameSettings.setOptionFloatValue(this.field_146133_q, f);
					this.field_146134_p = this.field_146133_q.normalizeValue(f);
					this.displayString = p_146119_1_.gameSettings.getKeyBinding(this.field_146133_q);*/
					if(color == EnumColorListener.R){
						r = (int) (currentPercent * 255f);
					}
					if(color == EnumColorListener.G){
						g = (int) (currentPercent * 255f);
					}
					if(color == EnumColorListener.B){
						b = (int) (currentPercent * 255f);
					}
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.drawTexturedModalRect(this.xPosition + (int)(this.currentPercent * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
				this.drawTexturedModalRect(this.xPosition + (int)(this.currentPercent * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
			}
		}

		/**
		 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
		 * e).
		 */
		public boolean mousePressed(Minecraft minecraft, int x, int y)
		{
			if (super.mousePressed(minecraft, x, y))
			{
				this.currentPercent = (float)(x - (this.xPosition + 4)) / (float)(this.width - 8);

				if (this.currentPercent < 0.0F)
				{
					this.currentPercent = 0.0F;
				}

				if (this.currentPercent > 1.0F)
				{
					this.currentPercent = 1.0F;
				}

				/*minecraft.gameSettings.setOptionFloatValue(this.field_146133_q, this.field_146133_q.denormalizeValue(this.field_146134_p));
				this.displayString = minecraft.gameSettings.getKeyBinding(this.field_146133_q);*/
				if(color == EnumColorListener.R){
					r = (int) (currentPercent * 255f);
				}
				if(color == EnumColorListener.G){
					g = (int) (currentPercent * 255f);
				}
				if(color == EnumColorListener.B){
					b = (int) (currentPercent * 255f);
				}
				this.held = true;
				return true;
			}
			else
			{
				return false;
			}
		}

		/**
		 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
		 */
		public void mouseReleased(int x, int y)
		{
			this.held = false;
		}
	}

	public enum EnumColorListener {
		R, G, B;
	}

}
