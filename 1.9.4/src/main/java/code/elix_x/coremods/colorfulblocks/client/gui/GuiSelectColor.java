package code.elix_x.coremods.colorfulblocks.client.gui;

import code.elix_x.coremods.colorfulblocks.ColorfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.net.ColorChangeMessage;
import code.elix_x.excore.utils.client.gui.ColorSelectorGuiScreen;
import code.elix_x.excore.utils.client.gui.elements.ButtonGuiElement;
import code.elix_x.excore.utils.color.RGBA;
import net.minecraft.client.resources.I18n;

public class GuiSelectColor extends ColorSelectorGuiScreen {

	public GuiSelectColor(RGBA color){
		super(null, color);
	}

	@Override
	public void addElements(){
		super.addElements();
		elements.add(new ButtonGuiElement("Done", xPos, nextY, guiWidth, 20, 2, 2, I18n.format("guiselectcolor.button.done")){

			@Override
			public void onButtonPressed(){
				super.onButtonPressed();
				ColorfulBlocksBase.net.sendToServer(new ColorChangeMessage(color));
			}

		});
	}

	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

}
